package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityFlashcardBinding
import com.example.nihongoflashcardapp.models.Flashcard

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private var currentIndex = 0
    private lateinit var flashcards: List<Flashcard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRemembered.setOnClickListener {
            saveStatus("remembered")
            nextCard()
        }

        binding.btnNotRemembered.setOnClickListener {
            saveStatus("not_remembered")
            nextCard()
        }
    }

    private fun saveStatus(status: String) {
        // Save to user_progress
    }

    private fun nextCard() {
        currentIndex++
        if (currentIndex >= flashcards.size) {
            openReview()
        } else {
            showCard()
        }
    }

    private fun showCard() {
        val card = flashcards[currentIndex]
        binding.txtWord.text = card.word
        binding.txtMeaning.text = card.meaning
    }

    private fun openReview() {
        startActivity(Intent(this, ReviewActivity::class.java))
        finish()
    }
}
