package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivityMapBinding
import com.sophieoc.realestatemanager.view.fragment.MapFragment

class MapActivity : BaseActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMapBinding.inflate(layoutInflater)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_map, MapFragment(), MapFragment().javaClass.simpleName)
            .commit()
        checkLocationEnabled()
        binding.myToolbar.setNavigationOnClickListener { onBackPressed()}
        super.onCreate(savedInstanceState)
    }

    override fun getLayout() = binding.root

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