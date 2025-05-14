package com.anynetwork.app.ui.screens.hashsearch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class HashSearchViewModel @Inject constructor() : ViewModel() {

    var message by mutableStateOf("Hello world")
    var requirement by mutableStateOf("")
    var numHashes by mutableStateOf("10")
    var results by mutableStateOf(listOf<HashResult>())
        private set

    var isRunning by mutableStateOf(false)
        private set

    var totalHashes by mutableStateOf(0)
        private set

    var instantSpeed by mutableStateOf(0.0)
        private set

    var averageSpeed by mutableStateOf(0.0)
        private set

    var estimatedRequired by mutableStateOf(0)
        private set

    private var job: Job? = null
    private var startTime = 0L
    private var lastHashTime = 0L
    private var lastHashCount = 0

    fun start() {
        if (message.isBlank() || requirement.isBlank()) return

        stop() // In case previous job is still running

        isRunning = true
        results = emptyList()
        totalHashes = 0
        instantSpeed = 0.0
        averageSpeed = 0.0
        estimatedRequired = 64.0.pow(requirement.length).toInt()

        startTime = System.currentTimeMillis()
        lastHashTime = startTime
        lastHashCount = 0

        val targetCount = numHashes.toIntOrNull() ?: 10
        val base = sha256Base64(message)

        job = viewModelScope.launch(Dispatchers.Default) {
            var nonce = 1

            while (isActive && results.size < targetCount) {
                val combined = base + nonce
                val hashed = sha256Base64(combined)

                if (hashed.lowercase().contains(requirement.lowercase())) {
                    val now = System.currentTimeMillis()
                    val timeElapsed = (now - startTime).toDouble() / 1000.0

                    withContext(Dispatchers.Main) {
                        results = results + HashResult(nonce, hashed, timeElapsed)
                    }

                    lastHashTime = now
                    lastHashCount = totalHashes
                }

                nonce++
                totalHashes = nonce

                // Update stats every 1000 iterations to reduce UI thrash
                if (nonce % 1000 == 0) {
                    val now = System.currentTimeMillis()
                    val timeSinceLast = (now - lastHashTime).coerceAtLeast(1)
                    val hashesSinceLast = totalHashes - lastHashCount

                    val inst = hashesSinceLast.toDouble() / (timeSinceLast.toDouble() / 1000.0)
                    val avg = totalHashes.toDouble() / ((now - startTime).toDouble() / 1000.0)

                    withContext(Dispatchers.Main) {
                        instantSpeed = inst
                        averageSpeed = avg
                    }
                }
            }

            withContext(Dispatchers.Main) {
                isRunning = false
            }
        }
    }

    fun stop() {
        job?.cancel()
        isRunning = false
    }

    private fun sha256Base64(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return Base64.getEncoder().encodeToString(digest)
    }
}

data class HashResult(val nonce: Int, val hash: String, val timeElapsed: Double)