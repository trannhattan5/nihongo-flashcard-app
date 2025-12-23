package com.example.nihongoflashcardapp.models

data class UserProgress(
     val userId: String = "",
    val lessonId: String = "",
    val cardId: String = "",
    val status: String = "",
    val updatedAt: Long = 0L
)
