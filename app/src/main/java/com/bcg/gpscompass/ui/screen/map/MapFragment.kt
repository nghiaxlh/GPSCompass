package com.bcg.gpscompass.ui.screen.map

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bcg.gpscompass.BuildConfig
import com.bcg.gpscompass.R
import com.bcg.gpscompass.ui.view.CompassImageView
import com.bcg.gpscompass.utils.gps.GpsUtil
import com.bcg.gpscompass.utils.hideKeyboard
import com.bcg.gpscompass.utils.sensor.SensorManagerCompass
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.autofill.*
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillOptions
import com.mapbox.search.autofill.AddressAutofillResponse
import com.mapbox.search.autofill.AddressAutofillResponse.*
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private lateinit var mMapView: MapView
    private lateinit var mBtnCompass: AppCompatImageView
    private lateinit var mBtnRotation: AppCompatImageView
    private lateinit var mBtnMyLocation: AppCompatImageView
    private lateinit var mCompassView: CompassImageView
    private lateinit var mBtnBack: AppCompatImageView
    private lateinit var mAutoTextView: AppCompatEditText
    private lateinit var addressAutofill: AddressAutofill

    private lateinit var searchResultsView: SearchResultsView
    private lateinit var searchEngineUiAdapter: AddressAutofillUiAdapter
    private lateinit var mSensorManagerCompass: SensorManagerCompass
    private var mAccel = floatArrayOf(0f, 0f, 9.8f)
    private var mMagnetic = floatArrayOf(0.5f, 0f, 0f)
    private var mAzimuth = 0f
    private var isTurnOnRotate = true
    private var isTurnOnCompass: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            currentLatitude = arguments!!.getDouble(ARG_LATITUDE)
            currentLongitude = arguments!!.getDouble(ARG_LONGITUDE)
        }
        addressAutofill = AddressAutofill.create(BuildConfig.MAPBOX_ACCESS_TOKEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mSensorManagerCompass = SensorManagerCompass(activity!!)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById(R.id.mapView)
        searchResultsView = view.findViewById(R.id.search_results_view)
        mBtnCompass = view.findViewById(R.id.btn_compass)
        mBtnRotation = view.findViewById(R.id.btn_rotation)
        mBtnMyLocation = view.findViewById(R.id.btn_my_location)
        mCompassView = view.findViewById(R.id.compass_view)
        mBtnBack = view.findViewById(R.id.btn_back)
        mAutoTextView = view.findViewById(R.id.autoTextView)
        mBtnBack.setOnClickListener(View.OnClickListener {
            activity!!.onBackPressedDispatcher.onBackPressed()
        })
        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )
        searchEngineUiAdapter = AddressAutofillUiAdapter(
            view = searchResultsView,
            addressAutofill = addressAutofill
        )
        searchEngineUiAdapter.addSearchListener(object : AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                showAddressAutofillSuggestion(
                    suggestion,
                    fromReverseGeocoding = false,
                )
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })
        mAutoTextView.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
//                if (ignoreNextQueryTextUpdate) {
//                    ignoreNextQueryTextUpdate = false
//                    return
//                }
                Log.d("nghialh", "onTextChanged")
                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launch {
                        Log.d("nghialh", query.toString())
                        searchEngineUiAdapter.search(query)
                    }
                }
                searchResultsView.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })
        initMap()
    }

    private fun showAddressAutofillSuggestion(suggestion: AddressAutofillSuggestion, fromReverseGeocoding: Boolean) {
        val address = suggestion.result().address
//        cityEditText.setText(address.place)
//        stateEditText.setText(address.region)
//        zipEditText.setText(address.postcode)
//
//        fullAddress.isVisible = true
//        fullAddress.text = suggestion.formattedAddress
//
//        pinCorrectionNote.isVisible = true
//
//        if (!fromReverseGeocoding) {
//            mapView.getMapboxMap().setCamera(
//                CameraOptions.Builder()
//                    .center(suggestion.coordinate)
//                    .zoom(16.0)
//                    .build()
//            )
//            ignoreNextMapIdleEvent = true
//            mapPin.isVisible = true
//        }
//
//        ignoreNextQueryTextUpdate = true
//        queryEditText.setText(
//            listOfNotNull(
//                address.houseNumber,
//                address.street
//            ).joinToString()
//        )
        mAutoTextView.clearFocus()

        searchResultsView.isVisible = false
        searchResultsView.hideKeyboard()
    }


    private fun initMap() {
        mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            mMapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
        }
        val cameraPosition = CameraOptions.Builder()
            .zoom(12.0)
            .center(Point.fromLngLat(currentLongitude ?: 0.0, currentLatitude ?: 0.0))
            .build()
        mMapView.getMapboxMap().setCamera(cameraPosition)
    }

    private fun updateCamera(bearing: Double = 0.0) {
        val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
        mMapView.camera.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(currentLongitude ?: 0.0, currentLatitude ?: 0.0))
                .zoom(12.0)
                .bearing(bearing)
                .build(),
            mapAnimationOptions
        )
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            mAccel = GpsUtil.lowPass(sensorEvent.values, mAccel)
            mSensorManagerCompass.gravity = mAccel
        } else if (sensorEvent?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetic = GpsUtil.lowPass(
                sensorEvent.values,
                mMagnetic
            )
            mSensorManagerCompass.setGeoMagnetic(mMagnetic)
        }

        mSensorManagerCompass.loadAzimuth()
        val newAzimuth: Float = mSensorManagerCompass.azimuthSensor
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth
//            if (mMarker != null) {
//                mMarker.setRotation(mAzimuth)
//            }
            if (isTurnOnRotate) {
                if (isTurnOnCompass) {
                    mCompassView.setDegress(-mAzimuth)
                }
                updateCamera(bearing = mAzimuth.toDouble())
            } else {
                mCompassView.setDegress(0f)
            }
            mCompassView.invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        mSensorManagerCompass.registerAccelerometerListener(this)
        mSensorManagerCompass.registerMagneListener(this)
        mSensorManagerCompass.registerOriListener(this)
    }

    override fun onPause() {
        super.onPause()
        mSensorManagerCompass.unregisterListener(this)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_LATITUDE = "map_latitude"
        private const val ARG_LONGITUDE = "map_longitude"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(lat: Double, lon: Double): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putDouble(ARG_LATITUDE, lat)
            args.putDouble(ARG_LONGITUDE, lon)
            fragment.arguments = args
            return fragment
        }
    }
}