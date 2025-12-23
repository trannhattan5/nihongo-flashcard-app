package com.example.nihongoflashcardapp.repository

import com.example.nihongoflashcardapp.firebase.FirebaseService

class LessonProgressRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    fun loadLessonProgress(
        lessonId: String,
        totalCards: Int,
        onResult: (
            remembered: Int,
            notRemembered: Int,
            notLearned: Int
        ) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("user_progress")
            .whereEqualTo("userId", userId)
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener { snap ->
                val remembered = snap.count { it["status"] == "remembered" }
                val notRemembered = snap.count { it["status"] == "not_remembered" }
                val notLearned = totalCards - (remembered + notRemembered)

                onResult(remembered, notRemembered, notLearned)
            }
    }
}

