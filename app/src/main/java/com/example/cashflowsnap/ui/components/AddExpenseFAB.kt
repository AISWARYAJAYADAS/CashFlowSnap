package com.example.cashflowsnap.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme

@Composable
fun AddExpenseFAB(onAddExpense: (String, String, String, String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Expense")
    }

    if (showDialog) {
        ExpenseInputDialog(
            onDismiss = { showDialog = false },
            onAddExpense = onAddExpense
        )
    }
}