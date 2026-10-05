package com.hasseena.jarvis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

class Voice(private val context: Context) {
    private var tts: TextToSpeech? = null
    init { tts = TextToSpeech(context) { if (it == TextToSpeech.SUCCESS) configureFemaleVoice() } }
    private fun configureFemaleVoice() { val engine = tts ?: return; engine.language = Locale.US; engine.voices?.firstOrNull { it.locale.language == "en" && it.name.contains("female", true) }?.let { engine.voice = it } }
    fun speak(text: String) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hasseena-response") }
    fun listen(onText: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) { onError("Speech recognition is not available on this phone."); return }
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) { onText(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()); recognizer.destroy() }
            override fun onError(error: Int) { onError("Speech recognition error: $error"); recognizer.destroy() }
            override fun onReadyForSpeech(p: Bundle?) {} ; override fun onBeginningOfSpeech() {} ; override fun onRmsChanged(r: Float) {} ; override fun onBufferReceived(b: ByteArray?) {} ; override fun onEndOfSpeech() {} ; override fun onPartialResults(b: Bundle?) {} ; override fun onEvent(t: Int, p: Bundle?) {}
        })
        recognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply { putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag()) })
    }
    fun shutdown() { tts?.stop(); tts?.shutdown() }
}
