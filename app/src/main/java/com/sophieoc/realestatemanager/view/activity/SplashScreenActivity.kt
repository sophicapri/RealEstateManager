package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity

class SplashScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isCurrentUserLogged()) startMainActivity() else startLoginActivity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_splash_screen
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}