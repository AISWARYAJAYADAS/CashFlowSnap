package com.example.cashflowsnap.model

data class Expense(
    val id: Int,
    val amount: String,
    val category: String,
    val date: String,
    val note: String
)
