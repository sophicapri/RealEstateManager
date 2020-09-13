package com.sophieoc.realestatemanager.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val viewModel by viewModel<MyViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(getLayout())
    }

    abstract fun getLayout(): Int

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isCurrentUserLogged(): Boolean {
        return getCurrentUser() != null
    }
}