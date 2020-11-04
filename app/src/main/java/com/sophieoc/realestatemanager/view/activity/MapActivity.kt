package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.view.fragment.MapFragment

class MapActivity : BaseActivity() {
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

    companion object {
        const val TAG = "LogMapActivity"
        const val REQUEST_CODE_LOCATION = 321
        const val REQUEST_CODE = 123
    }
}