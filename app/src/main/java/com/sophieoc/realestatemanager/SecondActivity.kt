package com.sophieoc.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sophieoc.realestatemanager.model.EstateAgent

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    fun test(){
        val user = EstateAgent(1,"s","","","","")
        /*val property = Property(-1, PropertyType.DUPLEX,"",1,4
        ,8,0,"",PropertyAvailability.AVAILABLE,
        Date(),Date(), Address(1,"","","","",""
                ,"","",4), ArrayList(),ArrayList(),-1)
        val propertyAndPhotos = PropertyAndPhotos(property,ArrayList(),ArrayList())
        propertyAndPhotos.

         */
    }
}