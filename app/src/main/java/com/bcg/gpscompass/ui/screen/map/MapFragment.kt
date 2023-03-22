package com.bcg.gpscompass.ui.screen.map

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bcg.gpscompass.R
import com.bcg.gpscompass.ui.view.CompassImageView
import com.bcg.gpscompass.utils.gps.GpsUtil
import com.bcg.gpscompass.utils.sensor.SensorManagerCompass
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mSensorManagerCompass = SensorManagerCompass(activity!!)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById(R.id.mapView)
        mBtnCompass = view.findViewById(R.id.btn_compass)
        mBtnRotation = view.findViewById(R.id.btn_rotation)
        mBtnMyLocation = view.findViewById(R.id.btn_my_location)
        mCompassView = view.findViewById(R.id.compass_view)
        initMap()
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
        mMapView.camera.flyTo(
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
            mSensorManagerCompass.setGravity(mAccel)
        } else if (sensorEvent?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetic = GpsUtil.lowPass(
                sensorEvent.values,
                mMagnetic
            )
            mSensorManagerCompass.setGeoMagnetic(mMagnetic)
        }

        mSensorManagerCompass.loadAzimuth()
        val newAzimuth: Float = mSensorManagerCompass.getAzimuthSensor()
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