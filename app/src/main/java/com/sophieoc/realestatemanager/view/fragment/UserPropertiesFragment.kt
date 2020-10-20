package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.USER_ID
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_user_properties.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserPropertiesFragment: PropertyListFragment() {
    private val userViewModel by viewModel<UserViewModel>()
    override fun getLayout() = Pair(R.layout.fragment_user_properties, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (mainContext.intent.hasExtra(USER_ID)){
            val id = mainContext.intent.extras?.get(USER_ID) as String
            userViewModel.getUserById(id).observe(mainContext, {
                if (it != null) initView(it)
            })
        }
        userViewModel.currentUser.observe(mainContext, {
            if (it != null) initView(it)
        })
        configureRecyclerView(recycler_view_user_properties)

    }

    private fun initView(it: UserWithProperties) {
        adapter.updateList(ArrayList(it.properties))
        Glide.with(this)
                .load(it.user.urlPhoto)
                .apply(RequestOptions.circleCropTransform())
                .into(ic_profile_picture)
    }
}