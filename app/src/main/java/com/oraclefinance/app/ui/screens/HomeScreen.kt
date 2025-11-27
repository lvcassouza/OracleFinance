package com.oraclefinance.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.oraclefinance.app.R
import com.oraclefinance.app.ui.components.StatCard
import com.oraclefinance.app.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.entry.rememberChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val addSheetOpen = remember { mutableStateOf(false) }
    val aiSheetOpen = remember { mutableStateOf(false) }
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val aiSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { addSheetOpen.value = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Olá", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { viewModel.logout() }) { Text("Logout") }
            }

            if (state.loadingSummary) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.summary?.let { summary ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Receita", "R$ %.2f".format(summary.income), modifier = Modifier.weight(1f))
                    StatCard("Despesa", "R$ %.2f".format(summary.expense), modifier = Modifier.weight(1f))
                    StatCard("Saldo", "R$ %.2f".format(summary.balance), modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))

                val entries = summary.last7Days.mapIndexed { index, day -> FloatEntry(index.toFloat(), day.amount.toFloat()) }
                val producer = rememberChartEntryModelProducer()
                LaunchedEffect(entries) { producer.setEntries(entries) }
                Chart(
                    chartModelProducer = producer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis()
                )
            }

            Button(onClick = {
                aiSheetOpen.value = true
                viewModel.analyzeWithAI()
            }) {
                Text("Oracle AI")
            }
        }
    }

    if (addSheetOpen.value) {
        ModalBottomSheet(onDismissRequest = { addSheetOpen.value = false }, sheetState = addSheetState) {
            AddTransactionSheet(onSubmit = { amount, description, type, category ->
                viewModel.addTransaction(amount, description, type, category)
                addSheetOpen.value = false
            })
        }
    }

    if (aiSheetOpen.value) {
        ModalBottomSheet(onDismissRequest = { aiSheetOpen.value = false }, sheetState = aiSheetState) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                if (state.loadingAI) {
                    CircularProgressIndicator()
                } else {
                    Text(state.aiText ?: "")
                }
            }
        }
    }
}

@Composable
private fun AddTransactionSheet(onSubmit: (Double, String, String, String) -> Unit) {
    val amount = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("Despesa") }
    val category = remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        androidx.compose.material3.OutlinedTextField(value = amount.value, onValueChange = { amount.value = it }, label = { Text("Valor") })
        androidx.compose.material3.OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("Descrição") })
        androidx.compose.material3.OutlinedTextField(value = type.value, onValueChange = { type.value = it }, label = { Text("Tipo") })
        androidx.compose.material3.OutlinedTextField(value = category.value, onValueChange = { category.value = it }, label = { Text("Categoria") })
        Button(onClick = {
            val amt = amount.value.toDoubleOrNull() ?: 0.0
            onSubmit(amt, description.value, type.value, category.value)
        }) { Text("Salvar") }
    }
}
