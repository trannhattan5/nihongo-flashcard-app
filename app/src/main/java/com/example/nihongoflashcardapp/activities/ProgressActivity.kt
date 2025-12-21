package com.example.nihongoflashcardapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityProgressBinding
import com.example.nihongoflashcardapp.repository.ProgressRepository

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private val repo = ProgressRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo.loadProgress { total, remembered, notRemembered ->
            binding.txtTotal.text = "Tổng số từ: $total"
            binding.txtRemembered.text = "Đã nhớ: $remembered"
            binding.txtNotRemembered.text = "Chưa nhớ: $notRemembered"
        }
    }
}

