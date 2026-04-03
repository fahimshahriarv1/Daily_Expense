package com.example.dailyexpense.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.dailyexpense.domain.model.Expense

@Entity(
    tableName = "expenses",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = ["id"],
        childColumns = ["accountId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("accountId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long,
    val accountId: Long
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    amount = amount,
    category = category,
    note = note,
    date = date,
    accountId = accountId
)

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    amount = amount,
    category = category,
    note = note,
    date = date,
    accountId = accountId
)
