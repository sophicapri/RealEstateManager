package com.sophieoc.realestatemanager.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.view.fragment.MapFragment
import kotlinx.android.synthetic.main.fragment_map.*

class MapActivity : BaseActivity() {
    lateinit var locationManager: LocationManager
    override fun getLayout() = Pair(R.layout.activity_map, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_map, fragmentMap, fragmentMap.javaClass.simpleName).commit()
    }

    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }

    fun isLocationEnabled(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return false
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings) { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_LOCATION)}
                    .setNegativeButton(R.string.ignore, null)
                    .show()
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: granted")
            } else
                Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(MainActivity.TAG, "onActivityResult: location enabled")
        }
    }

    companion object {
        const val TAG = "LogMapActivity"
        const val REQUEST_CODE_LOCATION = 321
        const val REQUEST_CODE = 123
    }
}