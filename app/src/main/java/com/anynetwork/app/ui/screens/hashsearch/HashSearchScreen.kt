package com.anynetwork.app.ui.screens.hashsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HashSearchScreen(viewModel: HashSearchViewModel = hiltViewModel()) {
    val message by remember { mutableStateOf("Hello world") }
    var requirement by remember { mutableStateOf(viewModel.requirement) }
    var numHashes by remember { mutableStateOf(viewModel.numHashes) }

    val results by remember { derivedStateOf { viewModel.results } }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Input fields
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = requirement,
                onValueChange = {
                    requirement = it
                    viewModel.requirement = it
                },
                label = { Text("Requirement") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = numHashes,
                onValueChange = {
                    numHashes = it
                    viewModel.numHashes = it
                },
                label = { Text("Hashes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(100.dp)
            )
        }

        // Stats + Start/Stop button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Hashes/sec (instant): ${viewModel.instantSpeed.toInt()}")
                Text("Hashes/sec (avg): ${viewModel.averageSpeed.toInt()}")
                Text("Total hashes: ${viewModel.totalHashes}")
                Text("Estimated required: ${viewModel.estimatedRequired}")
                val lastResult = results.lastOrNull()
                Text("Total time: ${"%.3f".format(lastResult?.timeElapsed ?: 0.0)} sec")
                if ((lastResult?.timeElapsed ?: 0.0) > 0 && results.isNotEmpty()) {
                    Text(
                        "Avg time/hash: ${
                            "%.3f".format(lastResult!!.timeElapsed / results.size)
                        } sec"
                    )
                }
            }

            Button(
                onClick = {
                    if (viewModel.isRunning) viewModel.stop() else viewModel.start()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.isRunning) Color.Red else Color.Green
                ),
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(if (viewModel.isRunning) "Stop" else "Start")
            }
        }

        // Hash results list
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(results) { index, result ->
                Text(
                    text = String.format(
                        "%d. Nonce: %d | Time: %.3f sec | Hash: %s",
                        index, result.nonce, result.timeElapsed, result.hash
                    ),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}