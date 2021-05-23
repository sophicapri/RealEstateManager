package com.sophieoc.realestatemanager.presentation.activity

import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivityUserPropertiesBinding

class UserPropertiesActivity : BaseActivity() {
    lateinit var binding: ActivityUserPropertiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserPropertiesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_user_properties, fragmentUser, fragmentUser.javaClass.simpleName)
            .commit()
        configurePropertyDetailFragment()
    }

    override fun getLayout() = binding.root
}