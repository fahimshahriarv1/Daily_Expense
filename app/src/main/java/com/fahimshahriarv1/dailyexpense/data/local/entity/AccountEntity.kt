package com.fahimshahriarv1.dailyexpense.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fahimshahriarv1.dailyexpense.domain.model.Account
import java.util.UUID

@Entity(
    tableName = "accounts",
    indices = [Index("uuid", unique = true)]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val balance: Double = 0.0
)

fun AccountEntity.toDomain() = Account(
    id = id,
    uuid = uuid,
    name = name,
    type = type,
    balance = balance
)

fun Account.toEntity() = AccountEntity(
    id = id,
    uuid = uuid,
    name = name,
    type = type,
    balance = balance
)
