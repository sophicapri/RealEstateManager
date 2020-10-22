package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentUserPropertiesBinding
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.USER_ID
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserPropertiesFragment : PropertyListFragment() {
    private val userViewModel by viewModel<UserViewModel>()
    private lateinit var binding : FragmentUserPropertiesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_properties, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (mainContext.intent.hasExtra(USER_ID)) {
            val id = mainContext.intent.extras?.get(USER_ID) as String
            userViewModel.getUserById(id).observe(mainContext, {
                if (it != null) initView(it)
                binding.myToolbar.title = mainContext.getString(R.string.agent_properties, it.user.username)
            })
        } else
            userViewModel.currentUser.observe(mainContext, {
                if (it != null) initView(it)
                binding.myToolbar.title = mainContext.getString(R.string.my_properties)
            })
    }

    private fun initView(it: UserWithProperties) {
        binding.user = it.user
        configureRecyclerView(binding.recyclerViewUserProperties)
        adapter.updateList(ArrayList(it.properties))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //override to not call super.onViewCreated in PropertyListFragment
    }
    
    companion object{
        const val TAG = "LogUserProperties"
    }
}