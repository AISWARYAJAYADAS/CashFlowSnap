package com.example.cashflowsnap.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(darkMode: Boolean, onToggleDarkMode: () -> Unit) {
    TopAppBar(
        title = { Text("CashFlowSnap", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Switch(checked = darkMode, onCheckedChange = { onToggleDarkMode() })
        }
    )
}