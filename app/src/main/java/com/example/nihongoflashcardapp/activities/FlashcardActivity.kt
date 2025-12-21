package com.example.nihongoflashcardapp.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.databinding.ActivityFlashcardBinding
 import com.example.nihongoflashcardapp.models.Flashcard
import com.example.nihongoflashcardapp.repository.FlashcardRepository

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private val repository = FlashcardRepository()

    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex = 0
    private var lessonId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lessonId = intent.getStringExtra("LESSON_ID") ?: ""

        Log.d("FlashcardFlow", "FlashcardActivity started")
        Log.d("FlashcardFlow", "LESSON_ID = '$lessonId'")

        if (lessonId.isBlank()) {
            Log.e("FlashcardFlow", "LESSON_ID INVALID → STOP")
            finish()
            return
        }

        setupActions()
        loadFlashcards()
    }

    private fun setupActions() {
        binding.btnRemembered.setOnClickListener {
            saveStatus("remembered")
            nextCard()
        }

        binding.btnNotRemembered.setOnClickListener {
            saveStatus("not_remembered")
            nextCard()
        }
    }

    private fun loadFlashcards() {
        repository.getFlashcards(
            lessonId = lessonId,
            onSuccess = {
                Log.d("FlashcardFlow", "Received flashcards = ${it.size}")
                flashcards = it
                currentIndex = 0
                showCard()
            },
            onError = {
                Log.e("FlashcardFlow", it)
            }
        )
    }

    private fun showCard() {
        if (flashcards.isEmpty()) {
            Log.e("FlashcardFlow", "No flashcards to show")
            return
        }

        val card = flashcards[currentIndex]
        Log.d("FlashcardFlow", "Show card ${currentIndex + 1}/${flashcards.size}: ${card.word}")

        binding.txtWord.text = card.word
        binding.txtReading.text = card.reading
        binding.txtMeaning.text = card.meaning
    }

    private fun nextCard() {
        currentIndex++
        if (currentIndex < flashcards.size) {
            showCard()
        } else {
            Log.d("FlashcardFlow", "End of flashcards")
            finish()
        }
    }

    private fun saveStatus(status: String) {
        if (flashcards.isEmpty()) return

        val card = flashcards[currentIndex]
        repository.saveProgress(
            lessonId = lessonId,
            cardId = card.id,
            status = status
        )
    }
}
