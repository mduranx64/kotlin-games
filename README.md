<p align="center">
  <img src="media/header.webp" alt="Kotlin Games — a collection of classic board games for Android" width="100%" />
</p>

<p align="center">
  <img src="media/icon.webp" alt="Kotlin Games app icon" width="112" />
</p>

<h1 align="center">Kotlin Games</h1>

<p align="center">
  A native Android collection of classic board games, built from the ground up with Kotlin and Jetpack Compose.
</p>

<p align="center">
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 1.9.0" /></a>
  <a href="https://developer.android.com/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose and Material 3" /></a>
  <img src="https://img.shields.io/badge/Android-9.0%2B-3DDC84?logo=android&logoColor=white" alt="Android 9.0 or newer" />
  <a href="https://github.com/mduranx64/kotlin-games/actions/workflows/android.yml"><img src="https://github.com/mduranx64/kotlin-games/actions/workflows/android.yml/badge.svg" alt="Android CI/CD status" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="MIT license" /></a>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.mduranx64.kotlingames&hl=en">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80" />
  </a>
</p>

## About the project

Kotlin Games is an open-source Android app that turns classic tabletop games into polished native mobile experiences. The first available game is local two-player Chess, backed by a custom rules engine and a responsive Compose interface that works in portrait and landscape orientations.

The project is also a practical showcase of modern Android development: declarative UI, lifecycle-aware state, accessible interactions, adaptive layouts, custom domain logic, and automated Play Store delivery. The app runs entirely on-device and does not collect user data.

## Gameplay

<p align="center">
  <img src="media/gameplay-demo.gif" alt="A local two-player chess match in Kotlin Games" width="320" />
</p>

## Engineering highlights

- **Compose-first UI** — Material 3 components, reusable composables, previews, light/dark themes, and edge-to-edge system-bar handling.
- **Adaptive game board** — dedicated portrait and landscape arrangements sized from the available screen dimensions.
- **Custom chess engine** — turn tracking, piece-specific move validation, captures, castling, en passant, and pawn promotion implemented in Kotlin.
- **Observable state** — a lifecycle-aware `ViewModel` exposes Compose state so board updates immediately drive the interface.
- **Accessible interaction** — semantic descriptions identify pieces, colors, board coordinates, and empty squares for assistive technology.
- **Production delivery** — Gradle release builds, Fastlane lanes, signed artifacts, and GitHub Actions deployment to Google Play.

## Architecture

The UI observes a single game state and sends board selections back to the domain layer. Navigation and Android lifecycle concerns remain outside the chess rules engine.

```mermaid
flowchart LR
    A[Activities] --> N[Navigation Compose]
    N --> M[Game library]
    N --> C[Chess screen]
    C --> V[ChessViewModel]
    V --> B[Board rules and state]
    B --> V
    V --> C
    T[Material 3 theme] --> M
    T --> C
```

## Built with

| Area | Technology |
| --- | --- |
| Language | Kotlin 1.9 |
| UI | Jetpack Compose, Material 3 |
| State | AndroidX Lifecycle ViewModel, Compose state |
| Navigation | Navigation Compose |
| Build | Gradle Kotlin DSL, version catalogs |
| Delivery | GitHub Actions, Fastlane, Google Play |
| Compatibility | Android 9 (API 28) and newer |

## Screenshots

<p align="center">
  <img src="media/splash-screen.jpeg" alt="Kotlin Games splash screen" width="22%" />
  <img src="media/game-library.jpeg" alt="Kotlin Games library showing Chess" width="22%" />
  <img src="media/chess-new-game.jpeg" alt="A new two-player chess game" width="22%" />
  <img src="media/chess-midgame.jpeg" alt="An active two-player chess match" width="22%" />
</p>

## Run locally

### Requirements

- Android Studio with Android SDK 35
- JDK 17
- An emulator or physical device running Android 9 or newer

### Build

```bash
git clone https://github.com/mduranx64/kotlin-games.git
cd kotlin-games/KotlinGames
./gradlew assembleDebug
```

Open the `KotlinGames` directory in Android Studio to run and debug the app interactively. Release builds require the signing environment variables configured by the CI workflow; they are not needed for local debug builds.

## Roadmap

- Add more classic board games to the library
- Expand chess end-state handling with check, checkmate, and stalemate detection
- Grow unit and Compose UI coverage around the game engine and user flows

## Credits

Chess artwork is based on assets from [OpenGameArt](https://opengameart.org/content/chess-pieces-and-board-squares).

## License

Kotlin Games is available under the [MIT License](LICENSE).

---

<p align="center">
  Built by <a href="https://github.com/mduranx64">Miguel Duran</a> — explore the code, try the app, or get in touch on GitHub.
</p>
