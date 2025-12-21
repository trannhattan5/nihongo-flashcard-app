package com.example.nihongoflashcardapp.repository

import android.util.Log
import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Flashcard
import com.example.nihongoflashcardapp.models.UserProgress
import kotlin.math.log

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
                Log.d("FlashcardTest", "Query flashcards size = ${it.size()}")
                it.documents.forEach { doc ->
                    Log.d("FlashcardTest", "DocId=${doc.id}, data=${doc.data}")
                }
                val flashcards = it.documents.map { doc ->
                    Flashcard(
                        id = doc.id,
                        lessonId = doc.getString("lessonId") ?: "",
                        word = doc.getString("word") ?: "",
                        reading = doc.getString("reading") ?: "",
                        meaning = doc.getString("meaning") ?: "",
                        example = doc.getString("example") ?: ""
                    )
                }
                onSuccess(flashcards)

            }
            .addOnFailureListener {
                 onError(it.message ?: "Load flashcards failed")
            }
        Log.d(
            "FlashcardFlow",
            "Firestore projectId = ${FirebaseService.db.app.options.projectId}"
        )

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
