"""End-to-end QA pipeline automating:
1. Gap resolution from Docs/prd, Docs/psd, Docs/tests, and owning source files
2. Stage 1-3 QA artifacts (test-model.md, test-suite.md, coverage-report.md) via qa-architect
3. Stage 4 Robolectric/Compose automation + Page Objects + Gradle test & fix loop
4. wiki/lint.py verification and optional git commit & push.
"""

from datetime import date

slugs = args["slugs"]
run_date = args.get("run_date") or date.today().isoformat()
priorities = args.get("priorities", "p0,p1,p2")
resolve_gaps = args.get("resolve_gaps", True)
commit_and_push = args.get("commit_and_push", False)


async def process_feature(slug: str) -> dict:
    artifact_dir = f"qa-artifacts/{run_date}-{slug}"

    # 1. Read PRD and optionally resolve gaps from PSD, tests doc, and source code
    gap_context = await agent(
        prompt=(
            f"Read `Docs/prd/{slug}.md`.\n"
            + (
                f"Also read `Docs/psd/{slug}.md`, `Docs/tests/{slug}.md`, and the owning Kotlin source files "
                f"cited in those docs to resolve all requirement ambiguities, field types, defaults, edge cases, "
                f"state transitions, and UI testTags.\n"
                if resolve_gaps
                else "Do NOT read external docs; return only the verbatim PRD text and note that gaps remain open.\n"
            )
            + "Return the verbatim PRD text plus a concise, factual Gap Resolution Summary."
        ),
        schema={
            "type": "object",
            "required": ["prd_text", "gap_resolutions"],
            "properties": {
                "prd_text": {"type": "string"},
                "gap_resolutions": {"type": "string"},
            },
        },
        role=f"Groom {slug}",
        type_name="research",
    )
    log(f"[{slug}] PRD & gap context prepared")

    # 2. Generate test-model.md, test-suite.md, and coverage-report.md via qa-architect
    qa_bundle = await agent(
        prompt=(
            "Read `/Users/sai/.gemini/config/plugins/qa/skills/test-design-techniques/SKILL.md`, "
            "`/Users/sai/.gemini/config/plugins/qa/skills/risk-scoring/SKILL.md`, and "
            "`/Users/sai/.gemini/config/plugins/qa/skills/test-artifact-schema/SKILL.md`.\n\n"
            f"Produce the complete Stage 1-3 QA artifacts for `{slug}` (`{artifact_dir}/`) using the "
            "pasted PRD and resolved gap context below. Include `qa-summary` footers in all three files.\n\n"
            f"=== PRD ===\n{gap_context['prd_text']}\n\n"
            f"=== RESOLVED GAPS ===\n{gap_context['gap_resolutions']}"
        ),
        schema={
            "type": "object",
            "required": ["test_model_md", "test_suite_md", "coverage_report_md"],
            "properties": {
                "test_model_md": {"type": "string"},
                "test_suite_md": {"type": "string"},
                "coverage_report_md": {"type": "string"},
            },
        },
        role=f"QA Architect {slug}",
        type_name="qa-architect",
    )

    # Persist QA artifacts & automate tests + verify in an isolated workspace share
    result = await agent(
        prompt=(
            f"For feature `{slug}`:\n"
            f"1. Write `{artifact_dir}/test-model.md`, `{artifact_dir}/test-suite.md`, and "
            f"`{artifact_dir}/coverage-report.md` using these exact contents:\n"
            f"--- test-model.md ---\n{qa_bundle['test_model_md']}\n"
            f"--- test-suite.md ---\n{qa_bundle['test_suite_md']}\n"
            f"--- coverage-report.md ---\n{qa_bundle['coverage_report_md']}\n\n"
            f"2. Automate `{priorities}` cases from `{artifact_dir}/test-suite.md` under "
            f"`app/src/test/java/com/example/qa/` with a Page Object / Driver and an independent `@Test` per case ID "
            f"(strict AAA, `Room` isolation, Robolectric + `createComposeRule()`). "
            f"IMPORTANT Robolectric rules: (a) run `repository.ensureInitialDataLoaded()` on `Dispatchers.IO` BEFORE "
            f"constructing `LanguageLearnerViewModel(app)` (never `runBlocking` on `allLessons.first` on the main thread); "
            f"(b) do NOT import `androidx.test.espresso.Espresso` (use `ShadowDialog.getLatestDialog()?.onBackPressed()` for dialog back); "
            f"(c) pump `ShadowLooper.idleMainLooper()` inside `waitUntil` when waiting on `ViewModel` `StateFlow` updates from Room.\n"
            f"3. Run `./gradlew testDebugUnitTest --tests \"com.example.qa.*\"` (only the new test class, using `BypassSandbox: true`) and fix any "
            f"test wiring or spec-vs-code defects without weakening assertions until all new tests pass. Always call `finish` when done.\n"
            f"4. Return the list of created/modified files and the exact test count summary."
        ),
        schema={
            "type": "object",
            "required": ["slug", "test_class", "files", "tests_passed", "tests_failed"],
            "properties": {
                "slug": {"type": "string"},
                "test_class": {"type": "string"},
                "files": {"type": "array", "items": {"type": "string"}},
                "tests_passed": {"type": "integer"},
                "tests_failed": {"type": "integer"},
            },
        },
        role=f"Automate {slug}",
        type_name="self",
    )
    log(f"[{slug}] {result['test_class']}: {result['tests_passed']} passed, {result['tests_failed']} failed")
    return result


phase("Feature QA Pipelines")
feature_results = await pipeline(slugs, process_feature)

phase("Wiki Lint & Final Verification")
final_status = await agent(
    prompt=(
        "1. Run `python3 wiki/lint.py` and confirm it exits with code 0.\n"
        "2. Run `./gradlew testDebugUnitTest --tests \"com.example.qa.*\"` to verify all QA tests pass together.\n"
        + (
            "3. Stage all changes (`git add -A`), commit with a concise message, and run `git push origin main`.\n"
            if commit_and_push
            else "3. Do NOT commit or push (`commit_and_push=false`).\n"
        )
        + f"Feature results: {feature_results}"
    ),
    schema={"type": "string"},
    role="Verify & Finalize",
    type_name="self",
)
log(final_status)
final_status
