package com.sophieoc.realestatemanager.presentation.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivityLoginBinding
import com.sophieoc.realestatemanager.presentation.BaseActivity
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    val userViewModel by viewModels<UserViewModel>()
    private lateinit var binding : ActivityLoginBinding
    private lateinit var progressBar : ProgressBar
    private val startForResult = registerForActivityResult(StartActivityForResult()){ activityResult ->
            val response: IdpResponse? = IdpResponse.fromResultIntent(activityResult.data)
            handleResponseAfterSignIn(activityResult.resultCode, response)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        progressBar = findViewById(R.id.progress_bar)
        val googleBtn = findViewById<ConstraintLayout>(R.id.google_sign_in_btn)
        googleBtn.setOnClickListener { startSignInWithGoogle()}
    }

    override fun getLayout() = binding.root

    private fun startSignInWithGoogle() {
        if (PreferenceHelper.internetAvailable) {
            startForResult.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(listOf(IdpConfig.GoogleBuilder().build()))
                    .setIsSmartLockEnabled(false, true)
                    .build())
            progressBar.visibility = VISIBLE
        } else
            Toast.makeText(this, getString(R.string.please_connect_to_internet), LENGTH_LONG).show()
    }

    private fun handleResponseAfterSignIn(resultCode: Int ,response: IdpResponse?) {
            if (resultCode == RESULT_OK) {
                userViewModel.currentUser.observe(this, {
                    startNewActivity(MainActivity::class.java)
                    progressBar.visibility = GONE
                    finish()
                })
            } else {
                    if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, getString(R.string.internet_unavailable), Toast.LENGTH_SHORT).show()
                    } else if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    }
            }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}
