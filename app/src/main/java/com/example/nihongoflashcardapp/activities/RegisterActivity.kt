package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.nihongoflashcardapp.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        startEnterAnimation()
        setupErrorClearing()

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvBackToLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupErrorClearing() {
        // Tự động xóa lỗi khi người dùng bắt đầu gõ lại
        binding.edtEmail.doAfterTextChanged { binding.tilEmail.error = null }
        binding.edtPassword.doAfterTextChanged { binding.tilPassword.error = null }
        binding.edtConfirmPassword.doAfterTextChanged { binding.tilConfirmPassword.error = null }
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun performRegister() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPass = binding.edtConfirmPassword.text.toString().trim()

        // 1. Xóa lỗi cũ
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        // 2. Kiểm tra dữ liệu (Validation)
        if (email.isEmpty()) {
            binding.tilEmail.error = "Vui lòng nhập Email"
            return
        }
        else if (!isValidEmail(email)) {
            binding.tilEmail.error = "Email không hợp lệ (Ví dụ: abc@gmail.com)"
            showErrorSnackbar("Email sai định dạng!")
            return
        }
        if (password.length < 8) { // CẬP NHẬT: Kiểm tra 8 ký tự
            binding.tilPassword.error = "Mật khẩu phải từ 8 ký tự trở lên"
            showErrorSnackbar("Mật khẩu quá ngắn!")
            return
        }

        if (confirmPass != password) {
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            return
        }

        // Hiển thị trạng thái đang xử lý
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Đang tạo tài khoản..."

        // 3. Tạo tài khoản trên Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    saveUserToFirestore(uid, email)
                } else {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "ĐĂNG KÝ NGAY"
                    showErrorSnackbar("Lỗi: ${task.exception?.message}")
                }
            }
    }

    private fun saveUserToFirestore(uid: String, email: String) {
        val userMap = hashMapOf(
            "uid" to uid,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid)
            .set(userMap)
            .addOnSuccessListener {
                auth.signOut() // Không tự động đăng nhập

                showSuccessSnackbar("Đăng ký thành công! Mời bạn đăng nhập. 🎉")

                // Đợi 1 chút cho người dùng kịp nhìn thấy thông báo thành công
                binding.root.postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }, 1500)
            }
            .addOnFailureListener { e ->
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "ĐĂNG KÝ NGAY"
                showErrorSnackbar("Lỗi lưu dữ liệu: ${e.message}")
            }
    }

    // Hàm hiển thị Thông báo lỗi ĐẸP
    private fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor("#E91E63")) // Màu hồng đỏ nổi bật
        snackbar.setTextColor(Color.WHITE)

        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(40, 0, 40, 100) // Cách lề và cách đáy
        snackbarView.layoutParams = params
        snackbarView.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f // Bo góc
            setColor(Color.parseColor("#E91E63"))
        }
        snackbar.show()
    }

    // Hàm hiển thị Thông báo thành công ĐẸP
    private fun showSuccessSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#4CAF50")) // Màu xanh lá
        snackbar.show()
    }

    private fun startEnterAnimation() {
        binding.registerCard.alpha = 0f
        binding.registerCard.translationY = 200f
        binding.registerCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(900)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.headerContainer.alpha = 0f
        binding.headerContainer.animate()
            .alpha(1f)
            .setDuration(1200)
            .setStartDelay(200)
            .start()
    }
}