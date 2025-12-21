package com.example.nihongoflashcardapp.models

data class Flashcard(
    val id: String = "",
    val lessonId: String = "",
    val word: String = "",
    val reading: String = "",
    val meaning: String = "",
    val example: String = ""
)
