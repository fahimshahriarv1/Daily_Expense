package com.fahimshahriarv1.dailyexpense.data.remote

import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreTransferDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun transfersRef() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it).collection("transfers")
    }

    suspend fun upsert(transfer: Transfer) {
        transfersRef()?.document(transfer.uuid)?.set(
            mapOf(
                "uuid" to transfer.uuid,
                "amount" to transfer.amount,
                "fromAccountUuid" to transfer.fromAccountUuid,
                "toAccountUuid" to transfer.toAccountUuid,
                "note" to transfer.note,
                "date" to transfer.date
            )
        )?.await()
    }

    suspend fun delete(uuid: String) {
        transfersRef()?.document(uuid)?.delete()?.await()
    }

    suspend fun getAll(): List<Transfer> {
        val docs = transfersRef()?.get()?.await()?.documents ?: return emptyList()
        return docs.mapNotNull { doc ->
            try {
                Transfer(
                    uuid = doc.getString("uuid") ?: doc.id,
                    amount = doc.getDouble("amount") ?: 0.0,
                    fromAccountUuid = doc.getString("fromAccountUuid") ?: "",
                    toAccountUuid = doc.getString("toAccountUuid") ?: "",
                    note = doc.getString("note") ?: "",
                    date = doc.getLong("date") ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
