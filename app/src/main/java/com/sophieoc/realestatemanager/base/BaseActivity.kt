package com.sophieoc.realestatemanager.base

import android.app.Activity
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.utils.PROPERTY_KEY
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.view.fragment.MapFragment
import com.sophieoc.realestatemanager.view.fragment.PropertyDetailFragment
import com.sophieoc.realestatemanager.view.fragment.PropertyListFragment
import com.sophieoc.realestatemanager.view.fragment.UserProfileFragment
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseActivity : AppCompatActivity(){
    lateinit var locationManager: LocationManager
    lateinit var auth: FirebaseAuth
    val fragmentMap = MapFragment()
    val fragmentList = PropertyListFragment()
    val fragmentUser = UserProfileFragment()
    val fragmentPropertyDetail = PropertyDetailFragment()
    val viewModel by viewModel<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        auth = FirebaseAuth.getInstance()
        setContentView(getLayout())
    }

    abstract fun getLayout(): Int

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isCurrentUserLogged(): Boolean {
        return getCurrentUser() != null
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_PROPERTY && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra(PROPERTY_KEY))
                intent.putExtra(PROPERTY_KEY, data.getStringExtra(PROPERTY_KEY))
        }
    }
}