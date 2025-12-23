    package com.example.nihongoflashcardapp.activities

    import android.content.Intent
    import android.os.Bundle
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import com.example.nihongoflashcardapp.R
    import com.example.nihongoflashcardapp.databinding.ActivityProgressBinding
    import com.example.nihongoflashcardapp.firebase.FirebaseService
    import com.example.nihongoflashcardapp.navigation.BottomNavHelper
    import com.example.nihongoflashcardapp.repository.ProgressRepository

    class ProgressActivity : AppCompatActivity() {

        private lateinit var binding: ActivityProgressBinding
        private val repo = ProgressRepository()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityProgressBinding.inflate(layoutInflater)
            setContentView(binding.root)
            BottomNavHelper.setup(
                activity = this,
                bottomNav = binding.bottomNavigation,
                selectedItemId = R.id.nav_progress
            )


            repo.loadProgress { total, remembered, notRemembered ->
                binding.txtTotal.text = "Tổng số từ: $total"
                binding.txtRemembered.text = "Đã nhớ: $remembered"
                binding.txtNotRemembered.text = "Chưa nhớ: $notRemembered"
            }
            binding.btnLogout.setOnClickListener {
                FirebaseService.auth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            loadUserInfo()

        }
        private fun loadUserInfo() {
            val user = FirebaseService.auth.currentUser ?: return
            binding.txtUserEmail.text = user.email ?: ""

            FirebaseService.db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name") ?: "Người học"
                    binding.txtUserName.text = "Xin chào, $name"
                }
        }

    }

