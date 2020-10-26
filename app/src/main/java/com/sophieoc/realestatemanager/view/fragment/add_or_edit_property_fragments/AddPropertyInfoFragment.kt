package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentAddInfoBinding
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import com.sophieoc.realestatemanager.utils.toStringFormat
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import kotlinx.android.synthetic.main.fragment_add_info.*
import java.text.DateFormat
import java.util.*

class AddPropertyInfoFragment : BaseFragment(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: FragmentAddInfoBinding
    private lateinit var addPropertyActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
        Log.d(TAG, "onCreate: ")
    }


    override fun getLayout() = Pair(null, binding.root)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_info,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = addPropertyActivity.propertyViewModel
        if (addPropertyActivity.activityRestarted)
            binding.executePendingBindings()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        bindViews()
    }

    private fun showDatePickerDialog() {
        Locale.setDefault(Locale.US)
        val datePickerDialog = DatePickerDialog(mainContext,
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
        btn_date.text = df.format(selectedDate)
        error_date.visibility = View.GONE
        btn_date?.setTextColor(ContextCompat.getColor(mainContext, R.color.colorPrimaryLight))
        if (addPropertyActivity.propertyViewModel.property.availability == PropertyAvailability.AVAILABLE) {
            addPropertyActivity.propertyViewModel.property.dateOnMarket = selectedDate
            addPropertyActivity.propertyViewModel.property.dateSold = null
        }else {
            addPropertyActivity.propertyViewModel.property.dateSold = selectedDate
        }
    }

    private fun bindViews() {
        val property = addPropertyActivity.propertyViewModel.property
        types_spinner.setSelection(getSpinnerPosition(property.type.s, R.array.property_types))
        types_spinner.onItemSelectedListener = getOnTypeSelectedListener()
        if (addPropertyActivity.intent.extras != null) {
            availability_spinner.setSelection(getSpinnerPosition(property.availability.s, R.array.property_availability))
            availability_spinner.onItemSelectedListener = getOnAvailabilitySelectedListener()
            for_sale_text_view.visibility = GONE
        } else {
            property.availability = PropertyAvailability.AVAILABLE
            for_sale_text_view.visibility = VISIBLE
            availability_spinner.visibility = INVISIBLE
        }
        btn_date.setOnClickListener { showDatePickerDialog() }
        btn_date.text = getString(R.string.click_to_select_a_date)
        property.dateSold?.let {  btn_date.text = it.toStringFormat()}
        property.dateOnMarket?.let {  btn_date.text = it.toStringFormat()}
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }

    private fun getOnAvailabilitySelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            addPropertyActivity.propertyViewModel.property.availability = PropertyAvailability.values()[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun getOnTypeSelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            addPropertyActivity.propertyViewModel.property.type = PropertyType.values()[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    companion object {
        const val TAG = "AddInfoFragment"
    }
}
