package com.sophieoc.realestatemanager.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import org.koin.android.ext.android.get

abstract class BaseActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        configureViewModel()
        setContentView(getLayout())
    }

    private fun configureViewModel() {
        viewModel = get()
    }

    abstract fun getLayout(): Int

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isCurrentUserLogged(): Boolean {
        return getCurrentUser() != null
    }
}