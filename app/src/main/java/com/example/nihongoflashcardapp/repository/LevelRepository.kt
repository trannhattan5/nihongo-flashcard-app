package com.example.nihongoflashcardapp.repository


import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.models.Level

class LevelRepository {

    private val db = FirebaseService.db

    fun getLevels(
        onSuccess: (List<Level>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("levels")
            .orderBy("order")
            .get()
            .addOnSuccessListener { result ->

                val levels = result.documents.map { doc ->
                    Level(
                        id = doc.id, // ⭐ QUAN TRỌNG NHẤT
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        order = doc.getLong("order")?.toInt() ?: 0
                    )
                }

                onSuccess(levels)
            }
            .addOnFailureListener {
                onError(it.message ?: "Load levels failed")
            }
    }

}
