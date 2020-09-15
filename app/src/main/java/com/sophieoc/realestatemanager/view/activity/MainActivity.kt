package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.DATABASE_NAME
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.PropertyType

class MainActivity : BaseActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.currentUser.observe(this, Observer { 
            if (it != null) {
                Toast.makeText(this, " Hello ${it.user.username}" , LENGTH_LONG).show()
            }
        })
    }
}