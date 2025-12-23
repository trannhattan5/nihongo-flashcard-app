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
import com.example.nihongoflashcardapp.repository.LessonProgressRepository
import com.example.nihongoflashcardapp.repository.ReviewRepository

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private val repository = FlashcardRepository()

    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex = 0
    private var lessonId: String = ""
    private var filterStatus: String? = null

    private var rememberedCount = 0
    private var notRememberedCount = 0

    // trạng thái mặt thẻ
    private var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataFromIntent()
        setupActions()
        loadFlashcards()
        filterStatus = intent.getStringExtra("FILTER_STATUS")

    }

    /* ================= INIT ================= */

    private fun initDataFromIntent() {
        filterStatus = intent.getStringExtra("FILTER_STATUS")
        lessonId = intent.getStringExtra("LESSON_ID") ?: ""

        Log.d("FlashcardFlow", "FILTER_STATUS = $filterStatus")
        Log.d("FlashcardFlow", "LESSON_ID = $lessonId")

        // Chỉ yêu cầu lessonId khi KHÔNG phải review
        if (filterStatus == null && lessonId.isBlank()) {
            Log.e("FlashcardFlow", "LESSON_ID REQUIRED")
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
            rememberedCount++
            saveStatus("remembered")
            nextCard()
        }

        binding.btnNotRemembered.setOnClickListener {
            notRememberedCount++

            saveStatus("not_remembered")
            nextCard()
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    /* ================= LOAD DATA ================= */

    private fun loadFlashcards() {
        repository.getFlashcards(
            lessonId,
            onSuccess = { cards ->
                flashcards = cards

                LessonProgressRepository().loadLessonProgress(
                    lessonId = lessonId,
                    totalCards = cards.size
                ) { remembered, notRemembered, _ ->
                    rememberedCount = remembered
                    notRememberedCount = notRemembered

                    // Quan trọng: xác định thẻ chưa học
                    currentIndex = remembered + notRemembered

                    showCard()
                    updateRealtimeProgress()
                }
            },
            onError = { }
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
        updateRealtimeProgress()

        currentIndex++
        if (currentIndex < flashcards.size) {
            showCard()
        } else {
            setResult(RESULT_OK)
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
    private fun updateRealtimeProgress() {
        val remaining = flashcards.size - rememberedCount - notRememberedCount

        binding.txtRememberedCount.text = "✔ $rememberedCount"
        binding.txtNotRememberedCount.text = "✖ $notRememberedCount"
        binding.txtRemainingCount.text = "⏳ $remaining"

        binding.progressStudy.progress =
            ((rememberedCount + notRememberedCount) * 100) / flashcards.size
    }




}
