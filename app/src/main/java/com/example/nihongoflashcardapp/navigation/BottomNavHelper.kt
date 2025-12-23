package com.example.nihongoflashcardapp.navigation

import android.app.Activity
import android.content.Intent
import com.example.nihongoflashcardapp.R
import com.example.nihongoflashcardapp.activities.*
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavHelper {

    fun setup(
        activity: Activity,
        bottomNav: BottomNavigationView,
        selectedItemId: Int
    ) {
        bottomNav.selectedItemId = selectedItemId

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == selectedItemId) return@setOnItemSelectedListener true

            val intent = when (item.itemId) {
                R.id.nav_level -> Intent(activity, LevelActivity::class.java)
                R.id.nav_review -> Intent(activity, ReviewActivity::class.java)
                R.id.nav_progress -> Intent(activity, ProgressActivity::class.java)
                else -> null
            }

            intent?.let {
                activity.startActivity(it)
                activity.overridePendingTransition(0, 0)
            }
            true
        }
    }
}
