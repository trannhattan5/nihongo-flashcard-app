package com.example.nihongoflashcardapp.activities

import android.graphics.Color
import android.os.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityMatchingGameBinding
import com.example.nihongoflashcardapp.models.Flashcard
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore

class MatchingGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchingGameBinding
    private val db = FirebaseFirestore.getInstance()
    private var timer: CountDownTimer? = null

    private var selectedJpCard: MaterialCardView? = null
    private var selectedViCard: MaterialCardView? = null
    private var selectedJpData: Flashcard? = null
    private var selectedViData: Flashcard? = null

    // Quản lý từ vựng
    private var fullVocabList = mutableListOf<Flashcard>()
    private var remainingVocabList = mutableListOf<Flashcard>()
    private var correctInRound = 0
    private var totalMatched = 0

    private var isGameActive = false
    private var currentLevel: String = "N5"
    private var currentLessonId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchingGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLevel = intent.getStringExtra("LEVEL_ID") ?: "N5"
        currentLessonId = intent.getStringExtra("LESSON_ID")

        loadDataFromFirebase()
        binding.btnReset.setOnClickListener { resetFullGame() }
    }

    private fun loadDataFromFirebase() {
        // Lọc nghiêm ngặt theo Lesson hoặc Level
        val query = if (!currentLessonId.isNullOrEmpty()) {
            db.collection("flashcards").whereEqualTo("lessonId", currentLessonId)
        } else {
            db.collection("flashcards").whereEqualTo("levelId", currentLevel)
        }

        query.get().addOnSuccessListener { result ->
            fullVocabList = result.documents.mapNotNull { doc ->
                // Thủ công gán ID để so sánh đúng sai chính xác 100%
                doc.toObject(Flashcard::class.java)?.copy(id = doc.id)
            }.toMutableList()

            if (fullVocabList.size >= 2) {
                binding.overallProgress.max = fullVocabList.size
                resetFullGame()
            } else {
                Toast.makeText(this, "Không đủ từ vựng để chơi!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startNextRound() {
        correctInRound = 0
        isGameActive = true
        binding.layoutJapanese.removeAllViews()
        binding.layoutVietnamese.removeAllViews()
        updateProgressUI()

        // Lấy tối đa 4 từ từ danh sách còn lại
        remainingVocabList.shuffle()
        val roundSize = if (remainingVocabList.size >= 4) 4 else remainingVocabList.size
        val roundCards = remainingVocabList.take(roundSize)

        // Trộn UI
        val jpCards = roundCards.shuffled()
        val viCards = roundCards.shuffled()

        for (data in jpCards) binding.layoutJapanese.addView(createCard(data.word, data, true))
        for (data in viCards) binding.layoutVietnamese.addView(createCard(data.meaning, data, false))

        startTimer()
    }

    private fun createCard(content: String, data: Flashcard, isJp: Boolean): View {
        val parent = if (isJp) binding.layoutJapanese else binding.layoutVietnamese
        val view = layoutInflater.inflate(R.layout.item_matching_card, parent, false)
        val card = view.findViewById<MaterialCardView>(R.id.cardContainer)
        card.findViewById<android.widget.TextView>(R.id.tvContent).text = content

        card.setOnClickListener { if (isGameActive) handleSelection(card, data, isJp) }
        return view
    }

    private fun handleSelection(card: MaterialCardView, data: Flashcard, isJp: Boolean) {
        if (!card.isEnabled) return

        if (isJp) {
            resetStyle(selectedJpCard)
            selectedJpCard = card
            selectedJpData = data
        } else {
            resetStyle(selectedViCard)
            selectedViCard = card
            selectedViData = data
        }

        card.setStrokeColor(Color.parseColor("#6200EE"))
        card.setCardBackgroundColor(Color.parseColor("#F3E5F5"))

        if (selectedJpData != null && selectedViData != null) {
            if (selectedJpData!!.id == selectedViData!!.id) handleCorrect()
            else handleWrong()
        }
    }

    private fun handleCorrect() {
        selectedJpCard?.apply {
            setCardBackgroundColor(Color.parseColor("#C8E6C9"))
            setStrokeColor(Color.parseColor("#4CAF50"))
            isEnabled = false
        }
        selectedViCard?.apply {
            setCardBackgroundColor(Color.parseColor("#C8E6C9"))
            setStrokeColor(Color.parseColor("#4CAF50"))
            isEnabled = false
        }

        remainingVocabList.remove(selectedJpData)
        correctInRound++
        totalMatched++
        updateProgressUI()

        val currentRoundLimit = if (remainingVocabList.size + correctInRound < 4) remainingVocabList.size + correctInRound else 4

        if (correctInRound >= currentRoundLimit) {
            timer?.cancel()
            if (remainingVocabList.isEmpty()) {
                val endMsg = if (!currentLessonId.isNullOrEmpty()) "Bạn đã thuộc toàn bộ bài học!" else "Bạn đã thuộc hết cấp độ $currentLevel!"
                showGameOverDialog(endMsg, true)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({ startNextRound() }, 600)
            }
        }
        clearSelection()
    }

    private fun handleWrong() {
        val c1 = selectedJpCard
        val c2 = selectedViCard
        c1?.setStrokeColor(Color.RED)
        c2?.setStrokeColor(Color.RED)
        Handler(Looper.getMainLooper()).postDelayed({
            resetStyle(c1)
            resetStyle(c2)
        }, 500)
        clearSelection()
    }

    private fun updateProgressUI() {
        binding.tvProgressText.text = "Tiến độ: $totalMatched / ${fullVocabList.size}"
        binding.overallProgress.setProgress(totalMatched, true)
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(16000, 100) {
            override fun onTick(millis: Long) {
                binding.tvTimerText.text = "${millis / 1000}s"
                binding.timerProgress.progress = (millis / 100).toInt()
            }
            override fun onFinish() {
                if (isGameActive) showGameOverDialog("Hết thời gian! Bạn đã nối được $totalMatched câu.", false)
            }
        }.start()
    }

    private fun showGameOverDialog(msg: String, isWin: Boolean) {
        isGameActive = false
        MaterialAlertDialogBuilder(this)
            .setTitle(if (isWin) "🎉 Hoàn thành!" else "⏰ Kết thúc")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Chơi lại bài này") { _, _ -> resetFullGame() }
            .setNegativeButton("Thoát") { _, _ -> finish() }
            .show()
    }

    private fun resetFullGame() {
        totalMatched = 0
        remainingVocabList = fullVocabList.toMutableList()
        updateProgressUI()
        startNextRound()
    }

    private fun clearSelection() {
        selectedJpCard = null; selectedViCard = null; selectedJpData = null; selectedViData = null
    }

    private fun resetStyle(card: MaterialCardView?) {
        if (card != null && card.isEnabled) {
            card.setStrokeColor(Color.parseColor("#E0E0E0"))
            card.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}