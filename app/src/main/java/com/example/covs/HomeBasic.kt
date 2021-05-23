package com.example.covs


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeBasic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_basic)
        val fManager = supportFragmentManager
        val transaction = fManager.beginTransaction()
        transaction.add(R.id.topAppBar, HomeFragment())
        transaction.commitAllowingStateLoss()

    }




}