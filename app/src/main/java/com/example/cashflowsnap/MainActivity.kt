package com.example.cashflowsnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.cashflowsnap.model.Expense
import com.example.cashflowsnap.ui.screens.SnapScreen
import com.example.cashflowsnap.ui.theme.CashFlowSnapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snaps = remember { mutableStateOf(listOf<Expense>()) }
            val darkMode = remember { mutableStateOf(false) }

            CashFlowSnapTheme(darkMode = darkMode.value) {
                SnapScreen(
                    snaps = snaps.value,
                    onAddExpense = { amount, category, date, note ->
                        val newExpense = Expense(
                            id = snaps.value.size + 1,
                            amount = amount,
                            category = category,
                            date = date,
                            note = note
                        )
                        snaps.value += newExpense
                    },
                    onDeleteExpense = { index ->
                        snaps.value = snaps.value.filterIndexed { i, _ -> i != index }
                    },
                    onEditExpense = { index, amount, category, date, note ->
                        val updatedExpense = Expense(
                            id = snaps.value[index].id,
                            amount = amount,
                            category = category,
                            date = date,
                            note = note
                        )
                        snaps.value = snaps.value.toMutableList().apply { set(index, updatedExpense) }
                    },
                    onToggleDarkMode = { darkMode.value = !darkMode.value }
                )
            }
        }
    }}

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        val sampleExpenses = listOf(
            Expense(
                id = 1,
                amount = "20",
                category = "Food",
                date = "March 2",
                note = "Pizza"
            ),
            Expense(
                id = 2,
                amount = "50",
                category = "Transport",
                date = "March 3",
                note = "Bus fare"
            )
        )

        CashFlowSnapTheme(darkMode = true) {
            SnapScreen(
                snaps = sampleExpenses,
                onAddExpense = { _, _, _, _ -> },
                onDeleteExpense = { _ -> },
                onEditExpense = { _, _, _, _, _ -> },
                onToggleDarkMode = {}
            )
        }
    }


