# SwingLab

SwingLab is an Android learning, stock-analysis and paper-trading prototype for Indian equities. The current implementation uses a dark Jetpack Compose interface and a configurable Twelve Data REST provider.

## Repository layout

- `SwingLab-Android-Source.zip.b64` — Base64-encoded initial Android Studio source archive.
- `.github/workflows/android.yml` — CI workflow that decodes the archive and builds a debug APK.
- `BUILDING.md` — local setup and build instructions.

The source archive is encoded as text so it can be stored through the repository file API. Decode it before opening the Android project.

## Features in this prototype

- Market overview and NSE watchlist for RELIANCE, TCS, HDFCBANK, ICICIBANK and INFY.
- Twelve Data quote and daily OHLC requests.
- Starter ATH-window and simplified 20-session range breakout candidate checks.
- Position sizing and risk/reward calculator.
- Paper setup journal (currently in-memory; entries are not yet persisted across app restarts).
- Learning notes on market direction, relative strength, sector rotation, ATH, Darvas and risk.
- Android home-screen widget displaying the last quote snapshot after an in-app refresh.
- GitHub Actions debug APK workflow.

## Live data

Create a Twelve Data account and add your API key under Settings in the app. Exchange coverage, real-time access, symbol syntax and request limits depend on your Twelve Data subscription. The app must not be treated as a guaranteed real-time feed until you confirm the entitlement for NSE symbols in your account.

## Strategy attribution and limitations

These are transparent educational starter rules, not a verified reproduction of Atul Patel's proprietary or complete trading system. ATH uses the fetched history window (up to 500 daily bars), which may not represent the true lifetime all-time high. Darvas detection is simplified. Scanner results require manual chart, volume, liquidity and market-context review.

Paper mode does not connect to a broker and cannot place orders. The current journal is a prototype, not a full trade lifecycle or P&L ledger. No live-money execution is implemented.

## Build

See [BUILDING.md](BUILDING.md). The workflow produces a debug APK artifact for testing, not a signed Play Store release.
