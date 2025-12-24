package com.example.nihongoflashcardapp.repository

import android.util.Log
import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Flashcard

class FlashcardRepository {

    private val db = FirebaseService.db
    private val auth = FirebaseService.auth

    /* =========================================================
     * 1. LẤY TOÀN BỘ FLASHCARD THEO LESSON (DÙNG LÀM CHUẨN)
     * ========================================================= */
    fun getAllFlashcards(
        lessonId: String,
        onSuccess: (List<Flashcard>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("flashcards")
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener { snap ->
                val cards = snap.documents.map { doc ->
                    Flashcard(
                        id = doc.id,
                        lessonId = lessonId,
                        word = doc.getString("word") ?: "",
                        reading = doc.getString("reading") ?: "",
                        meaning = doc.getString("meaning") ?: "",
                        example = doc.getString("example") ?: ""
                    )
                }
                onSuccess(cards)
            }
            .addOnFailureListener {
                onError(it.message ?: "Load flashcards failed")
            }
    }

    /* =========================================================
     * 2. LẤY FLASHCARD CHƯA HỌC (MODE_LEARN_NEW)
     * ========================================================= */
    fun getUnlearnedFlashcards(
        lessonId: String,
        onSuccess: (List<Flashcard>) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        // 1. Lấy toàn bộ thẻ của lesson
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

                // 2. Lấy progress của user
                db.collection("user_progress")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("lessonId", lessonId)
                    .get()
                    .addOnSuccessListener { progressSnap ->

                        val learnedIds = progressSnap.documents
                            .mapNotNull { it.getString("cardId") }
                            .toSet()

                        // 3. Chỉ trả về thẻ CHƯA học
                        onSuccess(
                            allCards.filter { it.id !in learnedIds }
                        )
                    }
            }
    }

    /* =========================================================
     * 3. LƯU / CẬP NHẬT TRẠNG THÁI FLASHCARD
     * ========================================================= */
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

    /* =========================================================
     * 4. LOAD PROGRESS CỦA LESSON (DÙNG CHO UI + STAT)
     * ========================================================= */
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
