package com.fahimshahriarv1.dailyexpense.data.remote

import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreExpenseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun expensesRef() = auth.currentUser?.uid?.let {
        firestore.collection("users").document(it).collection("expenses")
    }

    suspend fun upsert(expense: Expense) {
        expensesRef()?.document(expense.uuid)?.set(
            mapOf(
                "uuid" to expense.uuid,
                "amount" to expense.amount,
                "category" to expense.category,
                "note" to expense.note,
                "date" to expense.date,
                "accountId" to expense.accountId
            )
        )?.await()
    }

    suspend fun delete(uuid: String) {
        expensesRef()?.document(uuid)?.delete()?.await()
    }

    suspend fun getAll(): List<Expense> {
        val docs = expensesRef()?.get()?.await()?.documents ?: return emptyList()
        return docs.mapNotNull { doc ->
            try {
                Expense(
                    uuid = doc.getString("uuid") ?: doc.id,
                    amount = doc.getDouble("amount") ?: 0.0,
                    category = doc.getString("category") ?: "",
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
