package com.sophieoc.realestatemanager.view.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_property_list.*
import kotlinx.android.synthetic.main.results_for_search.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), OnDateSetListener, NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    private var filterDialog: AlertDialog? = null
    private var filterChipGroup: ChipGroup? = null
    private var filterPropertyType: String? = null
    private var filterNbrOfBed: Int? = null
    private var filterNbrOfBath: Int? = null
    private var filterPropertyAvailability: String? = null
    private var filterDateOnMarket: Date? = null
    private var filterDateSold: Date? = null
    private var filterPriceMin: Int = 0
    private var filterPriceMax: Int = 100000000
    private var filterSurfaceMin: Int = 0
    private var filterSurfaceMax: Int = 500
    private var filterNbrOfPictures: Int? = null
    private var filterPark: String? = null
    private var filterSchool: String? = "school"
    private var filterStore: String? = null

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
        setSupportActionBar(my_toolbar)
    }

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
        val toggle = ActionBarDrawerToggle(this, drawer_layout, my_toolbar,
                R.string.open_navigation_drawer, R.string.close_navigation_drawer)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        navigation_view?.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_view -> startNewActivity(MapActivity::class.java)
            R.id.user_properties -> startNewActivity(UserPropertiesActivity::class.java)
            R.id.settings -> startNewActivity(SettingsActivity::class.java)
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
                .setNegativeButton("cancel") { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener(this)

        filterDialog = alertBuilder.create()
        filterDialog?.setOnShowListener(this)
        filterDialog?.show()

        filterChipGroup = filterDialog?.findViewById(R.id.type_chip_group)
        filterChipGroup?.setOnCheckedChangeListener { chipGroup, checkedId ->
            val chip = chipGroup.findViewById<Chip>(checkedId)
        }
    }

    private fun startSearch(dialog: DialogInterface?) {
        //   if (dialog == filterDialog)
        //dialog.findViewById<>()

        //TODO : add progress bar
        viewModel.getFilteredList(propertyType = filterPropertyType, nbrOfBed = filterNbrOfBed, nbrOfBath = filterNbrOfBath,
                propertyAvailability = filterPropertyAvailability, dateOnMarket = filterDateOnMarket, dateSold = filterDateSold,
                priceMin = filterPriceMin, priceMax = filterPriceMax, surfaceMin = filterSurfaceMin, surfaceMax = filterSurfaceMax,
                nbrOfPictures = filterNbrOfPictures, park = filterPark, school = filterSchool, store = filterStore).observe(this, {
            it?.let {
                displayResultsText()
                fragmentList.updateList(ArrayList(it))
            }
            if (it == null) {
                Log.d(TAG, "startSearch: property list is null")
            }
            dialog?.dismiss()
        })
    }

    private fun displayResultsText() {
        results_search_container.visibility = View.VISIBLE
        data_searched.text = getTextToDisplay()
        btn_reset_search.setOnClickListener {
            fragmentList.resetFilter()
            results_search_container.visibility = View.GONE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d(TAG, "onDismiss")
        if (dialog === filterDialog) {
            filterDialog = null
            filterPropertyType = null
            filterNbrOfBed = null
            filterNbrOfBath = null
            filterPropertyAvailability = null
            filterDateOnMarket = null
            filterDateSold = null
            filterPriceMin = 0
            filterPriceMax = 40000000
            filterSurfaceMin = 0
            filterSurfaceMax = 300
            filterNbrOfPictures = null
            filterPark = null
        }
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        if (filterDialog != null) {
            val positiveButton = filterDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener {
                startSearch(filterDialog)
            }
            val negativeButton = filterDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton?.setOnClickListener { filterDialog?.dismiss() }
        }
    }

    // -- Handle Filter -- //
    private fun showDatePickerDialog() {
        Locale.setDefault(Locale.FRANCE)
        val datePickerDialog = DatePickerDialog(this,
                this, Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH],
                Calendar.getInstance()[Calendar.DAY_OF_MONTH])
        datePickerDialog.show()
        val okButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        okButton.id = R.id.calendar_ok_button
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US)
        val selectedDate = GregorianCalendar(year, month, dayOfMonth).time
        val dateView = filterDialog?.findViewById<Button>(R.id.select_date)
        dateView?.text = df.format(selectedDate)
        dateView?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))
        //update(newDate)
    }

    private fun signOut() {
        auth.signOut()
        finishAffinity()
        startNewActivity(LoginActivity::class.java)
    }

    private fun getTextToDisplay(): String {
        return ""
    }
}