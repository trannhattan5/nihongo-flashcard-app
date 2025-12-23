package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.activities.LevelActivity
import com.example.nihongoflashcardapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

//    override fun onStart() {
//        super.onStart()
//        // Kiểm tra nếu user đã đăng nhập từ trước thì vào thẳng màn hình chính
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            goToMainActivity()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Chạy hoạt ảnh mượt mà khi mở app
        startAnimations()

        // 2. Xử lý nút Đăng nhập
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // 3. Xử lý chuyển sang màn hình Đăng ký
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 4. Xử lý Quên mật khẩu (Tùy chọn)
        // binding.tvForgotPassword.setOnClickListener { ... }
    }

    private fun performLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        // Kiểm tra hợp lệ
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }

        // Hiển thị trạng thái chờ trên nút bấm
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Đang đăng nhập..."

        // Đăng nhập với Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Chào mừng bạn trở lại!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    // Nếu thất bại
                    Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "ĐĂNG NHẬP"
                }
            }
    }

    private fun goToMainActivity() {
        // Thay MainActivity bằng tên màn hình chính sau khi đăng nhập của bạn
        val intent = Intent(this, LevelActivity::class.java)
        startActivity(intent)
        finish() // Đóng màn hình Login để không quay lại được khi nhấn Back
    }

    private fun startAnimations() {
        // Card đăng nhập trồi lên từ dưới
        binding.loginCard.alpha = 0f
        binding.loginCard.translationY = 200f
        binding.loginCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(900)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Header (Logo và Tiêu đề) hiện ra từ từ
        binding.headerContainer.alpha = 0f
        binding.headerContainer.animate()
            .alpha(1f)
            .setDuration(1200)
            .setStartDelay(300)
            .start()

        // Nút đăng ký hiện ra sau cùng
        binding.btnRegister.alpha = 0f
        binding.btnRegister.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(1000)
            .start()
    }
}