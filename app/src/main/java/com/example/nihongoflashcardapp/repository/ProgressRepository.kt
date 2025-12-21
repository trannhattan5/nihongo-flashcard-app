package com.example.nihongoflashcardapp.repository

import com.example.nihongoflashcardapp.firebase.FirebaseService

class ProgressRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    fun loadProgress(
        onResult: (total: Int, remembered: Int, notRemembered: Int) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("user_progress")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val total = result.size()
                val remembered = result.count { it.getString("status") == "remembered" }
                val notRemembered = total - remembered
                onResult(total, remembered, notRemembered)
            }
    }
}
