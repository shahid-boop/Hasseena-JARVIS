package com.hasseena.jarvis

import android.content.Context

class Prefs(context: Context) {
    private val p = context.getSharedPreferences(AppConfig.PREFS, Context.MODE_PRIVATE)
    var apiKey: String get() = p.getString("api_key", "") ?: ""; set(v) { p.edit().putString("api_key", v.trim()).apply() }
    var model: String get() = p.getString("model", AppConfig.DEFAULT_MODEL) ?: AppConfig.DEFAULT_MODEL; set(v) { p.edit().putString("model", v.trim()).apply() }
    var voiceEnabled: Boolean get() = p.getBoolean("voice_enabled", true); set(v) { p.edit().putBoolean("voice_enabled", v).apply() }
    var memory: String get() = p.getString("memory", "") ?: ""; set(v) { p.edit().putString("memory", v).apply() }
}
