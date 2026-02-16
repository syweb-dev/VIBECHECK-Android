/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import ASIA.TPD.vibecheck.R
import ASIA.TPD.vibecheck.data.Mood
import ASIA.TPD.vibecheck.data.Transaction
import ASIA.TPD.vibecheck.data.TransactionType
import ASIA.TPD.vibecheck.data.TransactionViewModel
import ASIA.TPD.vibecheck.ui.components.MoodPicker
import ASIA.TPD.vibecheck.ui.components.VibeButton
import ASIA.TPD.vibecheck.ui.theme.*

@Composable
fun AddEntryScreen(
    viewModel: TransactionViewModel = viewModel(),
    isTableTop: Boolean = false,
    onNavigateBack: () -> Unit
) {
    var amountStr by remember { mutableStateOf("0") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedMood by remember { mutableStateOf(Mood.NEUTRAL) }
    var note by remember { mutableStateOf("General") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val categories by remember {
        mutableStateOf(
            try {
                context.resources.getStringArray(ASIA.TPD.vibecheck.R.array.categories).toList()
            } catch (e: Exception) {
                listOf("General","Food","Transport","Shopping","Entertainment","Bills","Health","Education","Salary","Bonus","Investment","Other")
            }
        )
    }

    // --- Content Sections ---

    val TopSection = @Composable {
        // 1. Income/Expense Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            VibeButton(
                text = stringResource(id = R.string.income),
                onClick = { selectedType = TransactionType.INCOME },
                modifier = Modifier.weight(1f),
                color = if (selectedType == TransactionType.INCOME) NeoGreen else NeoWhite
            )
            VibeButton(
                text = stringResource(id = R.string.expense),
                onClick = { selectedType = TransactionType.EXPENSE },
                modifier = Modifier.weight(1f),
                color = if (selectedType == TransactionType.EXPENSE) NeoPink else NeoWhite
            )
        }

        // 2. Large Amount Display
        Text(
            text = "$$amountStr",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = NeoBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // 3. Category Selection (Placeholder)
        VibeButton(
            text = stringResource(id = R.string.category_label, note),
            onClick = { showCategoryDialog = true },
            modifier = Modifier.fillMaxWidth()
        )
    }

    val BottomSection = @Composable {
        // 4. Mood Picker
        Text(
            text = stringResource(id = R.string.pick_your_mood),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        MoodPicker(
            selectedMood = selectedMood,
            onMoodSelected = { selectedMood = it },
            modifier = Modifier.fillMaxWidth()
        )

        // 5. Numeric Keypad
        val keys = listOf(
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            ".", "0", "⌫"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp) // 4 rows * 56dp + 3 gaps * 8dp
        ) {
            items(keys) { key ->
                VibeButton(
                    text = key,
                    onClick = {
                        amountStr = updateAmount(amountStr, key)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    color = NeoWhite
                )
            }
        }

        // 6. SAVE Button
        val canSave = amountStr.toDoubleOrNull()?.let { it > 0.0 } == true
        VibeButton(
            text = stringResource(id = R.string.save),
            onClick = {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    val transaction = Transaction(
                        date = System.currentTimeMillis(),
                        type = selectedType,
                        amount = amount,
                        notes = note,
                        mood = selectedMood
                    )
                    viewModel.addTransaction(transaction)
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            color = if (canSave) NeoYellow else NeoWhite,
            enabled = canSave
        )
    }

    // --- Layout Logic ---

    if (isTableTop) {
        // Flex Mode: Split Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoBackground)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                VibeButton(
                    text = stringResource(id = R.string.back),
                    onClick = onNavigateBack,
                    color = NeoWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top Half (Display)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopSection()
            }

            // Bottom Half (Controls)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Wrap in scrollable box for safety
                Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        BottomSection()
                    }
                }
            }
        }
    } else {
        // Standard Mode: Single Scrollable Column
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoBackground)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                VibeButton(
                    text = stringResource(id = R.string.back),
                    onClick = onNavigateBack,
                    color = NeoWhite
                )
            }

            Text(
                text = stringResource(id = R.string.swipe_down_hint),
                style = MaterialTheme.typography.labelSmall,
                color = NeoBlack
            )

            TopSection()
            BottomSection()
        }
    }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(text = stringResource(id = R.string.category_label, ""))
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { c ->
                        VibeButton(
                            text = c,
                            onClick = {
                                note = c
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                VibeButton(
                    text = stringResource(id = ASIA.TPD.vibecheck.R.string.back),
                    onClick = { showCategoryDialog = false }
                )
            }
        )
    }
}

private fun updateAmount(current: String, input: String): String {
    return when (input) {
        "⌫" -> {
            if (current.length > 1) current.dropLast(1) else "0"
        }
        "." -> {
            if (current.contains(".")) current else "$current."
        }
        else -> {
            if (current == "0") input else current + input
        }
    }
}
