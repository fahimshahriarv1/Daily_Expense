package com.fahimshahriarv1.dailyexpense.domain.model

import java.util.UUID

data class Transfer(
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double,
    val fromAccountUuid: String,
    val toAccountUuid: String,
    val note: String = "",
    val date: Long,
    val fromAccountName: String = "",
    val toAccountName: String = ""
)
