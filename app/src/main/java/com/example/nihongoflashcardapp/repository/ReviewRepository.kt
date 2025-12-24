package com.example.nihongoflashcardapp.repository

import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Flashcard

class ReviewRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    /**
     * REVIEW ĐÚNG CHUẨN:
     * - Lấy ALL flashcards
     * - Lấy ALL progress
     * - Lọc theo status hiện tại
     */
    fun getReviewFlashcards(
        lessonId: String,
        status: String,
        onSuccess: (List<Flashcard>) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Lấy toàn bộ flashcards của lesson
        db.collection("flashcards")
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener { cardSnap ->

                val allCards = cardSnap.documents.map { doc ->
                    Flashcard(
                        id = doc.id,
                        lessonId = lessonId,
                        word = doc.getString("word") ?: "",
                        reading = doc.getString("reading") ?: "",
                        meaning = doc.getString("meaning") ?: "",
                        example = doc.getString("example") ?: ""
                    )
                }

                // 2. Lấy toàn bộ progress của user trong lesson
                db.collection("user_progress")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("lessonId", lessonId)
                    .get()
                    .addOnSuccessListener { progressSnap ->

                        val progressMap = progressSnap.documents.associate {
                            it.getString("cardId")!! to it.getString("status")
                        }

                        // 3. Lọc theo status HIỆN TẠI
                        val result = allCards.filter {
                            progressMap[it.id] == status
                        }

                        onSuccess(result)
                    }
            }
    }
}
