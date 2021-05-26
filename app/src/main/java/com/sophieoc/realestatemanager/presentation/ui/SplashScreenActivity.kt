package com.sophieoc.realestatemanager.presentation.ui

import android.os.Bundle
import com.sophieoc.realestatemanager.databinding.ActivitySplashScreenBinding
import com.sophieoc.realestatemanager.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
    lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        if (isCurrentUserLogged())
            startNewActivity(MainActivity::class.java)
        else
            startNewActivity(LoginActivity::class.java)
        finish()
    }

    override fun getLayout() = binding.root
}