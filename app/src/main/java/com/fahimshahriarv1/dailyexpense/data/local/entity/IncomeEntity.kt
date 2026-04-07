package com.fahimshahriarv1.dailyexpense.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import java.util.UUID

@Entity(
    tableName = "incomes",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["accountUuid"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("accountUuid"), Index("uuid", unique = true)]
)
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double,
    val source: String,
    val note: String = "",
    val date: Long,
    val accountUuid: String
)

fun IncomeEntity.toDomain() = Income(
    id = id,
    uuid = uuid,
    amount = amount,
    source = source,
    note = note,
    date = date,
    accountUuid = accountUuid
)

fun Income.toEntity() = IncomeEntity(
    id = id,
    uuid = uuid,
    amount = amount,
    source = source,
    note = note,
    date = date,
    accountUuid = accountUuid
)
