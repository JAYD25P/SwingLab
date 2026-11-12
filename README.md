# SwingLab Android

SwingLab is a dark-mode Indian-equities swing-trading study and paper-trading app.

## Included
- Compose dashboard, NSE watchlist, scanner, risk calculator, paper-trade journal and learning notes
- BharatStock REST integration for Indian equities and daily OHLCV history
- ATH-window and simplified Darvas-range candidate detection using retrieved daily candles
- Paper trades stored locally; no broker order placement
- Android home-screen widget showing the latest refreshed watchlist snapshot
- GitHub Actions debug APK build

## Free market-data setup
1. Create a free account and generate an API key at https://bharatstockapi.com/ (the free key does not require a payment card).
2. In SwingLab > Settings, paste the key and save it.
3. Tap Refresh to load the default NSE watchlist: RELIANCE, TCS, HDFCBANK, ICICIBANK and INFY.
4. Use Run scan to retrieve daily OHLCV history and evaluate the starter breakout rules.

The free plan currently lists 60 requests per day and approximately one year of price history. Data is end-of-day (EOD), not streaming or intraday real-time. A refresh uses one request per watchlist symbol; a full scan uses quote and history requests per symbol, so avoid repeated scans. See provider documentation and current limits at https://bharatstockapi.com/.

## Build locally
- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35

Open this repository in Android Studio and run `:app:assembleDebug`, or use the GitHub Actions workflow.

## GitHub Actions APK
Push to `main`. Open **Actions > SwingLab Android APK > latest run > Artifacts > SwingLab-debug-apk**.
The APK is a debug build for testing, not a Play Store release.

## Important limitations
- This is an educational tool, not investment advice or a recommendation service.
- Data is end-of-day; it is unsuitable for intraday decisions or real-time alerts.
- Free API quotas and history limits are provider-controlled and may change.
- The scanner's ATH is limited to the history returned by the free plan (about one year), not the lifetime ATH.
- Darvas detection is a simplified range-breakout candidate detector and must be reviewed on the chart.
- No automatic trading, broker connectivity, options, margin or real-money execution.
- API key is stored locally on device. Do not publish it or commit it. For a public multi-user app, move provider credentials behind a secure backend.
