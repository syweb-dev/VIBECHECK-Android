/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ASIA.TPD.vibecheck.data.Transaction
import ASIA.TPD.vibecheck.data.TransactionType
import ASIA.TPD.vibecheck.data.TransactionViewModel
import ASIA.TPD.vibecheck.data.Mood
import ASIA.TPD.vibecheck.ui.components.VibeButton
import ASIA.TPD.vibecheck.ui.theme.NeoBackground
import ASIA.TPD.vibecheck.ui.theme.NeoBlack
import ASIA.TPD.vibecheck.ui.theme.NeoWhite
import ASIA.TPD.vibecheck.ui.theme.NeoPink
import ASIA.TPD.vibecheck.ui.theme.NeoBlue
import ASIA.TPD.vibecheck.ui.theme.NeoGreen
import ASIA.TPD.vibecheck.ui.theme.NeoYellow
import ASIA.TPD.vibecheck.ui.theme.NeoOrange
import ASIA.TPD.vibecheck.ui.theme.NeoPurple
import androidx.compose.ui.res.stringResource
import ASIA.TPD.vibecheck.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoBackground)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            VibeButton(
                text = stringResource(id = R.string.back),
                onClick = onBackClick,
                color = NeoWhite
            )
        }

        Text(
            text = stringResource(id = R.string.analytics),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NeoBlack,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Happiness ROI (ScatterChart)
        Text(
            text = stringResource(id = R.string.happiness_roi),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NeoBlack,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        HappinessROIChart(transactions = transactions)
        Spacer(modifier = Modifier.height(24.dp))

        // Stupidity Tax (PieChart)
        Text(
            text = stringResource(id = R.string.stupidity_tax),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NeoBlack,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        StupidityTaxChart(transactions = transactions)
        Spacer(modifier = Modifier.height(24.dp))

        // Spending Trend (LineChart)
        Text(
            text = stringResource(id = R.string.spending_trend_last_30),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = NeoBlack,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        SpendingTrendChart(transactions = transactions)
        Spacer(modifier = Modifier.height(32.dp))

        VibeButton(
            text = stringResource(id = R.string.back_to_dashboard),
            onClick = onBackClick,
            color = NeoYellow
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HappinessROIChart(transactions: List<Transaction>) {
    val entries = transactions.map { transaction ->
        Entry(transaction.amount.toFloat(), transaction.mood.score.toFloat())
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(2.dp, NeoBlack)
            .background(NeoWhite)
            .padding(8.dp),
        factory = { context ->
            ScatterChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 6f // Mood 1-5
                axisLeft.granularity = 1f
                axisLeft.setLabelCount(5, true)
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val score = value.roundToInt().coerceIn(1, 5)
                        return Mood.fromScore(score).emoji
                    }
                }
            }
        },
        update = { chart ->
            val dataSet = ScatterDataSet(entries, "Transactions").apply {
                color = NeoPurple.toArgb()
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                scatterShapeSize = 15f
                setDrawValues(true)
                valueTextColor = NeoBlack.toArgb()
                valueTextSize = 12f
                valueFormatter = object : ValueFormatter() {
                    override fun getPointLabel(entry: Entry?): String {
                        if (entry == null) return ""
                        val score = entry.y.roundToInt().coerceIn(1, 5)
                        return Mood.fromScore(score).emoji
                    }
                }
            }
            chart.data = ScatterData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
fun StupidityTaxChart(transactions: List<Transaction>) {
    val badMoodTransactions = transactions.filter { it.mood.score < 3 && it.type == TransactionType.EXPENSE }
    val groupedData = badMoodTransactions.groupBy {
        if (it.notes.isNotBlank()) it.notes else "Unknown"
    }.mapValues { (_, txs) ->
        txs.sumOf { it.amount }
    }

    val entries = groupedData.map { (label, amount) ->
        PieEntry(amount.toFloat(), label)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(2.dp, NeoBlack)
            .background(NeoWhite)
            .padding(8.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                setEntryLabelColor(Color.Black.toArgb())
                setHoleColor(Color.Transparent.toArgb())
            }
        },
        update = { chart ->
            val dataSet = PieDataSet(entries, "Stupidity Tax").apply {
                colors = listOf(
                    NeoPink.toArgb(),
                    NeoBlue.toArgb(),
                    NeoGreen.toArgb(),
                    NeoYellow.toArgb(),
                    NeoOrange.toArgb()
                )
                valueTextColor = Color.Black.toArgb()
                valueTextSize = 12f
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
fun SpendingTrendChart(transactions: List<Transaction>) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val today = calendar.timeInMillis
    val days = (0 until 30).map { i ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = today
        cal.add(Calendar.DAY_OF_YEAR, - (29 - i))
        cal.timeInMillis
    }

    val recentTransactions = transactions.filter {
        it.date >= days.first() && it.type == TransactionType.EXPENSE
    }

    val groupedByDate = recentTransactions.groupBy {
        val cal = Calendar.getInstance()
        cal.timeInMillis = it.date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }.mapValues { (_, txs) ->
        txs.sumOf { it.amount }
    }

    val entries = days.mapIndexed { index, date ->
        val amount = groupedByDate[date] ?: 0.0
        Entry(index.toFloat(), amount.toFloat())
    }

    val labels = days.map { date ->
        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        sdf.format(Date(date))
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(2.dp, NeoBlack)
            .background(NeoWhite)
            .padding(8.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Daily Expense").apply {
                color = NeoBlack.toArgb()
                setCircleColor(NeoBlack.toArgb())
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                setDrawFilled(true)
                fillColor = NeoYellow.toArgb()
                fillAlpha = 100
            }
            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels) // Update labels if data changes
            chart.invalidate()
        }
    )
}
