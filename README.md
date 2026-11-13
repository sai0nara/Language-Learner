# Language Learner

Offline-first Android language learning app with custom XML/CSV/JSON curriculum import, SM-2 spaced repetition, 3x3 grammar matrices, and dual-audio pronunciation playback.

## Build & Test

Gradle is pinned to **JDK 25** via `gradle.properties` (`org.gradle.java.home`).


# Allure report
```shell
./gradlew :app:allureServe
```

# Execute tests
```shell
 ./gradlew connectedAndroidTest
```

# Build apk
```shell
./gradlew assembleDist
```

# Install apk
```shell
 ./gradlew installDebug
```
