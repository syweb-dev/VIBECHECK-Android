/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.lucanex.top.vibecheck.data.Transaction
import top.lucanex.top.vibecheck.data.TransactionType
import top.lucanex.top.vibecheck.data.TransactionViewModel
import top.lucanex.top.vibecheck.ui.components.VibeButton
import top.lucanex.top.vibecheck.ui.components.VibeCard
import top.lucanex.top.vibecheck.ui.theme.*
import top.lucanex.top.vibecheck.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: TransactionViewModel = viewModel(),
    onNavigateToAddEntry: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val todayTransactions = remember(transactions) {
        transactions.filter { isToday(it.date) }
    }

    val netHappiness = remember(todayTransactions) {
        todayTransactions.sumOf { it.mood.score }
    }

    val netSpending = remember(todayTransactions) {
        val income = todayTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = todayTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        income - expense
    }

    Scaffold(
        floatingActionButton = {
            VibeButton(
                text = "+",
                onClick = onNavigateToAddEntry,
                modifier = Modifier.size(64.dp),
                color = NeoYellow
            )
        },
        containerColor = NeoBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                VibeButton(
                    text = stringResource(id = R.string.settings),
                    onClick = onNavigateToSettings,
                    color = NeoWhite
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VibeCard(
                    modifier = Modifier.weight(1f),
                    color = NeoBlue
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.net_vibe),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$netHappiness",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                VibeCard(
                    modifier = Modifier.weight(1f),
                    color = NeoPink
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.net_cash),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.0f", netSpending)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            VibeButton(
                text = stringResource(id = R.string.view_analytics),
                onClick = onNavigateToAnalytics,
                color = NeoPurple,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.todays_moves),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(todayTransactions.sortedByDescending { it.date }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { viewModel.deleteTransaction(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: (Transaction) -> Unit) {
    val amountColor = if (transaction.type == TransactionType.EXPENSE) Color.Red else Color(0xFF00C853)
    val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
    var showConfirm by remember { mutableStateOf(false) }

    VibeCard(
        modifier = Modifier.fillMaxWidth(),
        color = NeoWhite
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.mood.emoji,
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatDate(transaction.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$amountPrefix$${transaction.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                VibeButton(
                    text = stringResource(id = R.string.delete),
                    onClick = { showConfirm = true },
                    color = NeoPink
                )
            }
        }
    }

    if (showConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(text = stringResource(id = R.string.confirm_delete_title)) },
            text = { Text(text = stringResource(id = R.string.confirm_delete_message)) },
            confirmButton = {
                VibeButton(
                    text = stringResource(id = R.string.confirm),
                    onClick = {
                        showConfirm = false
                        onDelete(transaction)
                    },
                    color = NeoYellow
                )
            },
            dismissButton = {
                VibeButton(
                    text = stringResource(id = R.string.cancel),
                    onClick = { showConfirm = false }
                )
            }
        )
    }
}

private fun isToday(dateMillis: Long): Boolean {
    val calendar = Calendar.getInstance()
    val todayYear = calendar.get(Calendar.YEAR)
    val todayDay = calendar.get(Calendar.DAY_OF_YEAR)

    calendar.timeInMillis = dateMillis
    return calendar.get(Calendar.YEAR) == todayYear &&
            calendar.get(Calendar.DAY_OF_YEAR) == todayDay
}

private fun formatDate(dateMillis: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(dateMillis))
}
