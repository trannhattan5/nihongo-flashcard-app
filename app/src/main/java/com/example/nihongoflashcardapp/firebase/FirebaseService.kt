package com.example.nihongoflashcardapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirebaseService {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
//        FirebaseFirestore.getInstance().apply {
//            firestoreSettings = FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build()
//        }
    }

}