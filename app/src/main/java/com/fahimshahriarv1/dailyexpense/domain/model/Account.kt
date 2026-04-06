package com.fahimshahriarv1.dailyexpense.domain.model

import java.util.UUID

data class Account(
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val balance: Double = 0.0
)
