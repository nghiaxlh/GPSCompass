package com.bcg.gpscompass

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bcg.gpscompass.repository.GpsCompassRepositoryImpl
import com.bcg.gpscompass.ui.screen.AppViewModelFactory
import com.bcg.gpscompass.ui.screen.compass.CompassFragment
import com.bcg.gpscompass.ui.screen.compass.CompassFragment.Companion.newInstance
import com.bcg.gpscompass.ui.screen.privacy.PrivacyFragment
import com.bcg.gpscompass.utils.Constants
import com.bcg.gpscompass.utils.Navigator
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.ResourceOptionsManager.Companion.getDefault

class MainActivity : AppCompatActivity() {
    private var mCompassFragment: CompassFragment? = null
    private var mPrivacyFragment: PrivacyFragment? = null
    private lateinit var prefs: SharedPreferences
    private val permissionsManager: PermissionsManager? = null
    private lateinit var mViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        getDefault(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProvider(
            this,
            AppViewModelFactory(GpsCompassRepositoryImpl())
        )[MainViewModel::class.java]
        prefs = getPreferences(MODE_PRIVATE)
        val firstOpen = prefs.getBoolean(Constants.PREF_FIRST_OPEN_KEY, true)
        if (firstOpen) {
            showPrivacyUI()
        } else {
            showCompassUI()
        }
    }

    fun showCompassUI() {
        mCompassFragment = newInstance()
        Navigator.replaceFragmentWithOutBackStack(this, R.id.frame_container, mCompassFragment)
    }

    fun showPrivacyUI() {
        mPrivacyFragment = PrivacyFragment.newInstance()
        Navigator.replaceFragmentWithOutBackStack(this, R.id.frame_container, mPrivacyFragment)
    }

    fun addFragment(fragment: Fragment?) {
        Navigator.replaceFragment(this, R.id.frame_container, fragment)
    }
}