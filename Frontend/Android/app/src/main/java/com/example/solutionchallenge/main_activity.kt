package com.example.solutionchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_camera -> {
                    replaceFragment(CameraMenuFragment())
                    true
                }
                R.id.nav_archive -> {
                    replaceFragment(ArchiveFragment())
                    true
                }
                R.id.nav_challenge -> {
                    replaceFragment(ChallengeFragment())
                    true
                }
                else -> false
            }
        }
    }

}
