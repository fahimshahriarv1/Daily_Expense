package com.fahimshahriarv1.dailyexpense.domain.model

import java.util.UUID

data class Income(
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double,
    val source: String,
    val note: String = "",
    val date: Long,
    val accountUuid: String,
    val accountName: String = ""
)
