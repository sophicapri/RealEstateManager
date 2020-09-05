package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.widget.TextView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    private var textViewMain: TextView? = null
    private var textViewQuantity: TextView? = null
    override fun getLayout(): Int {
        return R.layout.activity_main
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // changed "activity_SECOND_activity_text_view_main" to "activity_MAIN_activity_text_view_main"
        textViewMain = activity_main_activity_text_view_main
        textViewQuantity = activity_main_activity_text_view_quantity
        configureTextViewMain()
        configureTextViewQuantity()
    }

    private fun configureTextViewMain() {
        textViewMain!!.textSize = 15f
        textViewMain!!.text = "Le premier bien immobilier enregistré vaut "
    }

    // added "String.valueOf()"
    private fun configureTextViewQuantity() {
        val quantity = Utils.convertDollarToEuro(100)
        textViewQuantity!!.textSize = 20f
        textViewQuantity!!.text = quantity.toString()
    }
}