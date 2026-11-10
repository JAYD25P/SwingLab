# Building SwingLab

## Requirements

- Android Studio (current stable)
- JDK 17
- Android SDK Platform 35
- Gradle 8.9 (matches Android Gradle Plugin 8.7.3)

## Decode the project archive

From the repository root:

```bash
base64 --decode SwingLab-Android-Source.zip.b64 > SwingLab-Android-Source.zip
unzip -q SwingLab-Android-Source.zip -d SwingLab-project
```

Open the `SwingLab-project` folder in Android Studio.

## Build from a terminal

With Gradle 8.9 installed and Android SDK configured:

```bash
gradle --no-daemon -p SwingLab-project assembleDebug
```

APK output:

```
SwingLab-project/app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions

The workflow at `.github/workflows/android.yml` decodes the source archive, installs JDK 17 and Android SDK command-line tools, installs Gradle 8.9, runs `assembleDebug`, and uploads `SwingLab-debug-apk`.

Use **Actions → SwingLab Android APK → Run workflow** if the automatic push trigger did not start. Download the APK from the completed run's **Artifacts** section.

## Live provider setup

Add a Twelve Data API key in Settings inside the app. Do not commit API keys. Check your subscription for NSE quote access and real-time entitlements. Provider errors are surfaced as status messages rather than replaced with simulated live quotes.
