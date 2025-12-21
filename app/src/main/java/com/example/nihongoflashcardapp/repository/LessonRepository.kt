package com.example.nihongoflashcardapp.repository


import android.util.Log
import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Lesson

class LessonRepository {

    private val db = FirebaseService.db

    fun getLessonsByLevel(
        levelId: String,
        onSuccess: (List<Lesson>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("LessonTest", "Query lessons with levelId=$levelId")

        db.collection("lessons")
            .whereEqualTo("levelId", levelId)
            .orderBy("order")
            .get()
            .addOnSuccessListener { result ->
                Log.d("LessonTest", "Firestore result size = ${result.size()}")

                val lessons = result.documents.map { doc ->
                    Lesson(
                        id = doc.id, //  QUAN TRỌNG NHẤT
                        levelId = doc.getString("levelId") ?: "",
                        title = doc.getString("title") ?: "",
                        order = doc.getLong("order")?.toInt() ?: 0,
                        totalCards = doc.getLong("totalCards")?.toInt() ?: 0
                    )
                }
                onSuccess(lessons)

            }
            .addOnFailureListener {
                Log.e("LessonTest", "Firestore ERROR", it)
                onError(it.message ?: "Error")
            }
    }

}
