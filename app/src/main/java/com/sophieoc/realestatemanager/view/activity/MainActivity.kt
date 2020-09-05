package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.utils.Utils

class MainActivity : AppCompatActivity() {
    private var textViewMain: TextView? = null
    private var textViewQuantity: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // changed "activity_SECOND_activity_text_view_main" to "activity_MAIN_activity_text_view_main"
        textViewMain = findViewById(R.id.activity_main_activity_text_view_main)
        textViewQuantity = findViewById(R.id.activity_main_activity_text_view_quantity)
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