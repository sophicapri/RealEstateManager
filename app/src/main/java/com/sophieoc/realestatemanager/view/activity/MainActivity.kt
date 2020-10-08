package com.sophieoc.realestatemanager.view.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PropertyType
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), OnDateSetListener, NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    private var filterDialog: AlertDialog? = null
    private var filterChipGroup: ChipGroup? = null
    private var filterPropertyType: String? = null
    private var filterNbrOfBed: Int? = null
    private var filterNbrOfBath: Int? = 1
    private var filterPropertyAvailability: String? = null
    private var filterDateOnMarket: Date? = null
    private var filterDateSold: Date? = null
    private var filterPriceMin: Int = 0
    private var filterPriceMax: Int = 20000000
    private var filterSurfaceMin: Int = 0
    private var filterSurfaceMax: Int = 300
    private var filterNbrOfPictures: Int? = 1
    private var filterPointOfInterests: String? = null

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

    fun testMethodForFiler() {
        viewModel.getFilteredList(filterPropertyType, filterNbrOfBed, filterNbrOfBath, filterPropertyAvailability,
                filterDateOnMarket, filterDateSold, filterPriceMin, filterPriceMax, filterSurfaceMin, filterSurfaceMax,
                filterPointOfInterests).observe(this, {

        })
    }

    private fun updateListWithPictures(properties: ArrayList<Property>, nbrOfPictures: Int): ArrayList<Property> {
        properties.forEachIndexed { _, property ->
            if (property.photos.size < nbrOfPictures)
                properties.remove(property)
        }
        Log.d(TAG, "updateListWithPictures: ${properties.size}")
        return properties
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
                .setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }
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

        /*      filterPropertyType,
              filterNbrOfBed,
              filterNbrOfBath,
              filterPropertyAvailability,
              filterDateOnMarket,
              filterDateSold,
              filterPriceMin,
              filterPriceMax,
              filterSurfaceMin,
              filterSurfaceMax,
              filterNbrOfPictures,
              filterPointOfInterests


         */
        //TODO : add progress bar
            viewModel.getFilteredList(filterPropertyType, filterNbrOfBed, filterNbrOfBath, filterPropertyAvailability,
                    filterDateOnMarket, filterDateSold, filterPriceMin, filterPriceMax, filterSurfaceMin, filterSurfaceMax,
                    filterPointOfInterests).observe(this, {
                it?.let {
                        fragmentList.updateList(ArrayList(it))
                        Log.d(TAG, "startSearch: property list size = ${it.size}")
                }
                if (it == null) {
                    Log.d(TAG, "startSearch: property list is null")
                }
                dialog?.dismiss()
            })
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d(TAG, "onDismiss: on Dismiss")
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
            filterPointOfInterests = null
        }
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        if (filterDialog != null) {
            val positiveButton = filterDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener {
                startSearch(filterDialog)
                //onFilterDialogPositiveButtonClick(filterDialog)
            }
            val negativeButton = filterDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton?.setOnClickListener { filterDialog?.dismiss() } }
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

        //updateCurrentRoomsAvailabilityHandler(newDate)
    }

    private fun onFilterDialogPositiveButtonClick(filterDialog: AlertDialog?) {
        //
    }

    private fun signOut() {
        auth.signOut()
        finishAffinity()
        startNewActivity(LoginActivity::class.java)
    }
}