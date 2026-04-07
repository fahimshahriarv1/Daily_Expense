package com.fahimshahriarv1.dailyexpense.domain.model

import java.util.UUID

data class Expense(
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long,
    val accountId: Long,
    val accountName: String = ""
)
