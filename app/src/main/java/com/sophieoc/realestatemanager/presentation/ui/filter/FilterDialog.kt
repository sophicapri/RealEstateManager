package com.sophieoc.realestatemanager.presentation.ui.filter

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.DialogFilterBinding
import com.sophieoc.realestatemanager.model.EntriesFilter
import com.sophieoc.realestatemanager.presentation.ui.propertylist.PropertyListFragment
import com.sophieoc.realestatemanager.utils.MINIMUM_PICTURES
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.STEP_SIZE_PRICE
import com.sophieoc.realestatemanager.utils.formatToDollarsOrMeters
import kotlinx.coroutines.flow.collect
import java.text.DateFormat
import java.util.*

class FilterDialog(
    private val filterViewModel: FilterViewModel,
    private val fragment: PropertyListFragment,
    private val onStartSearchListener: OnStartSearchListener
) : AlertDialog(fragment.requireContext()),
    DatePickerDialog.OnDateSetListener,
    DialogInterface.OnShowListener,
    DialogInterface.OnDismissListener {
    private var _bindingFilter: DialogFilterBinding? = null
    private val bindingFilter: DialogFilterBinding
        get() = _bindingFilter!!
    val dialog: AlertDialog

    init {
        dialog = buildDialog()
    }

    private fun buildDialog(): AlertDialog {
        val alertBuilder = Builder(context, R.style.Dialog)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.title_filter_dialog, null)
        filterViewModel.entries = EntriesFilter()
        _bindingFilter = DataBindingUtil.inflate(
            inflater, R.layout.dialog_filter,
            null, false)
        bindFilterViews()
        val dialogBuilder = alertBuilder.setCustomTitle(view)
            .setView(bindingFilter.root)
            .setPositiveButton(context.getString(R.string.ok_btn), null)
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener(this)
        val dialog = dialogBuilder.create()
        dialog.setOnShowListener(this)
        return dialog
    }

    private fun bindFilterViews() {
        bindingFilter.selectDate.setOnClickListener { showDatePickerDialog() }
        bindingFilter.btnDeleteDate.setOnClickListener {
            bindingFilter.selectDate.text = context.getString(R.string.click_to_select_a_date)
            filterViewModel.entries.dateOnMarket = null
            filterViewModel.entries.dateSold = null
            bindingFilter.btnDeleteDate.visibility = View.GONE
        }
        bindingFilter.rangeSliderPrice.addOnChangeListener(getPriceSliderListener())
        fragment.lifecycleScope.launchWhenStarted {
            filterViewModel.getPriceOfPriciestProperty().collect { price ->
                if (price != null) {
                    bindingFilter.apply {
                        rangeSliderPrice.valueFrom = 0f
                        rangeSliderPrice.valueTo = price.toFloat()
                        rangeSliderPrice.values = arrayListOf(0.0f, price.toFloat() / 2)
                        rangeSliderPrice.stepSize = price.toFloat() / STEP_SIZE_PRICE
                    }
                }
            }
        }
        fragment.lifecycleScope.launchWhenStarted {
            bindingFilter.rangeSliderSurface.addOnChangeListener(getSurfaceSliderListener())
            filterViewModel.getSurfaceOfBiggestProperty().collect { surface ->
                if (surface != null) {
                    bindingFilter.apply {
                        rangeSliderSurface.valueFrom = 0f
                        rangeSliderSurface.valueTo = surface.toFloat()
                        rangeSliderSurface.values = arrayListOf(0.0f, surface.toFloat() / 2)
                    }
                }
            }
        }
        bindingFilter.minPrice.text = context.getString(
            R.string.dollar_value,
            bindingFilter.rangeSliderPrice.values.first().toInt().formatToDollarsOrMeters()
        )
        bindingFilter.maxPrice.text = context.getString(
            R.string.dollar_value,
            bindingFilter.rangeSliderPrice.values.last().toInt().formatToDollarsOrMeters()
        )
        bindingFilter.minSurface.text =
            context.getString(
                R.string.sqft_value,
                bindingFilter.rangeSliderSurface.values.first().toInt()
            )
        bindingFilter.maxSurface.text =
            context.getString(
                R.string.sqft_value,
                bindingFilter.rangeSliderSurface.values.last().toInt()
            )
        bindingFilter.nbrOfPicInput.addTextChangedListener(getTextWatcher())
    }

    private fun getPriceSliderListener() = RangeSlider.OnChangeListener { slider, value, _ ->
        if (slider.activeThumbIndex == 0)
            bindingFilter.minPrice.text =
                context.getString(R.string.dollar_value, value.toInt().formatToDollarsOrMeters())
        else
            bindingFilter.maxPrice.text =
                context.getString(R.string.dollar_value, value.toInt().formatToDollarsOrMeters())
    }

    private fun getSurfaceSliderListener() = RangeSlider.OnChangeListener { slider, value, _ ->
        if (slider.activeThumbIndex == 0)
            bindingFilter.minSurface.text = context.getString(R.string.sqft_value, value.toInt())
        else
            bindingFilter.maxSurface.text = context.getString(R.string.sqft_value, value.toInt())
    }

    private fun getTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank())
                bindingFilter.checkboxPictures.isChecked = true
        }
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        val positiveButton = dialog.getButton(BUTTON_POSITIVE)
        positiveButton.setOnClickListener { startSearch() }
        val negativeButton = dialog.getButton(BUTTON_NEGATIVE)
        negativeButton.setOnClickListener { dialog.dismiss() }
    }

    private fun startSearch() {
        getEntries()
        filterViewModel.startSearch()
        onStartSearchListener.displayResultsTextHeader()
        onStartSearchListener.displayPropertyListResult()
    }

    private fun getEntries() {
        val chipType = findViewById<Chip>(bindingFilter.typeChipGroup.checkedChipId)
        chipType?.let { filterViewModel.entries.propertyType = it.text.toString() }
        if (bindingFilter.nbrOfBedsInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfBed = bindingFilter.nbrOfBedsInput.text.toString().toInt()
        if (bindingFilter.nbrOfBathInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfBath = bindingFilter.nbrOfBathInput.text.toString().toInt()
        if (bindingFilter.nbrOfRoomsInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfRoom =
                bindingFilter.nbrOfRoomsInput.text.toString().toInt()
        val chipAvailability = findViewById<Chip>(bindingFilter.availabilityChipGroup.checkedChipId)
        chipAvailability?.let {
            if (it.id == R.id.for_sale) filterViewModel.entries.propertyAvailability =
                PropertyAvailability.AVAILABLE.toString()
            else filterViewModel.entries.propertyAvailability = it.text.toString()
        }
        if (bindingFilter.areaInput.text.toString().isNotEmpty())
            filterViewModel.entries.area = bindingFilter.areaInput.text.toString().trim()
        filterViewModel.entries.priceMin = bindingFilter.rangeSliderPrice.values.first().toInt()
        filterViewModel.entries.priceMax = bindingFilter.rangeSliderPrice.values.last().toInt()
        filterViewModel.entries.surfaceMin = bindingFilter.rangeSliderSurface.values.first().toInt()
        filterViewModel.entries.surfaceMax = bindingFilter.rangeSliderSurface.values.last().toInt()
        if (bindingFilter.checkboxPictures.isChecked)
            filterViewModel.entries.nbrOfPictures = MINIMUM_PICTURES
        if (bindingFilter.nbrOfPicInput.text.toString().isNotEmpty())
            filterViewModel.entries.nbrOfPictures =
                bindingFilter.nbrOfPicInput.text.toString().trim().toInt()
    }

    override fun onDismiss(dialog: DialogInterface) {
        dialog.dismiss()
        _bindingFilter = null
    }

    private fun showDatePickerDialog() {
        Locale.setDefault(Locale.US)
        val datePickerDialog = DatePickerDialog(
            context,
            this, Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
        val okButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        okButton.id = R.id.calendar_ok_button
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US)
        val selectedDate = GregorianCalendar(year, month, dayOfMonth).time
        bindingFilter.selectDate.text = df.format(selectedDate)
        bindingFilter.selectDate.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.colorPrimaryLight
            )
        )
        bindingFilter.btnDeleteDate.visibility = View.VISIBLE
        val chip = findViewById<Chip>(bindingFilter.availabilityChipGroup.checkedChipId)
        if (chip != null) {
            if (chip.id == R.id.for_sale) {
                filterViewModel.entries.dateOnMarket = selectedDate
            } else
                filterViewModel.entries.dateSold = selectedDate
        } else {
            filterViewModel.entries.dateOnMarket = selectedDate
            bindingFilter.availabilityChipGroup.check(R.id.for_sale)
        }
    }

    interface OnStartSearchListener {
        fun displayResultsTextHeader()
        fun displayPropertyListResult()
    }
}