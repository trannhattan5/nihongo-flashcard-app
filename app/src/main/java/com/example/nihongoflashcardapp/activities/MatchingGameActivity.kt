package com.example.nihongoflashcardapp.activities

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityMatchingGameBinding
import com.example.nihongoflashcardapp.models.Flashcard
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class MatchingGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchingGameBinding
    private val db = FirebaseFirestore.getInstance()
    private var timer: CountDownTimer? = null

    private var selectedJpCard: MaterialCardView? = null
    private var selectedViCard: MaterialCardView? = null
    private var selectedJpData: Flashcard? = null
    private var selectedViData: Flashcard? = null

    private var correctCount = 0
    private var isGameActive = false

    private var currentLevel: String = "N5"
    private var currentLessonId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchingGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận dữ liệu từ Intent
        currentLevel = intent.getStringExtra("LEVEL_ID") ?: "N5"
        // Kiểm tra kỹ nếu lesson_id rỗng thì coi như là null để lọc theo Level
        val lessonIdIntent = intent.getStringExtra("LESSON_ID")
        currentLessonId = if (lessonIdIntent.isNullOrBlank()) null else lessonIdIntent

        // Chỉnh tiêu đề hiển thị
        binding.tvTitle.text = if (currentLessonId != null) "Thử thách theo Bài" else "Thử thách nối từ $currentLevel"

        loadData(currentLevel, currentLessonId)

        binding.btnReset.setOnClickListener {
            resetGame(currentLevel, currentLessonId)
        }
    }

    private fun loadData(level: String, lessonId: String?) {
        // LOGIC LỌC: Ưu tiên lọc theo Lesson, nếu không có mới lọc theo Level
        val query = if (lessonId != null) {
            db.collection("flashcards").whereEqualTo("lessonId", lessonId)
        } else {
            db.collection("flashcards").whereEqualTo("levelId", level)
        }

        query.get().addOnSuccessListener { result ->
            val allCards = result.documents.mapNotNull { doc ->
                val flashcard = doc.toObject(Flashcard::class.java)
                flashcard?.copy(id = doc.id)
            }

            if (allCards.size >= 4) {
                val gameData = allCards.shuffled().take(4)
                setupGame(gameData)
                startTimer()
            } else {
                val errorTarget = if (lessonId != null) "Bài học này" else "Cấp độ $level"
                Toast.makeText(this, "$errorTarget chưa đủ 4 từ để chơi!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi kết nối Firebase!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCorrectMatch() {
        selectedJpCard?.apply {
            setCardBackgroundColor(Color.parseColor("#C8E6C9")) // Màu xanh lá nhẹ
            setStrokeColor(Color.parseColor("#4CAF50"))
            isEnabled = false
        }
        selectedViCard?.apply {
            setCardBackgroundColor(Color.parseColor("#C8E6C9"))
            setStrokeColor(Color.parseColor("#4CAF50"))
            isEnabled = false
        }

        correctCount++

        if (correctCount == 4) {
            isGameActive = false
            timer?.cancel()

            // THÔNG BÁO ĐÃ THUỘC THEO BÀI HOẶC THEO CẤP ĐỘ
            val successMessage = if (currentLessonId != null)
                "Chúc mừng! Bạn đã thuộc hết từ vựng của bài học này."
            else
                "Chúc mừng! Bạn đã thuộc hết từ vựng của cấp độ $currentLevel."

            showGameOverDialog(successMessage, true)
        }
        clearSelection()
    }

    private fun showGameOverDialog(msg: String, isWin: Boolean) {
        isGameActive = false
        // Sử dụng MaterialAlertDialogBuilder để giao diện thông báo ĐẸP HƠN
        MaterialAlertDialogBuilder(this)
            .setTitle(if (isWin) "Hoàn thành!" else "⏰ Hết giờ!")
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Chơi lại") { _, _ ->
                resetGame(currentLevel, currentLessonId)
            }
            .setNegativeButton("Thoát") { _, _ -> finish() }
            .show()
    }

    // --- CÁC HÀM HỖ TRỢ GIỮ NGUYÊN NHƯNG ĐÃ ĐƯỢC TỐI ƯU ---

    private fun startTimer() {
        isGameActive = true
        timer?.cancel()
        timer = object : CountDownTimer(16000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimerText.text = "${millisUntilFinished / 1000}s"
                binding.timerProgress.progress = (millisUntilFinished / 100).toInt()
            }
            override fun onFinish() {
                if (isGameActive && correctCount < 4) {
                    showGameOverDialog("Bạn chưa hoàn thành kịp lúc. Thử lại nhé!", false)
                }
            }
        }.start()
    }

    private fun setupGame(cards: List<Flashcard>) {
        val jpCards = cards.shuffled()
        val viCards = cards.shuffled()
        binding.layoutJapanese.removeAllViews()
        binding.layoutVietnamese.removeAllViews()
        for (cardData in jpCards) {
            binding.layoutJapanese.addView(createCard(cardData.word, cardData, true))
        }
        for (cardData in viCards) {
            binding.layoutVietnamese.addView(createCard(cardData.meaning, cardData, false))
        }
    }

    private fun createCard(content: String, data: Flashcard, isJp: Boolean): View {
        val view = layoutInflater.inflate(R.layout.item_matching_card, null)
        val card = view.findViewById<MaterialCardView>(R.id.cardContainer)
        view.findViewById<MaterialTextView>(R.id.tvContent).text = content
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
        checkMatch()
    }

    private fun checkMatch() {
        if (selectedJpData != null && selectedViData != null) {
            if (selectedJpData!!.id == selectedViData!!.id) handleCorrectMatch()
            else handleWrongMatch()
        }
    }

    private fun handleWrongMatch() {
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

    private fun resetGame(level: String, lessonId: String?) {
        correctCount = 0
        isGameActive = false
        timer?.cancel()
        clearSelection()
        loadData(level, lessonId)
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