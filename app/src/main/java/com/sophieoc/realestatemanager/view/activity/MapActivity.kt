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
    lateinit var locationManager: LocationManager

    override fun getLayout() = R.layout.activity_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_map, fragmentMap, fragmentMap.javaClass.simpleName).commit()

        configurePropertyDetailFragment()
    }

  /*  private fun configurePropertyDetailFragment() {
        val propertyDetailView = findViewById<View?>(R.id.frame_property_details)
        var fragment = supportFragmentManager.findFragmentById(R.id.frame_property_details)

        if (fragment == null && propertyDetailView != null) {
            fragment = fragmentProfileDetail
            val fm = supportFragmentManager.beginTransaction()
            fm.add(R.id.frame_property_details, fragment).commit()
        }
    }

   */
}