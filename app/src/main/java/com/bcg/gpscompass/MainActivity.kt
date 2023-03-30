package com.bcg.gpscompass

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bcg.gpscompass.ads.InterstitialAds
import com.bcg.gpscompass.repository.GpsCompassRepositoryImpl
import com.bcg.gpscompass.repository.model.firebase.UpdateAppModel
import com.bcg.gpscompass.ui.screen.AppViewModelFactory
import com.bcg.gpscompass.ui.screen.compass.CompassFragment
import com.bcg.gpscompass.ui.screen.compass.CompassFragment.Companion.newInstance
import com.bcg.gpscompass.ui.screen.privacy.PrivacyFragment
import com.bcg.gpscompass.utils.Constants
import com.bcg.gpscompass.utils.Navigator
import com.google.android.gms.ads.MobileAds
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.maps.ResourceOptionsManager.Companion.getDefault
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mCompassFragment: CompassFragment? = null
    private var mPrivacyFragment: PrivacyFragment? = null
    private lateinit var prefs: SharedPreferences
    private val permissionsManager: PermissionsManager? = null
    private lateinit var mViewModel: MainViewModel
    private lateinit var mInterstitialAds: InterstitialAds
    override fun onCreate(savedInstanceState: Bundle?) {
        getDefault(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this) {}
        mInterstitialAds = InterstitialAds(this, getString(R.string.interstitial_id_1))
        mInterstitialAds.onShowScreen { }
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
        init()
    }

    private fun init() {
        lifecycleScope.launch {
            val model: UpdateAppModel? = mViewModel.checkUpdateApp()
            if (model?.update == true || model?.force == true) {
                val updateDialog = AlertDialog.Builder(this@MainActivity)
                updateDialog.setTitle("New update available")
                updateDialog.setMessage("A new version is available. Update the app to continue")
                updateDialog.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                updateDialog.setPositiveButton("UPDATE") { dialog, which ->
                    //TODO open URL
                }
            }
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
        mInterstitialAds.showInterstitial {
            Navigator.replaceFragment(this, R.id.frame_container, fragment)
        }
    }
}