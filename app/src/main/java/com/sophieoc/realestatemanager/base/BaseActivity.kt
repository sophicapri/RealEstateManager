package com.sophieoc.realestatemanager.base

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.view.activity.MainActivity
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.view.fragment.MapFragment
import com.sophieoc.realestatemanager.view.fragment.PropertyDetailFragment
import com.sophieoc.realestatemanager.view.fragment.PropertyListFragment
import com.sophieoc.realestatemanager.view.fragment.UserPropertiesFragment

abstract class BaseActivity : AppCompatActivity(), PropertyListAdapter.OnPropertyClickListener {
    lateinit var auth: FirebaseAuth
    private lateinit var locationManager: LocationManager
    val fragmentMap = MapFragment()
    val fragmentList = PropertyListFragment()
    val fragmentUser = UserPropertiesFragment()
    val fragmentPropertyDetail = PropertyDetailFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        getLayout().first?.let { setContentView(it)}
        getLayout().second?.let { setContentView(it)}
        checkConnection()
    }

    abstract fun getLayout(): Pair<Int?, View?>

    fun checkConnection() {
        if (!Utils.isInternetAvailable(this)){
            Snackbar.make(window.decorView.findViewById(android.R.id.content)
                    , getString(R.string.offline_mode_on), LENGTH_SHORT).show()
            PreferenceHelper.internetAvailable = false
        } else
            PreferenceHelper.internetAvailable = true
    }

    fun isCurrentUserLogged(): Boolean {
        return auth.currentUser != null
    }

    fun <T> startNewActivity(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun startMapActivity(view: View) {
        fragmentPropertyDetail.startMapActivity()
    }

    fun configurePropertyDetailFragment() {
        val propertyDetailView = findViewById<View?>(R.id.frame_property_details)
        var fragment = supportFragmentManager.findFragmentById(R.id.frame_property_details)
        if (fragment == null && propertyDetailView != null) {
            fragment = fragmentPropertyDetail
            val fm = supportFragmentManager.beginTransaction()
            fm.add(R.id.frame_property_details, fragment, fragment::class.java.simpleName).commit()
        }
    }

    override fun onPropertyClick(propertyId: String) {
        val propertyDetailView = findViewById<View?>(R.id.frame_property_details)
        if (propertyDetailView == null) {
            val intent = Intent(this, PropertyDetailActivity::class.java)
            intent.putExtra(PROPERTY_ID, propertyId)
            startActivityForResult(intent, RQ_CODE_PROPERTY)
        } else {
            val bundle = Bundle()
            bundle.putString(PROPERTY_ID, propertyId)
            val propertyDetailFragment = PropertyDetailFragment()
            propertyDetailFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_property_details, propertyDetailFragment).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_PROPERTY && resultCode == Activity.RESULT_OK && data != null
                && data.hasExtra(PROPERTY_ID))
            intent.putExtra(PROPERTY_ID, data.getStringExtra(PROPERTY_ID))
        if (requestCode == MapActivity.REQUEST_CODE_LOCATION && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(MainActivity.TAG, "onActivityResult: location enabled")
        }
    }

    fun checkLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MapActivity.REQUEST_CODE)
            PreferenceHelper.locationEnabled = false
            return
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings) { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MapActivity.REQUEST_CODE_LOCATION)}
                    .setNegativeButton(R.string.ignore, null)
                    .show()
            PreferenceHelper.locationEnabled = false
            return
        }
        PreferenceHelper.locationEnabled = true
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MapActivity.REQUEST_CODE)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(MapActivity.TAG, "onRequestPermissionsResult: granted")
            } else
                Log.d(MapActivity.TAG, "onRequestPermissionsResult: refused")
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object{
        const val TAG = "LogBaseActivity"
    }
}