package com.sophieoc.realestatemanager

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.sophieoc.realestatemanager.utils.Utils
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInternetConnectionTest {
    companion object{
        private lateinit var device: UiDevice
        private const val APP_PACKAGE = "com.sophieoc.realestatemanager"
        private const val LAUNCH_TIMEOUT = 5000L
        private const val CMD_ENABLE_WIFI = "svc wifi enable"
        private const val CMD_DISABLE_WIFI = "svc wifi disable"
        private const val CMD_ENABLE_MOBILE_DATA = "svc data enable"
        private const val CMD_DISABLE_MOBILE_DATA = "svc data disable"
        private lateinit var context : Context
    }
    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)

        // Launch the app
        context = ApplicationProvider.getApplicationContext()
        val intent = context.packageManager.getLaunchIntentForPackage(APP_PACKAGE)?.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
                Until.hasObject(By.pkg(APP_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
        )
    }

    /**  This test will pass on emulators that can access mobile data
    *  (not Genymotion emulators for example) and on real devices only.
    */
    @Test
    fun internetAvailability(){
        // disable wifi
        device.executeShellCommand(CMD_DISABLE_WIFI)
        // disable mobile data
        device.executeShellCommand(CMD_DISABLE_MOBILE_DATA)
        Thread.sleep(2000)
        // check there's no internet
        assertThat(Utils.isInternetAvailable(context), Matchers.`is`(false))

        // enable mobile data
        device.executeShellCommand(CMD_ENABLE_MOBILE_DATA)
        Thread.sleep(2000)
        // check that there is internet
        assertThat(Utils.isInternetAvailable(context), Matchers.`is`(true))

        //disable mobile data
        device.executeShellCommand(CMD_DISABLE_MOBILE_DATA)
        Thread.sleep(2000)
        // check there's no internet
        assertThat(Utils.isInternetAvailable(context), Matchers.`is`(false))

        // enable wifi
        device.executeShellCommand(CMD_ENABLE_WIFI)
        Thread.sleep(5000)
        // check that there is internet (possibly increase Thread.sleep time if an error comes up below)
        assertThat(Utils.isInternetAvailable(context), Matchers.`is`(true))
    }
}