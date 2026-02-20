/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import top.lucanex.top.vibecheck.R
import top.lucanex.top.vibecheck.data.Frequency
import top.lucanex.top.vibecheck.data.Mood
import top.lucanex.top.vibecheck.data.RecurringTransaction
import top.lucanex.top.vibecheck.data.Transaction
import top.lucanex.top.vibecheck.data.TransactionType
import top.lucanex.top.vibecheck.data.TransactionViewModel
import top.lucanex.top.vibecheck.ui.components.MoodPicker
import top.lucanex.top.vibecheck.ui.components.VibeButton
import top.lucanex.top.vibecheck.ui.theme.*

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
    
    var isRecurring by remember { mutableStateOf(false) }
    var recurringFrequency by remember { mutableStateOf(Frequency.MONTHLY) }
    var recurringDay by remember { mutableStateOf("1") }
    var recurringMonth by remember { mutableStateOf("1") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val categories by remember {
        mutableStateOf(
            try {
                context.resources.getStringArray(top.lucanex.top.vibecheck.R.array.categories).toList()
            } catch (e: Exception) {
                listOf("General","Food","Transport","Shopping","Entertainment","Bills","Health","Education","Salary","Bonus","Investment","Other")
            }
        )
    }

    val TopSection = @Composable {
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
        VibeButton(
            text = stringResource(id = R.string.category_label, note),
            onClick = { showCategoryDialog = true },
            modifier = Modifier.fillMaxWidth()
        )
    }

    val BottomSection = @Composable {
        // Recurring Option
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRecurring,
                onCheckedChange = { isRecurring = it },
                colors = CheckboxDefaults.colors(checkedColor = NeoBlack)
            )
            Text(
                text = stringResource(id = R.string.fixed_recurring),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (isRecurring) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Frequency.entries.forEach { freq ->
                        val isSelected = recurringFrequency == freq
                        val freqText = when(freq) {
                            Frequency.DAILY -> stringResource(id = R.string.frequency_daily)
                            Frequency.MONTHLY -> stringResource(id = R.string.frequency_monthly)
                            Frequency.YEARLY -> stringResource(id = R.string.frequency_yearly)
                        }
                        VibeButton(
                            text = freqText,
                            onClick = { recurringFrequency = freq },
                            modifier = Modifier.weight(1f),
                            color = if (isSelected) NeoYellow else NeoWhite
                        )
                    }
                }
                
                if (recurringFrequency == Frequency.MONTHLY || recurringFrequency == Frequency.YEARLY) {
                    OutlinedTextField(
                        value = recurringDay,
                        onValueChange = { if (it.all { c -> c.isDigit() }) recurringDay = it },
                        label = { Text(stringResource(id = R.string.day_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                if (recurringFrequency == Frequency.YEARLY) {
                    OutlinedTextField(
                        value = recurringMonth,
                        onValueChange = { if (it.all { c -> c.isDigit() }) recurringMonth = it },
                        label = { Text(stringResource(id = R.string.month_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

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

        val keys = listOf(
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            ".", "0", "DEL"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp)
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

        val canSave = amountStr.toDoubleOrNull()?.let { it > 0.0 } == true
        VibeButton(
            text = stringResource(id = R.string.save),
            onClick = {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    val now = System.currentTimeMillis()
                    val transaction = Transaction(
                        date = now,
                        type = selectedType,
                        amount = amount,
                        notes = note,
                        mood = selectedMood
                    )
                    viewModel.addTransaction(transaction)

                    if (isRecurring) {
                        val day = recurringDay.toIntOrNull() ?: 1
                        val month = recurringMonth.toIntOrNull() ?: 1
                        val rt = RecurringTransaction(
                            type = selectedType,
                            amount = amount,
                            notes = note,
                            mood = selectedMood,
                            frequency = recurringFrequency,
                            dayOfMonth = day,
                            monthOfYear = month,
                            lastGeneratedTime = now
                        )
                        viewModel.addRecurringTransaction(rt)
                    }

                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            color = if (canSave) NeoYellow else NeoWhite,
            enabled = canSave
        )
    }

    if (isTableTop) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoBackground)
                .padding(16.dp)
        ) {
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopSection()
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        BottomSection()
                    }
                }
            }
        }
    } else {
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
                    text = stringResource(id = R.string.back),
                    onClick = { showCategoryDialog = false }
                )
            }
        )
    }
}

private fun updateAmount(current: String, input: String): String {
    return when (input) {
        "DEL", "⌫" -> {
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
