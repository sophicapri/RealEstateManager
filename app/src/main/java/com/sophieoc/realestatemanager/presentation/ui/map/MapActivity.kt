package com.sophieoc.realestatemanager.presentation.ui.map

import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivityMapBinding
import com.sophieoc.realestatemanager.presentation.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : BaseActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMapBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_map, MapFragment(), MapFragment().javaClass.simpleName)
            .commit()
        checkLocationEnabled()
        binding.myToolbar.setNavigationOnClickListener { onBackPressed()}
    }

    override fun getLayout() = binding.root

    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }

    companion object {
        const val TAG = "LogMapActivity"
        const val REQUEST_CODE = 123
    }
}