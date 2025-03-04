package com.example.cashflowsnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.cashflowsnap.ui.theme.CashFlowSnapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CashFlowSnapTheme {
                var snaps by remember { mutableStateOf(listOf<String>()) }

                Scaffold(
                    topBar = { CustomTopAppBar() },
                    floatingActionButton = {
                        AddExpenseFAB(
                            onAddExpense = { amount, category, date, note ->
                                val snap = "$amount - $category - $date - $note"
                                snaps = snaps + snap
                            }
                        )
                    }
                ) { padding ->
                    SnapScreen(
                        modifier = Modifier.padding(padding),
                        snaps = snaps,
                        onDeleteExpense = { indexToRemove ->
                            snaps = snaps.filterIndexed { i, _ -> i != indexToRemove }
                        },
                        onEditExpense = { indexToEdit, amount, category, date, note ->
                            val snap = "$amount - $category - $date - $note"
                            snaps = snaps.mapIndexed { i, old -> if (i == indexToEdit) snap else old }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "CashFlowSnap",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

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

@Composable
fun ExpenseInputDialog(
    onDismiss: () -> Unit,
    onAddExpense: (String, String, String, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add Expense",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g., Food)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g., March 2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (amount.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
                            onAddExpense(amount, category, date, note)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Expense")
                }
            }
        }
    }
}

@Composable
fun SnapScreen(
    modifier: Modifier = Modifier,
    snaps: List<String>,
    onDeleteExpense: (Int) -> Unit,
    onEditExpense: (Int, String, String, String, String) -> Unit
) {

    var showEditDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableIntStateOf(-1) }
    var editAmount by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                itemsIndexed(snaps) { index, snap ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = snap,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                Button(
                                    onClick = {
                                        val parts = snap.split(" - ")
                                        editIndex = index
                                        editAmount = parts[0]
                                        editCategory = parts[1]
                                        editDate = parts[2]
                                        editNote = if (parts.size > 3) parts[3] else ""
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                Button(onClick = { onDeleteExpense(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        ExpenseInputDialog(
            onDismiss = { showEditDialog = false },
            onAddExpense = { amount, category, date, note ->
                onEditExpense(editIndex, amount, category, date, note)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CashFlowSnapTheme {
        SnapScreen(
            snaps = listOf("20 - Food - March 2 - Pizza"),
            onDeleteExpense = {},
            onEditExpense = { _, _, _, _, _ -> })
    }
}