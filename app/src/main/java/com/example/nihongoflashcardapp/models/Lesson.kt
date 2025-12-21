package com.example.nihongoflashcardapp.models

data class Lesson(
    val id: String = "",
    val levelId: String = "",
    val title: String = "",
    val order: Int = 0,
    val totalCards: Int = 0
)
