package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.utils.DATABASE_NAME
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.PropertyType

class MainActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val property = Property()
        property.type = PropertyType.FLAT
        property.description = "Great FLATTY!"
        property.userId = getCurrentUser()?.uid.toString()

        viewModel.insert(property)

        viewModel.getPropertyByIdLocal(property.id).observe(this, Observer {
            if (it != null)
                println("getProperty LOCALE  description = " + it.description)
        })

        viewModel.getPropertiesLocal().observe(this, Observer {
            if (it != null)
                println("getProperties LOCALE  description = " + it.size)
        })

        viewModel.getCurrentUser()?.observe(this, Observer {
            println("current user name = " + it?.user?.username)
            if (it != null)
            println("current user property description = " + it.properties.size)
        })
    }
}