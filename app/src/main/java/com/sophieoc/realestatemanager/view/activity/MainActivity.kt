package com.sophieoc.realestatemanager.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.PROPERTY_KEY
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_property_list, fragmentList, fragmentList.javaClass.simpleName).commit()
        configureDrawerLayout()
    }

    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }

    private fun configureDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, my_toolbar,
                R.string.open_navigation_drawer, R.string.close_navigation_drawer)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view?.setNavigationItemSelectedListener(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_view -> startNewActivity(MapActivity::class.java)
            R.id.user_profile -> startNewActivity(UserProfileActivity::class.java)
            R.id.sign_out -> signOut()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun <T> startNewActivity(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun signOut() {
        auth.signOut()
        finishAffinity()
        startNewActivity(LoginActivity::class.java)
    }
}