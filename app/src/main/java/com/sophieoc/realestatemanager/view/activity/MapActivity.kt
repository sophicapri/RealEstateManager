package com.sophieoc.realestatemanager.view.activity

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.view.fragment.MapFragment
import kotlinx.android.synthetic.main.fragment_map.*

class MapActivity : BaseActivity() {
    override fun getLayout() = Pair(R.layout.activity_map, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_map, fragmentMap, fragmentMap.javaClass.simpleName).commit()
    }


    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }
}