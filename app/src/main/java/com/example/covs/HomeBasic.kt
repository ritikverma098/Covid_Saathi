package com.example.covs


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeBasic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_basic)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentFrame) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavBar!!.setupWithNavController(navController)

    }




}