package com.sophieoc.realestatemanager.view.activity

import android.location.LocationManager
import android.os.Bundle
import android.view.View
import com.sophieoc.realestatemanager.base.BaseActivity

class MapActivity: BaseActivity() {
    private var locationManager: LocationManager? = null
    override fun getLayout(): Int {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }
}