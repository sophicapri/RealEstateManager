package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.PROPERTY_ID


class PropertyDetailActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_property_detail
    }

    override fun onResume() {
        super.onResume()
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_property_details, fragmentPropertyDetail,
                        fragmentPropertyDetail::class.java.simpleName).commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (intent.hasExtra(PROPERTY_ID)) {
            val propertyId = intent.extras?.get(PROPERTY_ID) as String
            setResult(RESULT_OK, Intent().putExtra(PROPERTY_ID, propertyId))
        }
        finish()
    }
}