package com.sophieoc.realestatemanager.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureViewModel()
        setContentView(this.getLayout())
    }

    private fun configureViewModel() {
        //
    }

    abstract fun getLayout(): Int

    fun isCurrentUserLogged(): Boolean {
        return true
    }
}