package com.example.hsklearner.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechRecognitionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _speechState.value = SpeechState.Listening
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _speechState.value = SpeechState.Processing
                }

                override fun onError(error: Int) {
                    _speechState.value = SpeechState.Error(mapError(error))
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _speechState.value = SpeechState.Result(matches[0])
                    } else {
                        _speechState.value = SpeechState.Error("No speech detected")
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            _speechState.value = SpeechState.Error("Speech recognition not available")
        }
    }

    fun startListening() {
        if (speechRecognizer == null) return
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN") // Mandarin Chinese
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        
        // Reset state
        _speechState.value = SpeechState.Idle
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _speechState.value = SpeechState.Error(e.message ?: "Unknown error")
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }
    
    fun resetInfo() {
         _speechState.value = SpeechState.Idle
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun mapError(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }
}

sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    object Processing : SpeechState()
    data class Result(val text: String) : SpeechState()
    data class Error(val message: String) : SpeechState()
}
