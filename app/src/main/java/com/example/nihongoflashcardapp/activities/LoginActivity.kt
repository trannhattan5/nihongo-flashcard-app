package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged // Quan trọng: Thêm dòng này để dùng hàm rút gọn
import com.example.nihongoflashcardapp.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 1. Chạy hoạt ảnh mượt mà khi mở app
        startAnimations()

        // 2. Thiết lập tự động xóa lỗi khi người dùng gõ lại (Tăng UX)
        setupErrorClearing()

        // 3. Xử lý nút Đăng nhập
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // 4. Xử lý chuyển sang màn hình Đăng ký
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupErrorClearing() {
        // Sử dụng doAfterTextChanged để tránh lỗi mismatch type (TextWatcher)
        binding.edtEmail.doAfterTextChanged {
            binding.tilEmail.error = null
        }
        binding.edtPassword.doAfterTextChanged {
            binding.tilPassword.error = null
        }
    }

    private fun performLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        // 1. Xóa thông báo lỗi cũ trên LAYOUT bọc ngoài
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // 2. Kiểm tra dữ liệu đầu vào
        if (email.isEmpty()) {
            binding.tilEmail.error = "Vui lòng nhập Email"
            binding.edtEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Vui lòng nhập mật khẩu"
            binding.edtPassword.requestFocus()
            return
        }

        // Trạng thái chờ
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Đang xử lý..."

        // 3. Đăng nhập với Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showSuccessSnackbar("Chào mừng bạn trở lại! 🎉")
                    goToMainActivity()
                } else {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "ĐĂNG NHẬP"

                    // 4. HIỂN THỊ LỖI CHUẨN (Hiện chữ đỏ ở dưới khung)
                    showErrorSnackbar("Email hoặc mật khẩu không chính xác!")

                    // Báo lỗi trên Layout để không đè icon chấm than lên icon con mắt
                    binding.tilEmail.error = "Kiểm tra lại email"
                    binding.tilPassword.error = "Kiểm tra lại mật khẩu"
                }
            }
    }

    private fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor("#E91E63"))
        snackbar.setTextColor(Color.WHITE)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(40, 0, 40, 100)
        snackbarView.layoutParams = params
        snackbarView.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f
            setColor(Color.parseColor("#E91E63"))
        }
        snackbar.show()
    }

    private fun showSuccessSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
        snackbar.show()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, LevelActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startAnimations() {
        binding.loginCard.alpha = 0f
        binding.loginCard.translationY = 200f
        binding.loginCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(900)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.headerContainer.alpha = 0f
        binding.headerContainer.animate()
            .alpha(1f)
            .setDuration(1200)
            .setStartDelay(300)
            .start()

        binding.btnRegister.alpha = 0f
        binding.btnRegister.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(1000)
            .start()
    }
}