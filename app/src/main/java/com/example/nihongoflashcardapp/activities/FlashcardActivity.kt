package com.example.nihongoflashcardapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.databinding.ActivityFlashcardBinding
import com.example.nihongoflashcardapp.models.Flashcard
import com.example.nihongoflashcardapp.repository.FlashcardRepository
import com.example.nihongoflashcardapp.repository.LessonProgressRepository
import com.example.nihongoflashcardapp.repository.ReviewRepository
import com.example.nihongoflashcardapp.utils.MODE_LEARN_NEW
import com.example.nihongoflashcardapp.utils.MODE_REVIEW

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding

    private val flashcardRepo = FlashcardRepository()
    private val lessonProgressRepo = LessonProgressRepository()
    private val reviewRepo = ReviewRepository()

    private var flashcards: List<Flashcard> = emptyList()
    private var currentIndex = 0

    private lateinit var lessonId: String
    private lateinit var mode: String
    private var reviewStatus: String? = null

    private var isFront = true

    /* ================= LIFECYCLE ================= */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initIntent()
        setupActions()
        loadFlashcards()
    }

    /* ================= INIT ================= */

    private fun initIntent() {
        lessonId = intent.getStringExtra("LESSON_ID") ?: run {
            finish(); return
        }

        mode = intent.getStringExtra("MODE") ?: MODE_LEARN_NEW
        reviewStatus = intent.getStringExtra("FILTER_STATUS")
    }

    /* ================= LOAD FLASHCARDS ================= */

    private fun loadFlashcards() {
        when (mode) {

            MODE_LEARN_NEW -> {
                flashcardRepo.getUnlearnedFlashcards(
                    lessonId = lessonId
                ) { cards ->
                    flashcards = cards
                    currentIndex = 0
                    loadLessonSummary()
                    showCard()
                }
            }

            MODE_REVIEW -> {
                reviewRepo.getReviewFlashcards(
                    lessonId = lessonId,
                    status = reviewStatus ?: return
                ) { cards ->
                    flashcards = cards
                    currentIndex = 0
                    loadLessonSummary()
                    showCard()
                }
            }
        }
    }

    /* ================= UI EVENTS ================= */

    private fun setupActions() {
        binding.cardFlash.setOnClickListener { flipCard() }

        binding.btnRemembered.setOnClickListener {
            handleAnswer("remembered")
        }

        binding.btnNotRemembered.setOnClickListener {
            handleAnswer("not_remembered")
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    /* ================= HANDLE ANSWER ================= */

    private fun handleAnswer(status: String) {
        val card = flashcards[currentIndex]

        // 1. Lưu Firestore
        flashcardRepo.saveProgress(
            lessonId = lessonId,
            cardId = card.id,
            status = status
        )

        // 2. REVIEW MODE → loại thẻ khỏi session
        if (mode == MODE_REVIEW) {
            flashcards = flashcards.toMutableList().apply {
                removeAt(currentIndex)
            }
            // không tăng index
        } else {
            // LEARN MODE → sang thẻ tiếp
            currentIndex++
        }

        // 3. Reload thống kê lesson (✔ ✖ ⏳)
        loadLessonSummary()

        // 4. Show tiếp hoặc kết thúc
        showCard()
    }


    /* ================= SHOW CARD ================= */

    private fun showCard() {
        if (flashcards.isEmpty() || currentIndex >= flashcards.size) {
            finish()
            return
        }

        val card = flashcards[currentIndex]
        resetCard()

        binding.txtWord.text = card.word
        binding.txtReading.text = card.reading
        binding.txtMeaning.text = card.meaning

        updateSessionProgress()
    }

    private fun resetCard() {
        isFront = true
        binding.layoutFront.visibility = View.VISIBLE
        binding.layoutBack.visibility = View.GONE
        binding.cardFlash.rotationY = 0f
    }

    /* ================= SESSION PROGRESS ================= */

    private fun updateSessionProgress() {
        val total = flashcards.size
        val current = currentIndex + 1

        binding.txtProgressCounter.text = "$current / $total"
        binding.progressStudy.progress =
            if (total == 0) 0 else (current * 100 / total)
    }

    /* ================= LESSON SUMMARY ================= */

    private fun loadLessonSummary() {
        flashcardRepo.getAllFlashcards(
            lessonId = lessonId,
            onSuccess = { allCards ->
                lessonProgressRepo.loadLessonProgress(
                    lessonId = lessonId,
                    totalCards = allCards.size
                ) { remembered, notRemembered, notLearned ->
                    binding.txtRememberedCount.text = "✔ $remembered"
                    binding.txtNotRememberedCount.text = "✖ $notRemembered"
                    binding.txtRemainingCount.text = "⏳ $notLearned"
                }
            },
            onError = { }
        )
    }

    /* ================= FLIP ================= */

    private fun flipCard() {
        val scale = resources.displayMetrics.density
        binding.cardFlash.cameraDistance = 8000 * scale

        val out = ObjectAnimator.ofFloat(binding.cardFlash, "rotationY", 0f, 90f)
        val `in` = ObjectAnimator.ofFloat(binding.cardFlash, "rotationY", -90f, 0f)

        out.duration = 150
        `in`.duration = 150

        out.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                toggleSide()
                `in`.start()
            }
        })
        out.start()
    }

    private fun toggleSide() {
        isFront = !isFront
        binding.layoutFront.visibility = if (isFront) View.VISIBLE else View.GONE
        binding.layoutBack.visibility = if (isFront) View.GONE else View.VISIBLE
    }





}
