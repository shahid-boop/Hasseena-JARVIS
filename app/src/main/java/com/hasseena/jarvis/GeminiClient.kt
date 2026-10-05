package com.hasseena.jarvis

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class GeminiClient(private val prefs: Prefs) {
    fun ask(userText: String, callback: (Result<String>) -> Unit) {
        Thread {
            try {
                val key = prefs.apiKey
                require(key.isNotBlank()) { "Gemini API key is missing. Open Settings and add it." }
                val model = prefs.model.ifBlank { AppConfig.DEFAULT_MODEL }
                val url = URL("${AppConfig.GEMINI_ENDPOINT}${model}:generateContent?key=${java.net.URLEncoder.encode(key, "UTF-8")}")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"; connectTimeout = 15000; readTimeout = 45000
                    setRequestProperty("Content-Type", "application/json"); doOutput = true
                }
                val system = "You are Hasseena, a mature, confident, respectful female AI companion inspired by the architecture of a desktop JARVIS assistant. Be helpful, concise, warm and intelligent. Do not claim abilities Android has not granted. User memory: ${prefs.memory}"
                val parts = JSONArray().put(JSONObject().put("text", "$system\n\nUser: $userText"))
                val body = JSONObject().put("contents", JSONArray().put(JSONObject().put("role", "user").put("parts", parts))).toString()
                conn.outputStream.use { it.write(body.toByteArray(StandardCharsets.UTF_8)) }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val response = stream.bufferedReader().use { it.readText() }
                if (code !in 200..299) error("Gemini HTTP $code: $response")
                val text = JSONObject(response).optJSONArray("candidates")?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                if (text.isNullOrBlank()) error("Gemini returned no text.")
                callback(Result.success(text.trim()))
            } catch (e: Exception) { callback(Result.failure(e)) }
        }.start()
    }
}
