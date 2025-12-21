package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityLessonBinding

class LessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLessonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lessonId = "lesson01"
        openFlashcards(lessonId)
    }

    private fun openFlashcards(lessonId: String) {
        val intent = Intent(this, FlashcardActivity::class.java)
        intent.putExtra("LESSON_ID", lessonId)
        startActivity(intent)
    }
}
