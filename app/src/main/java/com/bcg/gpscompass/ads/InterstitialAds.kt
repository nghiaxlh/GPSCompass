package com.bcg.gpscompass.ads

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

const val TAG: String = "InterstitialAds"

class InterstitialAds(private val mActivity: Activity, private val mAdUnitId: String) {
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading = false

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(mActivity,
            mAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                    adIsLoading = false
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                    Toast.makeText(
                        mActivity, "onAdFailedToLoad() with error $error", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    Toast.makeText(mActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun showInterstitial(listener: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    interstitialAd = null
                    onShowScreen(listener)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Ad failed to show.")
                    interstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            interstitialAd?.show(mActivity)
        } else {
            Toast.makeText(mActivity, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            onShowScreen(listener)
        }
    }

    fun onShowScreen(listener: () -> Unit) {
        if (!adIsLoading && interstitialAd == null) {
            adIsLoading = true
            loadAd()
        }
        listener.invoke()

    }
}