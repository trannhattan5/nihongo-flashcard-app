package com.example.nihongoflashcardapp.repository


import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Lesson

class LessonRepository {

    private val db = FirebaseService.db

    fun getLessonsByLevel(
        levelId: String,
        onSuccess: (List<Lesson>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("lessons")
            .whereEqualTo("levelId", levelId)
            .orderBy("order")
            .get()
            .addOnSuccessListener {
                onSuccess(it.toObjects(Lesson::class.java))
            }
            .addOnFailureListener {
                onError(it.message ?: "Load lessons failed")
            }
    }
}
