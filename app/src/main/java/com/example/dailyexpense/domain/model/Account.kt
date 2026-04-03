package com.example.dailyexpense.domain.model

data class Account(
    val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Double = 0.0
)
