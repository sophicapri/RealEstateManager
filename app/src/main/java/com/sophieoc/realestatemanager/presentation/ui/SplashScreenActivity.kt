package com.sophieoc.realestatemanager.presentation.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivitySplashScreenBinding
import com.sophieoc.realestatemanager.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller?.hide(WindowInsets.Type.statusBars())
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.appLogo.animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isCurrentUserLogged())
                startNewActivity(MainActivity::class.java)
            else
                startNewActivity(LoginActivity::class.java)
            finish()
        }, 5000)
    }

    override fun getLayout(): View {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        return binding.root
    }
}