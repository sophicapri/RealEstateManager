package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivityMainBinding
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener{
    //private var filterDialog: AlertDialog? = null
    //private val filterViewModel by viewModel<FilterViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
   // lateinit var bindingFilter: DialogFilterBinding
    lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    //private lateinit var bindingPropertyList: FragmentPropertyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        drawerLayout = binding.drawerLayout
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_property_list, fragmentList, fragmentList.javaClass.simpleName)
            .commit()
        setSupportActionBar(binding.myToolbar)
    }


    override fun getLayout() = Pair(null, binding.root)

    override fun onResume() {
        super.onResume()
        configureDrawerLayout()
        configurePropertyDetailFragment()
        // bindingPropertyList = FragmentPropertyListBinding.bind(findViewById(R.id.fragment_property_list))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        configureDrawerLayout()
    }

    private fun configureDrawerLayout() {
        val navigationView = binding.navigationView
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.myToolbar,
            R.string.open_navigation_drawer, R.string.close_navigation_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        val headerView: View = navigationView.getHeaderView(0)
        val profilePic = headerView.findViewById<ImageView>(R.id.profile_picture)
        val username = headerView.findViewById<TextView>(R.id.username)
        val email = headerView.findViewById<TextView>(R.id.email_user)

        userViewModel.currentUser.observe(this, {
            it?.let {
                val user = it.user
                Glide.with(profilePic.context)
                    .load(user.urlPhoto)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePic)
                username.text = user.username
                email.text = user.email
                PreferenceHelper.currentUserId = user.uid
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_view -> startMapActivity()
            R.id.user_properties -> startNewActivity(UserPropertiesActivity::class.java)
            R.id.settings -> startNewActivity(SettingsActivity::class.java)
            R.id.sign_out -> signOut()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_button -> fragmentList.showFilterDialog()
        }
        return true
    }

private fun startMapActivity() {
    if (Utils.isInternetAvailable(this)) {
        startNewActivity(MapActivity::class.java)
        PreferenceHelper.internetAvailable = true
    } else {
        Toast.makeText(this, getString(R.string.map_unavailable), LENGTH_LONG).show()
        PreferenceHelper.internetAvailable = false
    }
}

private fun signOut() {
    auth.signOut()
    finishAffinity()
    startNewActivity(LoginActivity::class.java)
}

companion object {
    const val TAG = "LogMainActivity"
}
}