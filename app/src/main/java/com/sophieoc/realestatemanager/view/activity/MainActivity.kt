package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.view.fragment.MapFragment
import com.sophieoc.realestatemanager.view.fragment.PropertyListFragment

class MainActivity : BaseActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_property_list, fragmentList, fragmentList.javaClass.simpleName).commit()

        configurePropertyDetailFragment()
    }
}