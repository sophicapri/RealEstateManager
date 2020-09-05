package com.sophieoc.realestatemanager.base

import android.location.LocationManager
import android.os.Bundle
import android.view.View
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

    abstract fun getLayout(): View

    fun isCurrentUserLogged(): Boolean {
        return true
    }
}