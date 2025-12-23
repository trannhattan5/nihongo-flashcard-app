package com.example.nihongoflashcardapp.repository


import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Flashcard
class ReviewRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    fun getReviewFlashcards(
        lessonId: String,
        status: String,
        onSuccess: (List<Flashcard>) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("user_progress")
            .whereEqualTo("userId", userId)
            .whereEqualTo("lessonId", lessonId)
            .whereEqualTo("status", status)
            .get()
            .addOnSuccessListener { progressSnap ->

                val cardIds = progressSnap.documents
                    .mapNotNull { it.getString("cardId") }
                    .filter { it.isNotBlank() }

                if (cardIds.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                db.collection("flashcards")
                    .whereIn(
                        com.google.firebase.firestore.FieldPath.documentId(),
                        cardIds
                    )
                    .get()
                    .addOnSuccessListener { cardSnap ->
                        onSuccess(cardSnap.toObjects(Flashcard::class.java))
                    }
            }
    }
}
