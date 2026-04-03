package com.example.dailyexpense.domain.model

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long,
    val accountId: Long,
    val accountName: String = ""
)
