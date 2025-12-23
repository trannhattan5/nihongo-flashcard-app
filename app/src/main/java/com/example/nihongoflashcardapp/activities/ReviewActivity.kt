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

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var lessonId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        BottomNavHelper.setup(
            activity = this,
            bottomNav = binding.bottomNavigation,
            selectedItemId = binding.bottomNavigation.id
        )
        lessonId = intent.getStringExtra("LESSON_ID") ?: ""

        if (lessonId.isBlank()) {
            finish()
            return
        }

        binding.btnReviewRemembered.setOnClickListener {
            review("remembered")
        }

        binding.btnReviewNotRemembered.setOnClickListener {
            review("not_remembered")
        }
    }

    private fun review(status: String) {
        val intent = Intent(this, FlashcardActivity::class.java)
        intent.putExtra("LESSON_ID", lessonId)
        intent.putExtra("FILTER_STATUS", status)
        startActivity(intent)
        finish()
    }
}
