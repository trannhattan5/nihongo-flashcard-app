package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nihongoflashcardapp.adapter.LevelAdapter
import com.example.nihongoflashcardapp.databinding.ActivityLevelBinding
import com.example.nihongoflashcardapp.firebase.FirebaseService
import com.example.nihongoflashcardapp.navigation.BottomNavHelper
import com.example.nihongoflashcardapp.repository.LevelRepository

class LevelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLevelBinding
    private val repository = LevelRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomNavHelper.setup(
            activity = this,
            bottomNav = binding.bottomNavigation,
            selectedItemId = binding.bottomNavigation.id
        )
        FirebaseService.db.collection("lessons")
            .get()
            .addOnSuccessListener {
                Log.d("FirestoreTest", "Connected. Lessons = ${it.size()}")
            }
            .addOnFailureListener {
                Log.e("FirestoreTest", "ERROR", it)
            }

        binding.rvLevels.layoutManager = LinearLayoutManager(this)

        loadLevels()
    }

    private fun loadLevels() {
        repository.getLevels(
            onSuccess = { levels ->
                binding.rvLevels.adapter = LevelAdapter(levels) { level ->
                    openLessons(level.id)
                }
            },
            onError = {
                // log / toast
            }
        )
    }

    private fun openLessons(levelId: String) {
        Log.d("LessonTest", "Sending LEVEL_ID = $levelId")

        val intent = Intent(this, LessonActivity::class.java)
        intent.putExtra("LEVEL_ID", levelId) // QUAN TRỌNG
        startActivity(intent)
    }
}
