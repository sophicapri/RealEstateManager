package com.sophieoc.realestatemanager.presentation.ui.propertylist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentPropertyListBinding
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.presentation.BaseActivity
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel
import com.sophieoc.realestatemanager.presentation.ui.editproperty.EditAddPropertyActivity
import com.sophieoc.realestatemanager.presentation.ui.filter.FilterDialog
import com.sophieoc.realestatemanager.presentation.ui.filter.FilterViewModel
import com.sophieoc.realestatemanager.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PropertyListFragment : Fragment(), FilterDialog.OnStartSearchListener {
    private val propertyViewModel: PropertyViewModel by viewModels()
    private val filterViewModel: FilterViewModel by viewModels()
    private var _binding: FragmentPropertyListBinding? = null
    private val binding: FragmentPropertyListBinding
        get() = _binding!!
    private lateinit var adapter: PropertyListAdapter
    private lateinit var mainContext: BaseActivity
    private var filterDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = activity as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView(binding.recyclerViewProperties)
        binding.apply {
            swipeRefreshView.setOnRefreshListener {
                if (resultsSearchLayout.resultsSearchContainer.visibility == VISIBLE) {
                    displayPropertyListResult()
                } else
                    updatePropertyList()
                swipeRefreshView.isRefreshing = false
            }
            fabAddProperty.setOnClickListener {
                if (Utils.isInternetAvailable(mainContext))
                    mainContext.startNewActivity(EditAddPropertyActivity::class.java)
                else {
                    Toast.makeText(
                        context,
                        getString(R.string.cant_add_property_offline),
                        LENGTH_LONG
                    ).show()
                    PreferenceHelper.internetAvailable = false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainContext.checkConnection()
        if (binding.resultsSearchLayout.resultsSearchContainer.visibility == VISIBLE)
            displayPropertyListResult()
        else
            updatePropertyList()
    }

    private fun updatePropertyList() {
        lifecycleScope.launchWhenStarted {
            propertyViewModel.getProperties().collect { propertyListUiState ->
                when (propertyListUiState) {
                    is PropertyListUiState.Loading -> {/* TODO: showProgressBar() */
                    }
                    is PropertyListUiState.Empty -> binding.noPropertiesInDb.visibility = VISIBLE
                    is PropertyListUiState.Error -> {/* TODO: handleError */
                    }
                    is PropertyListUiState.Success -> {
                        adapter.updateList(ArrayList(propertyListUiState.propertyList))
                        binding.noPropertiesInDb.visibility = GONE
                    }
                }
            }
        }
    }

    private fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyListAdapter(getListener())
        recyclerView.adapter = adapter
    }

    private fun getListener() = object : PropertyListAdapter.OnPropertyClickListener {
        override fun onPropertyClick(propertyId: String) {
            mainContext.onPropertyClick(propertyId)
        }
    }

    private fun updateList(filteredList: ArrayList<Property>) {
        adapter.updateList(filteredList)
    }

    private fun resetFilter() {
        updatePropertyList()
    }

    @SuppressLint("InflateParams")
    fun showFilterDialog() {
        filterDialog = FilterDialog(filterViewModel,this, this).dialog
        filterDialog?.show()
    }

    override fun displayResultsTextHeader() {
        binding.apply {
            resultsSearchLayout.apply {
                resultsSearchContainer.visibility = VISIBLE
                dataSearched.text = getTextToDisplay()
                btnResetSearch.setOnClickListener {
                    resetFilter()
                    noPropertiesFound.visibility = GONE
                    resultsSearchContainer.visibility = GONE
                }
            }
        }
    }

    override fun displayPropertyListResult() {
        lifecycleScope.launchWhenStarted {
            filterViewModel.resultSearch.collect { propertyListUiState ->
                when (propertyListUiState) {
                    is PropertyListUiState.Loading -> {/*TODO: show progressBar*/
                    }
                    is PropertyListUiState.Empty -> {
                        binding.noPropertiesFound.visibility = VISIBLE
                    }
                    is PropertyListUiState.Success -> {
                        updateList(ArrayList(propertyListUiState.propertyList))
                        binding.noPropertiesFound.visibility = GONE
                    }
                    is PropertyListUiState.Error -> {/* TODO: handleError */ }
                }
                filterDialog?.dismiss()
            }
        }
    }

    private fun getTextToDisplay(): String {
        val entries = filterViewModel.entries
        var msg = ""
        entries.propertyType?.let { msg += "$it - " }
        entries.nbrOfRoom?.let {
            msg += resources.getQuantityString(R.plurals.nbr_of_room_filter, it, it)
        }
        entries.nbrOfBed?.let {
            msg +=  resources.getQuantityString(R.plurals.nbr_of_bed_filter, it, it)
        }
        entries.nbrOfBath?.let {
            msg +=  resources.getQuantityString(R.plurals.nbr_of_bath_filter, it, it)
        }
        entries.propertyAvailability?.let {
            msg += if (it == PropertyAvailability.AVAILABLE.name) "${PropertyAvailability.AVAILABLE.s} "
            else "${PropertyAvailability.SOLD.s} "
            entries.dateOnMarket?.let { date ->
                msg += getString(
                    R.string.since_date,
                    date.toStringFormat()
                )
            }
            entries.dateSold?.let { date ->
                msg += getString(
                    R.string.since_date,
                    date.toStringFormat()
                )
            }
            msg += "- "
        }
        entries.area?.let { msg += "$it - " }
        if (entries.priceMin != null && entries.priceMax != null) {
            msg += getString(
                R.string.price_between,
                entries.priceMin?.formatToDollarsOrMeters(),
                entries.priceMax?.formatToDollarsOrMeters()
            )
        }
        if (entries.surfaceMin != null && entries.surfaceMax != null) {
            msg += getString(R.string.sqft_between, entries.surfaceMin, entries.surfaceMax)
        }
        entries.nbrOfPictures?.let {
            msg +=  resources.getQuantityString(R.plurals.with_x_pictures, it, it)
        }
        return msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}