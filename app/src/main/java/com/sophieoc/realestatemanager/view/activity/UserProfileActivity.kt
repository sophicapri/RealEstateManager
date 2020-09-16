package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.view.View
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity

class UserProfileActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_user_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_user_profile, fragmentUser, fragmentUser.javaClass.simpleName).commit()

        configurePropertyDetailFragment()
    }
}