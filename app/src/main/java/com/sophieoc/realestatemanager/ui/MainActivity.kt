package com.sophieoc.realestatemanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivityMainBinding
import com.sophieoc.realestatemanager.ui.map.MapActivity
import com.sophieoc.realestatemanager.ui.settings.SettingsActivity
import com.sophieoc.realestatemanager.ui.user.UserPropertiesActivity
import com.sophieoc.realestatemanager.ui.user.UserUiState
import com.sophieoc.realestatemanager.ui.user.UserViewModel
import com.sophieoc.realestatemanager.util.PreferenceHelper
import com.sophieoc.realestatemanager.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val userViewModel by viewModels<UserViewModel>()
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.myToolbar)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_property_list, fragmentList, fragmentList.javaClass.simpleName)
            .commit()
    }

    override fun getLayout() = binding.root

    override fun onResume() {
        super.onResume()
        configureDrawerLayout()
        configurePropertyDetailFragment()
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
        val appLogo = headerView.findViewById<ImageView>(R.id.app_logo)
        val profilePic = headerView.findViewById<ImageView>(R.id.profile_picture)
        val username = headerView.findViewById<TextView>(R.id.username)
        val email = headerView.findViewById<TextView>(R.id.email_user)
        appLogo.setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_app_logo, null)
        )

        lifecycleScope.launchWhenStarted {
            userViewModel.currentUser.collect { userUiState ->
                when (userUiState) {
                    is UserUiState.Success -> {
                        userUiState.userWithProperties.apply {
                            Glide.with(profilePic.context)
                                .load(user.urlPhoto)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePic)
                            username.text = user.username
                            email.text = user.email
                            PreferenceHelper.currentUserId = user.uid
                        }
                    }
                    is UserUiState.Error -> { /*TODO:*/ }
                    is UserUiState.Loading -> {/*TODO:*/ }
                }
            }
        }
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
        private const val TAG = "LogMainActivity"
    }
}