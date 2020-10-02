package com.sophieoc.realestatemanager.view.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.api.Property
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.PropertyType
import com.sophieoc.realestatemanager.utils.RQ_CODE_ADD_PROPERTY
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_property_list.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    private var filterDialog: AlertDialog? = null
    private var filterChipGroup: ChipGroup? = null

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
        setSupportActionBar(my_toolbar)
    }

    override fun onResume() {
        super.onResume()
        configurePropertyDetailFragment()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        configureDrawerLayout()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_button -> showFilterDialog()
        }
        return true
    }

    private fun showFilterDialog() {
        val alertBuilder = AlertDialog.Builder(this, R.style.Dialog)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.title_filter_dialog, null)
        alertBuilder.setCustomTitle(view)
                .setView(R.layout.dialog_filter)
                .setPositiveButton("ok", null)
                .setNegativeButton("cancel", null)
                .setOnDismissListener(this)

        filterDialog = alertBuilder.create()
        filterDialog?.setOnShowListener(this)
        filterDialog?.show()

        filterChipGroup = filterDialog?.findViewById(R.id.type_chip_group)
        filterChipGroup?.setOnCheckedChangeListener { chipGroup, checkedId ->
            val chip = chipGroup.findViewById<Chip>(checkedId)
            Log.d(TAG, "showFilterDialog: selected chip ID = ${chip?.text == PropertyType.HOUSE.s} "  )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        /*   if (dialog === filterDialog) {
               dialogEditText = null
               filterDialog = null
           }
         */
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        if (filterDialog != null) {
            val positiveButton = filterDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener { onFilterDialogPositiveButtonClick(filterDialog) }
            val negativeButton = filterDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton?.setOnClickListener { filterDialog?.let { onDismiss(it) } }
        }
    }

    private fun onFilterDialogPositiveButtonClick(filterDialog: AlertDialog?) {
        //
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