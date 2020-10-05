package com.sophieoc.realestatemanager.base

import android.os.Bundle
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity

abstract class BaseEditPropertyFragment : BaseFragment(){
    lateinit var addPropertyActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    companion object {
        var updatedProperty = Property()
    }
}