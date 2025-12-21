package com.example.nihongoflashcardapp.repository


import com.example.nihongoflashcardapp.firebase.FirebaseService

class AuthRepository {

    private val auth = FirebaseService.auth

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Login failed") }
    }

    fun getUserId(): String {
        return auth.currentUser?.uid.orEmpty()
    }
}
