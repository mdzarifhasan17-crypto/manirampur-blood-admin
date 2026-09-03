# Admin build

1. Push this folder to a GitHub repository (the files app/, gradle/, build.gradle.kts, settings.gradle.kts must be at repository root).
2. Connect the repository to Codemagic.
3. Select workflow `android-debug`.
4. Build.
5. Download the APK artifact from `app/build/outputs/apk/debug/`.

Firebase Android package is `com.zarif.bloodbank.admin`.
