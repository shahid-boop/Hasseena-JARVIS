package com.hasseena.jarvis

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: Prefs; private lateinit var voice: Voice; private lateinit var client: GeminiClient; private lateinit var memory: Memory
    private lateinit var chat: LinearLayout; private lateinit var scroll: ScrollView; private lateinit var input: EditText; private lateinit var status: TextView

    override fun onCreate(state: Bundle?) { super.onCreate(state); setContentView(R.layout.activity_main)
        prefs = Prefs(this); voice = Voice(this); client = GeminiClient(prefs); memory = Memory(prefs)
        chat = findViewById(R.id.chat); scroll = findViewById(R.id.scroll); input = findViewById(R.id.input); status = findViewById(R.id.status)
        findViewById<Button>(R.id.send).setOnClickListener { send() }; findViewById<Button>(R.id.mic).setOnClickListener { requestMicAndListen() }; findViewById<Button>(R.id.settings).setOnClickListener { showSettings() }
        addMessage("Hasseena", "Hello. I'm Hasseena. I'm ready to listen.", false)
    }
    private fun send() { val text = input.text.toString().trim(); if (text.isBlank()) return; input.text.clear(); addMessage("You", text, true); status.text = "Hasseena is thinking…"; client.ask(text) { result -> runOnUiThread { result.onSuccess { reply -> addMessage("Hasseena", reply, false); status.text = "Ready"; if (prefs.voiceEnabled) voice.speak(reply) }.onFailure { e -> addMessage("Hasseena", "I couldn't complete that: ${e.message}", false); status.text = "Needs attention" } } } }
    private fun addMessage(who: String, text: String, user: Boolean) { val box = TextView(this).apply { this.text = "$who\n$text"; setTextColor(resources.getColor(if (user) R.color.text else R.color.text)); textSize = 16f; setPadding(18,14,18,14); setTypeface(null, Typeface.NORMAL); gravity = Gravity.START; setBackgroundResource(if (user) R.drawable.bubble_user else R.drawable.bubble_ai) }; val lp = LinearLayout.LayoutParams(-1, -2); lp.setMargins(0,8,0,8); chat.addView(box, lp); scroll.post { scroll.fullScroll(ScrollView.FOCUS_DOWN) } }
    private fun requestMicAndListen() { if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) { requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 20); return }; status.text = "Listening…"; voice.listen({ text -> input.setText(text); status.text = "Voice captured"; send() }, { status.text = it }) }
    private fun showSettings() { val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,8,32,0) }; val key = EditText(this).apply { hint = "Gemini API key"; inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD; setText(prefs.apiKey) }; val model = EditText(this).apply { hint = "Model"; setText(prefs.model) }; val voiceSwitch = CheckBox(this).apply { text = "Speak replies with female/device TTS voice"; isChecked = prefs.voiceEnabled }; panel.addView(key); panel.addView(model); panel.addView(voiceSwitch); AlertDialog.Builder(this).setTitle("Hasseena Settings").setView(panel).setPositiveButton("Save") { _, _ -> prefs.apiKey = key.text.toString(); prefs.model = model.text.toString().ifBlank { AppConfig.DEFAULT_MODEL }; prefs.voiceEnabled = voiceSwitch.isChecked; status.text = "Settings saved" }.setNegativeButton("Cancel", null).show() }
    override fun onDestroy() { voice.shutdown(); super.onDestroy() }
}
