package com.example.nihongoflashcardapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nihongoflashcardapp.databinding.ActivityRegisterBinding
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

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvBackToLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun performRegister() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPass = binding.edtConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPass) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Đang xử lý..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    saveUserToFirestore(uid, email)
                } else {
                    showError("Đăng ký thất bại: ${task.exception?.message}")
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
                // --- BƯỚC QUAN TRỌNG NHẤT Ở ĐÂY ---
                auth.signOut() // Đăng xuất ngay lập tức sau khi đăng ký thành công
                // ---------------------------------

                Toast.makeText(this, "Đăng ký thành công! Mời bạn đăng nhập lại.", Toast.LENGTH_LONG).show()

                // Chuyển về màn hình Login và xóa sạch các màn hình trước đó trong bộ nhớ
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                showError("Lỗi lưu dữ liệu: ${e.message}")
            }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        binding.btnRegister.isEnabled = true
        binding.btnRegister.text = "ĐĂNG KÝ NGAY"
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