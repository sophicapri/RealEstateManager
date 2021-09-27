package com.sophieoc.realestatemanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivitySplashScreenBinding
import com.sophieoc.realestatemanager.util.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.hideStatusBar(this)
        binding.appLogo.animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isCurrentUserLogged())
                startNewActivity(MainActivity::class.java)
            else
                startNewActivity(LoginActivity::class.java)
            finish()
        }, 3000)
    }

    override fun getLayout(): View {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        return binding.root
    }
}