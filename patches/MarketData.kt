package com.swinglab.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class Candle(val date: String, val open: Double, val high: Double, val low: Double, val close: Double, val volume: Long)
data class Quote(val symbol: String, val name: String, val price: Double, val changePct: Double, val timestamp: String)
data class StockAnalysis(val symbol: String, val quote: Quote?, val ath: Double?, val athBreakout: Boolean, val darvasTop: Double?, val darvasBottom: Double?, val note: String)

class KeyStore(context: Context) {
    private val prefs = try {
        val master = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(context, "swinglab_secure", master,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    } catch (_: Exception) { context.getSharedPreferences("swinglab_fallback", Context.MODE_PRIVATE) }

    fun getApiKey() = prefs.getString("bharatstock_api_key", "") ?: ""
    fun saveApiKey(value: String) { prefs.edit().putString("bharatstock_api_key", value.trim()).apply() }
}

class BharatStockProvider(private val keyStore: KeyStore) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS).readTimeout(18, TimeUnit.SECONDS).build()

    private fun get(path: String, params: Map<String, String> = emptyMap()): JSONObject {
        val query = if (params.isEmpty()) "" else params.entries.joinToString("&", prefix = "?") {
            "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
        }
        val key = keyStore.getApiKey()
        require(key.isNotBlank()) { "Add your free BharatStock API key in Settings." }
        val request = Request.Builder()
            .url("https://bharatstockapi.com/v1/$path$query")
            .header("X-API-Key", key)
            .get().build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val detail = try { JSONObject(body).optString("detail", body) } catch (_: Exception) { body }
                error("BharatStock HTTP ${response.code}: ${detail.take(180)}")
            }
            val json = JSONObject(body)
            if (json.has("detail")) error(json.optString("detail", "BharatStock API error"))
            return json
        }
    }

    suspend fun quote(symbol: String): Quote = withContext(Dispatchers.IO) {
        val j = get("stocks/$symbol")
        val latest = j.optJSONObject("latest_price") ?: j
        val price = latest.optDouble("close", Double.NaN)
        require(price.isFinite()) { "No closing price returned for $symbol." }
        val previous = latest.optDouble("prev_close", Double.NaN)
        val change = if (previous.isFinite() && previous != 0.0) ((price - previous) / previous) * 100.0 else 0.0
        Quote(symbol, j.optString("company_name", symbol), price, change,
            latest.optString("trade_date", "End-of-day data"))
    }

    suspend fun candles(symbol: String, size: Int = 250): List<Candle> = withContext(Dispatchers.IO) {
        val j = get("stocks/$symbol/prices", mapOf("exchange" to "NSE", "page" to "1", "page_size" to size.coerceIn(1, 1000).toString()))
        val arr = j.optJSONArray("data") ?: error("No daily price history returned for $symbol.")
        (0 until arr.length()).mapNotNull { i ->
            val c = arr.getJSONObject(i)
            val o = c.optDouble("open", Double.NaN)
            val h = c.optDouble("high", Double.NaN)
            val l = c.optDouble("low", Double.NaN)
            val cl = c.optDouble("close", Double.NaN)
            if (!o.isFinite() || !h.isFinite() || !l.isFinite() || !cl.isFinite()) null
            else Candle(c.optString("trade_date"), o, h, l, cl, c.optLong("volume", 0L))
        }.reversed()
    }
}

suspend fun analyze(provider: BharatStockProvider, symbol: String): StockAnalysis {
    val q = provider.quote(symbol)
    val bars = provider.candles(symbol, 250)
    val ath = bars.maxOfOrNull { it.high }
    val previousAth = bars.dropLast(1).maxOfOrNull { it.high }
    val last = bars.lastOrNull()
    val breakout = last != null && previousAth != null && last.close > previousAth
    val recent = bars.takeLast(20)
    val top = recent.dropLast(1).maxOfOrNull { it.high }
    val bottom = recent.minOfOrNull { it.low }
    val darvas = last != null && top != null && last.close > top
    val note = when {
        breakout -> "Potential 1-year-window breakout: verify longer history, volume and close confirmation."
        darvas -> "Potential 20-session range breakout. Review box structure and volume."
        else -> "No qualifying starter breakout detected in the retrieved window."
    }
    return StockAnalysis(symbol, q, ath, breakout || darvas, top, bottom, note)
}
