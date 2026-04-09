package com.fahimshahriarv1.dailyexpense.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import java.util.UUID

@Entity(
    tableName = "transfers",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["fromAccountUuid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["uuid"],
            childColumns = ["toAccountUuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fromAccountUuid"), Index("toAccountUuid"), Index("uuid", unique = true)]
)
data class TransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double,
    val fromAccountUuid: String,
    val toAccountUuid: String,
    val note: String = "",
    val date: Long
)

fun TransferEntity.toDomain() = Transfer(
    id = id,
    uuid = uuid,
    amount = amount,
    fromAccountUuid = fromAccountUuid,
    toAccountUuid = toAccountUuid,
    note = note,
    date = date
)

fun Transfer.toEntity() = TransferEntity(
    id = id,
    uuid = uuid,
    amount = amount,
    fromAccountUuid = fromAccountUuid,
    toAccountUuid = toAccountUuid,
    note = note,
    date = date
)
