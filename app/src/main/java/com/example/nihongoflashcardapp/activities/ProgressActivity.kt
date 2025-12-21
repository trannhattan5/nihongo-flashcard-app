package com.example.nihongoflashcardapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.databinding.ActivityProgressBinding

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Demo hiển thị dữ liệu
        binding.txtTotal.text = "Tổng số từ: 50"
        binding.txtRemembered.text = "Đã nhớ: 30"
        binding.txtNotRemembered.text = "Chưa nhớ: 20"
    }
}
