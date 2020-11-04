package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.util.Log
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.view.fragment.MapFragment

class MapActivity : BaseActivity() {
    var activityRestarted = false
    override fun getLayout() = Pair(R.layout.activity_map, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_map, MapFragment(), MapFragment().javaClass.simpleName).commit()
    }

    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        activityRestarted = true
        Log.d(TAG, "onRestoreInstanceState: ")
    }

    companion object {
        const val TAG = "LogMapActivity"
        const val REQUEST_CODE_LOCATION = 321
        const val REQUEST_CODE = 123
    }
}