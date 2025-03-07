package com.example.cashflowsnap.model

import android.net.Uri

data class Expense(
    val id: Int,
    val amount: Float,
    val category: String,
    val date: String,
    val note: String,
    val picUri: Uri? = null
)
