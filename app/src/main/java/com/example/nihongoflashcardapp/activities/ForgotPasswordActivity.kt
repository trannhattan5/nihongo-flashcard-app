package com.example.nihongoflashcardapp.activities

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.nihongoflashcardapp.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Hoạt ảnh
        startEnterAnimation()

        // Xóa lỗi khi gõ
        binding.edtEmail.doAfterTextChanged { binding.tilEmail.error = null }

        binding.btnSend.setOnClickListener {
            resetPassword()
        }

        binding.btnBackToLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun resetPassword() {
        val email = binding.edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Vui lòng nhập Email"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email không đúng định dạng"
            return
        }

        binding.btnSend.isEnabled = false
        binding.btnSend.text = "Đang gửi..."

        // Gửi email khôi phục mật khẩu từ Firebase
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSuccessSnackbar("Liên kết khôi phục đã được gửi tới Email của bạn! 🎉")
                    binding.btnSend.text = "ĐÃ GỬI"
                } else {
                    showErrorSnackbar("Lỗi: ${task.exception?.message}")
                    binding.btnSend.isEnabled = true
                    binding.btnSend.text = "GỬI YÊU CẦU"
                }
            }
    }

    private fun showSuccessSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
        snackbar.show()
    }

    private fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor("#E91E63"))

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(40, 0, 40, 100)
        snackbarView.layoutParams = params
        snackbarView.background = GradientDrawable().apply {
            cornerRadius = 30f
            setColor(Color.parseColor("#E91E63"))
        }
        snackbar.show()
    }

    private fun startEnterAnimation() {
        binding.forgotCard.alpha = 0f
        binding.forgotCard.translationY = 200f
        binding.forgotCard.animate()
            .alpha(1f).translationY(0f)
            .setDuration(800).setInterpolator(DecelerateInterpolator()).start()
    }
}