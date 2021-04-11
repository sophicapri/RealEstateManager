package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddInfoBinding
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import com.sophieoc.realestatemanager.utils.toStringFormat
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import java.text.DateFormat
import java.util.*

class AddPropertyInfoFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: FragmentAddInfoBinding
    private lateinit var rootActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_info,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = rootActivity.propertyViewModel
        if (rootActivity.activityRestarted)
            binding.executePendingBindings()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        rootActivity.checkConnection()
        bindViews()
    }

    private fun showDatePickerDialog() {
        Locale.setDefault(Locale.US)
        val datePickerDialog = DatePickerDialog(rootActivity,
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
        binding.apply {
            btnDate.text = df.format(selectedDate)
            errorDate.visibility = GONE
            btnDate.setTextColor(ContextCompat.getColor(rootActivity, R.color.colorPrimaryLight))
            if (rootActivity.propertyViewModel.property.availability == PropertyAvailability.AVAILABLE) {
                rootActivity.propertyViewModel.property.dateOnMarket = selectedDate
                rootActivity.propertyViewModel.property.dateSold = null
            } else {
                rootActivity.propertyViewModel.property.dateSold = selectedDate
            }
        }
    }

    private fun bindViews() {
        val property = rootActivity.propertyViewModel.property
        binding.apply {
            typesSpinner.setSelection(getSpinnerPosition(property.type.s, R.array.property_types))
            typesSpinner.onItemSelectedListener = getOnTypeSelectedListener()
            if (rootActivity.intent.extras != null) {
                availabilitySpinner.setSelection(
                    getSpinnerPosition(
                        property.availability.s,
                        R.array.property_availability
                    )
                )
                availabilitySpinner.onItemSelectedListener = getOnAvailabilitySelectedListener()
                forSaleTextView.visibility = GONE
            } else {
                property.availability = PropertyAvailability.AVAILABLE
                forSaleTextView.visibility = VISIBLE
                availabilitySpinner.visibility = INVISIBLE
            }
            btnDate.setOnClickListener { showDatePickerDialog() }
            btnDate.text = getString(R.string.click_to_select_a_date)
            property.dateSold?.let { btnDate.text = it.toStringFormat() }
            property.dateOnMarket?.let { btnDate.text = it.toStringFormat() }
        }
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }

    private fun getOnAvailabilitySelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            rootActivity.propertyViewModel.property.availability = PropertyAvailability.values()[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun getOnTypeSelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            rootActivity.propertyViewModel.property.type = PropertyType.values()[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    companion object {
        const val TAG = "AddInfoFragment"
    }
}
