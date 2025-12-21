package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityLevelBinding

class LevelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khi user click level
        openLessons("N5")
    }

    private fun openLessons(levelId: String) {
        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra("LEVEL_ID", levelId)
        startActivity(intent)
    }
}
