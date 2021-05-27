package com.sophieoc.realestatemanager.presentation.ui.editproperty

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EditPropertyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var fragmentAddress = AddAddressFragment()
    var fragmentPropertyInfo = AddPropertyInfoFragment()
    var fragmentPictures = AddPicturesFragment()

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        FragmentIndex.ADD_ADDRESS_FRAGMENT to { fragmentAddress },
        FragmentIndex.ADD_PROPERTY_INFO_FRAGMENT to { fragmentPropertyInfo },
        FragmentIndex.ADD_PICTURES_FRAGMENT to { fragmentPictures }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}