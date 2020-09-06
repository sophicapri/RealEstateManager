package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity() {
    companion object {
        const val TAG = "LoginActivity"
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        next_button.setOnClickListener{
            if (isEmailInDatabase())
                updateUI()
        }
        connection_button.setOnClickListener{
            if (isEmailInDatabase())
                signIn()
            else
                createUser()
        }
    }

    private fun createUser() {
        auth.createUserWithEmailAndPassword(getEmailInput(), getPasswordInput())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        createUserInFirestore()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun createUserInFirestore() {

        val username = getCurrentUser()!!.displayName
        val uid = getCurrentUser()!!.uid
        val email = getCurrentUser()!!.email
        //val currentUser = EstateAgent(uid, username, urlPicture, email)

        //viewModel.createUser(currentUser)
        //
        startMainActivity()
    }

    private fun getPasswordInput(): String {
        return password_input.toString()
    }

    private fun getEmailInput(): String {
        return email_address_input.toString()
    }

    private fun isEmailInDatabase(): Boolean {
        return false
    }


    private fun updateUI() {
        new_account.visibility = VISIBLE
        add_profile_picture.visibility = VISIBLE
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(getEmailInput(), getPasswordInput())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        startMainActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
