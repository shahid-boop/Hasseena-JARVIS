# Hasseena — Android JARVIS-style AI Assistant

Hasseena is an Android-native adaptation inspired by the architecture and feature ideas found in the supplied Mark-LV project. The original Mark-LV source is preserved under `marklv-reference/` for reference; it is **not copied into the Android runtime** because its PyQt6/desktop-control stack is not Android-compatible.

## What is included
- Android Studio Gradle project
- Kotlin Android app
- Female/device TTS support
- Android SpeechRecognizer voice input
- Gemini REST client using Android platform networking
- Local preferences for API key, model and memory
- Chat UI with Hasseena branding
- Runtime permission handling for microphone
- Reference copy of the supplied Mark-LV project
- Architecture and verification notes

## Build
1. Open this folder in Android Studio.
2. Allow Gradle to download the Android/Kotlin dependencies.
3. Use JDK 17.
4. Sync Gradle.
5. Run on an Android 8.0+ device/emulator.
6. Open **Settings** inside Hasseena and add your Gemini API key.

## Important
The API key is stored locally in app preferences for this starter build. For a production app, move model calls behind your own authenticated backend so a client-side key is not exposed.

The phone's installed TTS engine controls which female voices are available. Hasseena requests an English female voice when the engine exposes one; otherwise it uses the device default voice.

## Hasseena Branding

The Android project now includes the Hasseena visual identity:
- `app/src/main/res/drawable-nodpi/hasseena_icon.png` — launcher icon artwork.
- `app/src/main/res/drawable-nodpi/hasseena_splash.png` — main startup splash artwork.
- `app/src/main/res/drawable-nodpi/hasseena_loading.png` — alternate loading artwork kept for future loading-state use.
- `SplashActivity.kt` — startup screen before the main assistant UI.
- Android 12+ `SplashTheme` support in `values-v31/styles.xml`.

The launcher entry point is `SplashActivity`; it opens `MainActivity` after the branded splash.
