package com.example.nihongoflashcardapp.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.databinding.ActivityLoadingBinding

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startLoading()
    }

    private fun startLoading() {
        // Animator chạy từ 0 đến 100
        val animator = ValueAnimator.ofInt(0, 100)
        animator.duration = 2000 // Thời gian loading (2 giây)
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            // Cập nhật thanh ProgressBar
            binding.progressBar.progress = progress
            // Cập nhật số phần trăm hiển thị
            binding.txtProgressPercent.text = "$progress%"
        }

        // Khi chạy xong thì chuyển trang
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                val intent = Intent(this@LoadingActivity, LevelActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        })

        animator.start()
    }
}