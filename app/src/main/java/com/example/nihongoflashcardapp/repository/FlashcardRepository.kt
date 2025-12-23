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

        db.collection("user_progress")
            .whereEqualTo("userId", userId)
            .whereEqualTo("lessonId", lessonId)
            .whereEqualTo("cardId", cardId)
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    // CREATE
                    db.collection("user_progress").add(
                        hashMapOf(
                            "userId" to userId,
                            "lessonId" to lessonId,
                            "cardId" to cardId,
                            "status" to status,
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                } else {
                    // UPDATE
                    snap.documents.first().reference.update(
                        mapOf(
                            "status" to status,
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                }
            }
    }
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
                val remembered = snap.count {
                    it.getString("status") == "remembered"
                }
                val notRemembered = snap.count {
                    it.getString("status") == "not_remembered"
                }
                val notLearned = totalCards - (remembered + notRemembered)

                onResult(remembered, notRemembered, notLearned)
            }
    }


}
