package com.sophieoc.realestatemanager.ui.editproperty

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentEditAddPropertyBinding
import com.sophieoc.realestatemanager.notification.NotificationHelper
import com.sophieoc.realestatemanager.ui.property.PropertyUiState
import com.sophieoc.realestatemanager.ui.property.PropertyViewModel
import com.sophieoc.realestatemanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*


@AndroidEntryPoint
class EditAddPropertyFragment : Fragment(), DialogInterface.OnDismissListener {
    private var _binding: FragmentEditAddPropertyBinding? = null
    private val binding: FragmentEditAddPropertyBinding
        get() = _binding!!
    private val sharedViewModel by activityViewModels<PropertyViewModel>()
    private lateinit var pagerAdapter: EditPropertyPagerAdapter
    private var currentFragmentIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentEditAddPropertyBinding.inflate(inflater, container, false)
        bindViews()
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager
        pagerAdapter = EditPropertyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        if (savedInstanceState != null){
            currentFragmentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT)
            viewPager.currentItem = currentFragmentIndex
        }

        tabLayout.addOnTabSelectedListener(getOnTabSelectedListener())
        TabLayoutMediator(tabLayout, viewPager, true) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()
        return binding.root
    }

    private fun getOnTabSelectedListener(): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(position: TabLayout.Tab) {
                currentFragmentIndex = position.position
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, currentFragmentIndex)
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            FragmentIndex.ADD_ADDRESS_FRAGMENT -> R.drawable.add_address_tab_selector
            FragmentIndex.ADD_PROPERTY_INFO_FRAGMENT -> R.drawable.property_info_tab_selector
            FragmentIndex.ADD_PICTURES_FRAGMENT -> R.drawable.add_pictures_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            FragmentIndex.ADD_ADDRESS_FRAGMENT -> getString(R.string.address_title)
            FragmentIndex.ADD_PROPERTY_INFO_FRAGMENT -> getString(R.string.main_informations_title)
            FragmentIndex.ADD_PICTURES_FRAGMENT -> getString(R.string.pictures_title)
            else -> null
        }
    }

    private fun bindViews() {
        binding.apply {
            propertyViewModel = sharedViewModel
            activity = this@EditAddPropertyFragment
            requireActivity().intent.extras?.let { titleEditCreate.text = getString(R.string.edit_property_title) }
            if (requireActivity().intent.extras == null)
                titleEditCreate.text = getString(R.string.add_property_title)
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
    }

    fun saveChanges() {
        if (Utils.isInternetAvailable(requireContext())) {
            PreferenceHelper.internetAvailable = false
            checkDates()
            if (checkInputs()) {
                binding.progressBar.visibility = VISIBLE
                sharedViewModel.property.userId = PreferenceHelper.currentUserId
                saveProperty()
            } else
                showAlertDialog()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.cant_save_property), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun checkDates() {
        if (sharedViewModel.property.availability == PropertyAvailability.AVAILABLE)
            sharedViewModel.property.dateSold = null
        else
            sharedViewModel.property.dateOnMarket = null
    }

    private fun saveProperty() {
        sharedViewModel.upsertProperty()
        lifecycleScope.launchWhenStarted {
            sharedViewModel.propertySaved.collect { propertyUiState ->
                when (propertyUiState) {
                    is PropertyUiState.Success -> {
                        requireActivity().onBackPressed()
                        binding.progressBar.visibility = GONE
                        displayNotification()
                    }
                    is PropertyUiState.Error -> { /* showError(propertyUiState.exception) */}
                    is PropertyUiState.Loading -> {/* showLoading()*/}
                }
            }
        }
    }

    private fun checkInputs(): Boolean {
        return pagerAdapter.fragmentAddress.checkAddressPage()
            .and(pagerAdapter.fragmentPropertyInfo.checkMainInfoPage())
    }

    private fun showAlertDialog() {
        var message = getString(R.string.fill_in_missing_fields)
        if (emptyFieldsInAddress) {
            message += getString(R.string.in_address_page)
        }
        if (emptyFieldsInMainInfo) {
            if (emptyFieldsInAddress)
                message += getString(R.string.and)
            message += getString(R.string.in_main_info_page)
        }
        message += "."

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.forgot_info_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_btn)) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener(this)
            .create().show()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        emptyFieldsInAddress = false
        emptyFieldsInMainInfo = false
    }

    private fun displayNotification() {
        val notificationHelper = NotificationHelper(requireContext())
        val nb: NotificationCompat.Builder = notificationHelper
            .getChannelNotification(getString(R.string.property_saved_successful))
        with(NotificationManagerCompat.from(requireContext())) {
            notify(NotificationHelper.NOTIFICATION_ID, nb.build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddPropertyFragment"
        var emptyFieldsInAddress = false
        var emptyFieldsInMainInfo = false
    }
}