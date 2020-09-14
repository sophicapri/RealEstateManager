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

       /* val property = Property()
        property.type = PropertyType.FLAT
        property.description = "Great FLATTY!"
        property.userId = getCurrentUser()?.uid.toString()

        viewModel.insert(property)
        */

        viewModel.getPropertyById("3d77d003-deec-424a-8efb-1895e7397d2c").observe(this, Observer {
            println("what happened above ?")
            if(it != null)
            println("description = " + it.description)
        })
    }
}