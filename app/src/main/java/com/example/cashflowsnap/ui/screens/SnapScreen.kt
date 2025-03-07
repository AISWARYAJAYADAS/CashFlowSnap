package com.example.cashflowsnap.ui.screens

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashflowsnap.model.Expense
import com.example.cashflowsnap.ui.components.AddExpenseFAB
import com.example.cashflowsnap.ui.components.CustomTopAppBar
import com.example.cashflowsnap.ui.components.ExpenseItem
import com.example.cashflowsnap.ui.components.ExpenseInputDialog

@Composable
fun SnapScreen(
    snaps: List<Expense>,
    onAddExpense: (String, String, String, String, Uri?) -> Unit,
    onDeleteExpense: (Int) -> Unit,
    onEditExpense: (Int, String, String, String, String, Uri?) -> Unit,
    darkMode:Boolean,
    onToggleDarkMode: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTopAppBar(darkMode = darkMode, onToggleDarkMode = onToggleDarkMode)
        if (snaps.isEmpty()) {
            Text(
                text = "No expenses added yet",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(snaps) { index, expense ->
                    ExpenseItem(
                        expense = expense,
                        onEdit = {
                            editIndex = index
                            showEditDialog = true
                        },
                        onDelete = { onDeleteExpense(index) }
                    )
                }
            }
        }
        AddExpenseFAB(onAddExpense = onAddExpense)
    }

    if (showEditDialog && editIndex != -1) {
        val expense = snaps[editIndex]
        ExpenseInputDialog(
            onDismiss = { showEditDialog = false },
            onAddExpense = { amount, category, date, note, uri ->
                onEditExpense(editIndex, amount, category, date, note, uri)
                showEditDialog = false
            },
            initialAmount = expense.amount.toString(),
            initialCategory = expense.category,
            initialDate = expense.date,
            initialNote = expense.note
        )
    }
}