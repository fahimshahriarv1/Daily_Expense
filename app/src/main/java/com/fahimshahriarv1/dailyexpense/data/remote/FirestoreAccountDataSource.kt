package com.fahimshahriarv1.dailyexpense.data.remote

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAccountDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun accountsRef() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it).collection("accounts")
    }

    suspend fun upsert(account: Account) {
        accountsRef()?.document(account.uuid)?.set(
            mapOf(
                "uuid" to account.uuid,
                "name" to account.name,
                "type" to account.type,
                "balance" to account.balance
            )
        )?.await()
    }

    suspend fun delete(uuid: String) {
        accountsRef()?.document(uuid)?.delete()?.await()
    }

    suspend fun updateBalance(uuid: String, balance: Double) {
        accountsRef()?.document(uuid)?.update("balance", balance)?.await()
    }

    suspend fun getAll(): List<Account> {
        val docs = accountsRef()?.get()?.await()?.documents ?: return emptyList()
        return docs.mapNotNull { doc ->
            try {
                Account(
                    uuid = doc.getString("uuid") ?: doc.id,
                    name = doc.getString("name") ?: "",
                    type = doc.getString("type") ?: "",
                    balance = doc.getDouble("balance") ?: 0.0
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
