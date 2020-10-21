package com.sophieoc.realestatemanager.view.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.slider.RangeSlider
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.DialogFilterBinding
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.viewmodel.FilterViewModel
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_property_list.*
import kotlinx.android.synthetic.main.results_for_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), OnDateSetListener, NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    private var filterDialog: AlertDialog? = null
    private val filterViewModel by viewModel<FilterViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
    lateinit var binding: DialogFilterBinding

    companion object {
        const val TAG = "LogMainActivity"
    }

    override fun getLayout() = Pair(R.layout.activity_main, null)

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
        val drawerView: View = navigation_view.getHeaderView(0)
        val profilePic = drawerView.findViewById<ImageView>(R.id.profile_picture)
        val username = drawerView.findViewById<TextView>(R.id.username)
        val email = drawerView.findViewById<TextView>(R.id.email_user)
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

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, null, false)
        alertBuilder.setCustomTitle(view)
                .setView(binding.root)
                .setPositiveButton(getString(R.string.ok_btn), null)
                .setNegativeButton(getString(R.string.cancel).toUpperCase()) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener(this)

        binding.selectDate.setOnClickListener { showDatePickerDialog() }
        binding.btnDeleteDate.setOnClickListener {
            binding.selectDate.text = getString(R.string.click_to_select_a_date)
            filterViewModel.entries.dateOnMarket = null
            filterViewModel.entries.dateSold = null
            binding.btnDeleteDate.visibility = GONE
        }
        binding.rangeSliderPrice.addOnChangeListener(getPriceSliderListener())
        binding.rangeSliderSurface.addOnChangeListener(getSurfaceSliderListener())
        binding.minPrice.text = getString(R.string.dollar_value, binding.rangeSliderPrice.values.first().toInt().formatToDollarsOrMeters())
        binding.maxPrice.text = getString(R.string.dollar_value, binding.rangeSliderPrice.values.last().toInt().formatToDollarsOrMeters())
        binding.minSurface.text = getString(R.string.sqft_value, binding.rangeSliderSurface.values.first().toInt())
        binding.maxSurface.text = getString(R.string.sqft_value, binding.rangeSliderSurface.values.last().toInt())
        binding.nbrOfPicInput.addTextChangedListener(getTextWatcher())
        filterDialog = alertBuilder.create()
        filterDialog?.setOnShowListener(this)
        filterDialog?.show()
    }

    private fun getPriceSliderListener() = RangeSlider.OnChangeListener { slider, value, _ ->
        if (slider.activeThumbIndex == 0)
            binding.minPrice.text = getString(R.string.dollar_value, value.toInt().formatToDollarsOrMeters())
        else
            binding.maxPrice.text = getString(R.string.dollar_value, value.toInt().formatToDollarsOrMeters())
    }

    private fun getSurfaceSliderListener() = RangeSlider.OnChangeListener { slider, value, _ ->
        if (slider.activeThumbIndex == 0)
            binding.minSurface.text = getString(R.string.sqft_value, value.toInt())
        else
            binding.maxSurface.text = getString(R.string.sqft_value, value.toInt())
    }

    private fun getTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank())
                binding.checkboxPictures.isChecked = true
        }

    }

    private fun startSearch(dialog: DialogInterface?) {
        getEntries()
        filterViewModel.startSearch()
        filterViewModel.resultSearch.observe(this, {
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

    private fun getEntries() {
        val chipType = filterDialog?.findViewById<Chip>(binding.typeChipGroup.checkedChipId)
        chipType?.let { filterViewModel.entries.propertyType = it.text.toString() }
        if (binding.nbrOfBedsInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfBed = binding.nbrOfBedsInput.text.toString().toInt()
        if (binding.nbrOfBathInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfBath = binding.nbrOfBathInput.text.toString().toInt()
        if (binding.nbrOfRoomsInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfRoom = binding.nbrOfRoomsInput.text.toString().toInt()
        val chipAvailability = filterDialog?.findViewById<Chip>(binding.availabilityChipGroup.checkedChipId)
        chipAvailability?.let {
            if (it.id == R.id.for_sale)
                filterViewModel.entries.propertyAvailability = PropertyAvailability.AVAILABLE.toString()
            else
                filterViewModel.entries.propertyAvailability = it.text.toString()
        }
        if (binding.areaInput.text.toString().isNotEmpty())
            filterViewModel.entries.area = binding.areaInput.text.toString().trim()
        filterViewModel.entries.priceMin = binding.rangeSliderPrice.values.first().toInt()
        filterViewModel.entries.priceMax = binding.rangeSliderPrice.values.last().toInt()
        filterViewModel.entries.surfaceMin = binding.rangeSliderSurface.values.first().toInt()
        filterViewModel.entries.surfaceMax = binding.rangeSliderSurface.values.last().toInt()
        if (binding.checkboxPictures.isChecked)
            filterViewModel.entries.nbrOfPictures = MINIMUM_PICTURES
        if (binding.nbrOfPicInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfPictures = binding.nbrOfPicInput.text.toString().trim().toInt()
        if (binding.checkboxPark.isChecked)
            filterViewModel.entries.park = PARK
        if (binding.checkboxStore.isChecked)
            filterViewModel.entries.store = STORE
        if (binding.checkboxSchool.isChecked)
            filterViewModel.entries.school = SCHOOL
    }

    private fun displayResultsText() {
        results_search_container.visibility = VISIBLE
        data_searched.text = getTextToDisplay()
        btn_reset_search.setOnClickListener {
            fragmentList.resetFilter()
            results_search_container.visibility = View.GONE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (dialog === filterDialog) {
            filterDialog = null
            filterViewModel.entries = EntriesFilter()
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

    private fun showDatePickerDialog() {
        Locale.setDefault(Locale.US)
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
        binding.selectDate.text = df.format(selectedDate)
        binding.selectDate.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))
        binding.btnDeleteDate.visibility = VISIBLE
        val chip = filterDialog?.findViewById<Chip>(binding.availabilityChipGroup.checkedChipId)
        if (chip != null) {
            if (chip.id == R.id.for_sale)
                filterViewModel.entries.dateOnMarket = selectedDate
            else
                filterViewModel.entries.dateSold = selectedDate
        } else {
            filterViewModel.entries.dateOnMarket = selectedDate
            binding.availabilityChipGroup.check(R.id.for_sale)
        }
    }

    private fun startMapActivity() {
        if (Utils.isConnectionAvailable(this)) {
            startNewActivity(MapActivity::class.java)
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(this, getString(R.string.map_unavailable), LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    fun startAddPropertyActivity(view: View) {
        startNewActivity(EditOrAddPropertyActivity::class.java)
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