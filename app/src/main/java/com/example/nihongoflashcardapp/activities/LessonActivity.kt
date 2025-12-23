package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nihongoflashcardapp.adapter.LessonAdapter
import com.example.nihongoflashcardapp.databinding.ActivityLessonBinding
import com.example.nihongoflashcardapp.repository.LessonRepository

class LessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLessonBinding
    private val repository = LessonRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val levelId = intent.getStringExtra("LEVEL_ID")

        Log.d("LessonTest", "LEVEL_ID = $levelId")

        if (levelId == null) {
            Log.e("LessonTest", "LEVEL_ID is NULL")
            return
        }
        binding.txtLevel.text = "Level $levelId"


        setupRecyclerView()
        loadLessons(levelId)
    }

    private fun setupRecyclerView() {
        binding.rvLessons.layoutManager = LinearLayoutManager(this)

    }

    private fun loadLessons(levelId: String) {
        repository.getLessonsByLevel(
            levelId,
            onSuccess = { lessons ->
                binding.rvLessons.adapter = LessonAdapter(
                    lessons,
                    onLearn = { lesson ->
                        openFlashcards(lesson.id)
                    },
                    onReview = { lesson ->
                        openReview(lesson.id)
                    }
                )
            },
            onError = { }
        )
    }


    private val REQUEST_LEARN = 1001

    private fun openFlashcards(lessonId: String) {
        startActivityForResult(
            Intent(this, FlashcardActivity::class.java)
                .putExtra("LESSON_ID", lessonId),
            REQUEST_LEARN
        )
    }


    private fun openReview(lessonId: String) {
        startActivity(
            Intent(this, ReviewActivity::class.java)
                .putExtra("LESSON_ID", lessonId)
        )
    }




}
