package com.example.nihongoflashcardapp.repository

import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Flashcard
import com.example.nihongoflashcardapp.models.UserProgress

class FlashcardRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    fun getFlashcards(
        lessonId: String,
        onSuccess: (List<Flashcard>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("flashcards")
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener {
                onSuccess(it.toObjects(Flashcard::class.java))
            }
            .addOnFailureListener {
                onError(it.message ?: "Load flashcards failed")
            }
    }

    fun saveProgress(
        lessonId: String,
        cardId: String,
        status: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        val progress = UserProgress(
            userId = userId,
            lessonId = lessonId,
            cardId = cardId,
            status = status,
            updatedAt = System.currentTimeMillis()
        )

        db.collection("user_progress")
            .add(progress)
    }
}
