package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity

class UserPropertiesActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_user_properties

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_user_properties, fragmentUser, fragmentUser.javaClass.simpleName).commit()

        configurePropertyDetailFragment()
    }
}