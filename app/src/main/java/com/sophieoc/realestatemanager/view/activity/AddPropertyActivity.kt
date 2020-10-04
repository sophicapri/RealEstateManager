package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.ADD_PROPERTY_KEY
import com.sophieoc.realestatemanager.view.fragment.PropertyEditOrAddFragment

class AddPropertyActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_add_property

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_add_property, PropertyEditOrAddFragment(),
                        PropertyEditOrAddFragment().javaClass.simpleName).commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setResult(RESULT_OK, Intent().putExtra(ADD_PROPERTY_KEY, savedInstanceState))
        finish()
    }
}