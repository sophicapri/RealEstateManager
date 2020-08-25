package com.sophieoc.realestatemanager

import org.junit.Assert
import org.junit.Test
import java.util.*

class UtilsTest {
    @Test
    fun todayDatePattern_isCorrect() {
        val todayDate = Calendar.getInstance()
        val day = todayDate.get(Calendar.DAY_OF_MONTH)
        val month = todayDate.get(Calendar.MONTH) + 1
        val year = todayDate.get(Calendar.YEAR)
        val dateExpected = if (month > 9 ) "$day/$month/$year" else "$day/0$month/$year"
        println(dateExpected)
        val todayDateResult = Utils.todayDate
        Assert.assertEquals(dateExpected, todayDateResult)
    }
}