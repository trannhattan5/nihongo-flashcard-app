package com.example.nihongoflashcardapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
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

    // trạng thái mặt thẻ
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataFromIntent()
        setupActions()
        loadFlashcards()
    }

    /* ================= INIT ================= */

    private fun initDataFromIntent() {
        lessonId = intent.getStringExtra("LESSON_ID") ?: ""

        Log.d("FlashcardFlow", "FlashcardActivity started")
        Log.d("FlashcardFlow", "LESSON_ID = '$lessonId'")

        if (lessonId.isBlank()) {
            Log.e("FlashcardFlow", "LESSON_ID INVALID → FINISH")
            finish()
        }
    }

    /* ================= UI EVENTS ================= */

    private fun setupActions() {

        // Lật thẻ khi tap
        binding.cardFlash.setOnClickListener {
            flipCard()
        }

        binding.btnRemembered.setOnClickListener {
            saveStatus("remembered")
            nextCard()
        }

        binding.btnNotRemembered.setOnClickListener {
            saveStatus("not_remembered")
            nextCard()
        }
    }

    /* ================= LOAD DATA ================= */

    private fun loadFlashcards() {
        repository.getFlashcards(
            lessonId = lessonId,
            onSuccess = { list ->
                Log.d("FlashcardFlow", "Received flashcards = ${list.size}")
                flashcards = list
                currentIndex = 0
                showCard()
            },
            onError = {
                Log.e("FlashcardFlow", it)
            }
        )
    }

    /* ================= SHOW CARD ================= */

    private fun showCard() {
        if (flashcards.isEmpty() || currentIndex >= flashcards.size) {
            Log.e("FlashcardFlow", "No flashcards to show")
            return
        }

        val card = flashcards[currentIndex]

        resetCardState()

        binding.txtWord.text = card.word
        binding.txtReading.text = card.reading
        binding.txtMeaning.text = card.meaning

        updateProgress()
    }

    private fun resetCardState() {
        isFront = true
        binding.layoutFront.visibility = View.VISIBLE
        binding.layoutBack.visibility = View.GONE
        binding.cardFlash.rotationY = 0f
    }

    private fun updateProgress() {
        binding.txtProgressCounter.text =
            "${currentIndex + 1}/${flashcards.size}"

        binding.progressStudy.progress =
            ((currentIndex + 1) * 100) / flashcards.size
    }

    /* ================= FLIP CARD ================= */

    private fun flipCard() {
        val scale = resources.displayMetrics.density
        binding.cardFlash.cameraDistance = 8000 * scale

        val flipOut = ObjectAnimator.ofFloat(binding.cardFlash, "rotationY", 0f, 90f)
        val flipIn = ObjectAnimator.ofFloat(binding.cardFlash, "rotationY", -90f, 0f)

        flipOut.duration = 150
        flipIn.duration = 150

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                toggleCardSide()
                flipIn.start()
            }
        })

        flipOut.start()
    }

    private fun toggleCardSide() {
        if (isFront) {
            binding.layoutFront.visibility = View.GONE
            binding.layoutBack.visibility = View.VISIBLE
        } else {
            binding.layoutFront.visibility = View.VISIBLE
            binding.layoutBack.visibility = View.GONE
        }
        isFront = !isFront
    }

    /* ================= NAVIGATION ================= */

    private fun nextCard() {
        currentIndex++
        if (currentIndex < flashcards.size) {
            showCard()
        } else {
            Log.d("FlashcardFlow", "End of flashcards")
            finish()
        }
    }

    /* ================= SAVE PROGRESS ================= */

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
