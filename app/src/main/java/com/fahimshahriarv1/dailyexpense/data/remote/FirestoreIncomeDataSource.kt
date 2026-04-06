package com.fahimshahriarv1.dailyexpense.data.remote

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreIncomeDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun incomesRef() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it).collection("incomes")
    }

    suspend fun upsert(income: Income) {
        incomesRef()?.document(income.uuid)?.set(
            mapOf(
                "uuid" to income.uuid,
                "amount" to income.amount,
                "source" to income.source,
                "note" to income.note,
                "date" to income.date,
                "accountId" to income.accountId
            )
        )?.await()
    }

    suspend fun delete(uuid: String) {
        incomesRef()?.document(uuid)?.delete()?.await()
    }

    suspend fun getAll(): List<Income> {
        val docs = incomesRef()?.get()?.await()?.documents ?: return emptyList()
        return docs.mapNotNull { doc ->
            try {
                Income(
                    uuid = doc.getString("uuid") ?: doc.id,
                    amount = doc.getDouble("amount") ?: 0.0,
                    source = doc.getString("source") ?: "",
                    note = doc.getString("note") ?: "",
                    date = doc.getLong("date") ?: 0L,
                    accountId = doc.getLong("accountId") ?: 0L
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
