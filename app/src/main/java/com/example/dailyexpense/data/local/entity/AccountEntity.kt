package com.example.dailyexpense.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dailyexpense.domain.model.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Double = 0.0
)

fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = type,
    balance = balance
)

fun Account.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    balance = balance
)
