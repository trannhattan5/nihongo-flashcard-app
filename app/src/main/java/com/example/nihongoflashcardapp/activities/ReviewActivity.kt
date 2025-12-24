package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityReviewBinding
import com.example.nihongoflashcardapp.navigation.BottomNavHelper
import com.example.nihongoflashcardapp.repository.ReviewRepository
import com.example.nihongoflashcardapp.utils.MODE_REVIEW

class ReviewActivity : AppCompatActivity() {

    private lateinit var lessonId: String
    private var lessonTotalCards = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lessonId = intent.getStringExtra("LESSON_ID") ?: run {
            finish(); return
        }

        binding.cardReviewRemembered.setOnClickListener {
            openReview("remembered")
        }

        binding.cardReviewNotRemembered.setOnClickListener {
            openReview("not_remembered")
        }
    }

    private fun openReview(status: String) {
        startActivity(
            Intent(this, FlashcardActivity::class.java)
                .putExtra("LESSON_ID", lessonId)
                .putExtra("MODE", MODE_REVIEW)
                .putExtra("FILTER_STATUS", status)
        )
    }

}

