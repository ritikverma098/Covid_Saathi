package com.example.covs


import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_home_basic.*

class HomeBasic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_basic)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavBar!!.setupWithNavController(navController)

    }
}