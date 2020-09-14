package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    companion object {
        const val TAG = "LoginActivity"
        const val RC_SIGN_IN = 123
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // change to google button
        google_sign_in_btn.setOnClickListener { startSignInWithGoogle()}
    }

    private fun startSignInWithGoogle() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK)
            handleResponseAfterSignIn(requestCode, resultCode, data)
    }

    private fun handleResponseAfterSignIn(requestCode: Int, resultCode: Int, data: Intent?) {
        val response: IdpResponse? = IdpResponse.fromResultIntent(data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                viewModel.getCurrentUser()?.observe(this, {
                    //println("login activity = " + it.user.username)
                  startMainActivity()
                })
            } else { // ERRORS
                    if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                       // Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show()
                    } else if (response?.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                       // Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
