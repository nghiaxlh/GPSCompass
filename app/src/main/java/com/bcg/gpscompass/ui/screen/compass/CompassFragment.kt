package com.bcg.gpscompass.ui.screen.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bcg.gpscompass.MainActivity
import com.bcg.gpscompass.R
import com.bcg.gpscompass.repository.GpsCompassRepositoryImpl
import com.bcg.gpscompass.repository.model.Geocoding
import com.bcg.gpscompass.repository.remote.ApiState
import com.bcg.gpscompass.ui.base.BaseFragment
import com.bcg.gpscompass.ui.screen.AppViewModelFactory
import com.bcg.gpscompass.ui.screen.location.LocationFragment
import com.bcg.gpscompass.ui.screen.map.MapFragment
import com.bcg.gpscompass.ui.screen.weather.WeatherFragment
import com.bcg.gpscompass.ui.view.CompassImageView
import com.bcg.gpscompass.utils.gps.GpsUtil
import com.bcg.gpscompass.utils.location.LocationListenerCallback
import com.bcg.gpscompass.utils.location.LocationListenerCallback.LocationUpdateListener
import com.bcg.gpscompass.utils.sensor.SensorManagerCompass
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import kotlinx.coroutines.launch

class CompassFragment : BaseFragment<CompassPresenter?>(), SensorEventListener, CompassListener,
    LocationUpdateListener, View.OnClickListener {
    var DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private var mViewModel: CompassViewModel? = null
    private var mSensorManagerCompass: SensorManagerCompass? = null
    private var mCustomImageCompassView: CompassImageView? = null
    private var mTxtAddress: TextView? = null
    private var mBtnMap: ImageButton? = null
    private var mBtnLocation: ImageButton? = null
    private var mBtnWeather: ImageButton? = null
    private val mTypeface: Typeface? = null
    private var mCurrentMagnetic = 0
    private var valuesAccel = floatArrayOf(0f, 0f, 9.8f)
    private var valuesMagnetic = floatArrayOf(0.5f, 0f, 0f)
    private var mAzimuth = 0f
    private var mGravity: FloatArray? = null
    private val currentInclinationX = 0.0
    private val currentInclinationY = 0.0
    private var startInclinationX = 0.0
    private var startInclinationY = 0.0
    private val ivBallCompass: ImageView? = null
    private val AA = 0.3f
    var DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val requestPermissionLauncher =
        registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                lastLocation
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    private var callback: LocationListenerCallback? = null
    private var locationEngine: LocationEngine? = null
    private var currentAddress: String? = null
    private var shortAddress: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    override fun createPresenter(): CompassPresenter {
        return CompassPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.font_roboto_bold);
        mSensorManagerCompass = SensorManagerCompass(requireActivity())
        mViewModel = ViewModelProvider(this, AppViewModelFactory(GpsCompassRepositoryImpl())).get(
            CompassViewModel::class.java
        )
        callback = LocationListenerCallback(requireActivity(), this)
    }

    protected fun xyzBall(
        mFloat1: FloatArray,
        mFloat2: FloatArray?,
        currentFloat: Float
    ): FloatArray {
        if (mFloat2 == null) {
            return mFloat1
        }
        for (i in mFloat1.indices) {
            mFloat2[i] += currentFloat * (mFloat1[i] - mFloat2[i])
        }
        return mFloat2
    }

    private fun tiltDevice() {
//        double dbMath = Math.sqrt(mGravity[0] * mGravity[0] + mGravity[1] * mGravity[1] + mGravity[2] * mGravity[2]);
//        double[] dbListArray = new double[3];
//        dbListArray[0] = (mGravity[0] / dbMath);
//        dbListArray[1] = (mGravity[1] / dbMath);
//        dbListArray[2] = (mGravity[2] / dbMath);
//        currentInclinationX = (-Math.toDegrees(Math.asin(dbListArray[0])));
//        currentInclinationY = Math.toDegrees(Math.asin(dbListArray[1]));
//        double dbLayout = rlLimited.getWidth() / 2 - ivBallCompass.getWidth() / 2;
//        currentInclinationX = (dbLayout * -dbListArray[0]);
//        currentInclinationY = (dbLayout * dbListArray[1]);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_compass, container, false)
        mBtnMap = view.findViewById<View>(R.id.btn_map_compass) as ImageButton
        mBtnLocation = view.findViewById<View>(R.id.btn_location_compass) as ImageButton
        mCustomImageCompassView = view.findViewById<View>(R.id.iv_compass) as CompassImageView
        mTxtAddress = view.findViewById<View>(R.id.tv_address) as TextView
        //mTvDegreesDirection = (TextView) view.findViewById(R.id.tv_degrees_direction_home);
//        mTvCity.setTypeface(mTypeface);
//        mTvCity.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        mTvCity.setSelected(true);
//        mTvCity.setText("Wait...");
//        mTvDegreesDirection.setTypeface(mTypeface);
//        nativeExpressAdView = view.findViewById(R.id.ads_banner_home);

//        rlLimited = view.findViewById(R.id.rl_limit);
//        ivBallCompass = view.findViewById(R.id.iv_ball);
        mBtnWeather = view.findViewById(R.id.btn_weather_compass)
        mBtnWeather!!.setOnClickListener(this)
        mBtnLocation!!.setOnClickListener(this)
        mBtnMap!!.setOnClickListener(this)
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            lastLocation
        } else if (shouldShowRequestPermissionRationale("ABC")) {
//            showInContextUI(...);
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        showAddress()
        return view
    }

    private fun showAddress() {
        lifecycleScope.launch {
            mViewModel?.geoCodings?.collect {
                when (it) {
                    is ApiState.Loading -> {
                        mTxtAddress!!.text = "Waiting..."
                    }
                    is ApiState.Failure -> {
                        it.e.printStackTrace()
                        mTxtAddress!!.text = "Not found address"
                    }
                    is ApiState.Success -> {
                        val address = it.data as Geocoding
                        val neighbourhood = if (address.address?.neighbourhood.isNullOrEmpty()) "" else "${address.address?.neighbourhood},"
                        val city = address.address?.city
                        val road = if (address.address?.road.isNullOrEmpty()) "" else "${address.address?.road},"
                        val suburb = if (address.address?.suburb.isNullOrEmpty()) "" else "${address.address?.suburb},"
                        shortAddress = "$road $neighbourhood $suburb $city"
                        shortAddress.also { mTxtAddress!!.text = it }
                        currentAddress = address.displayName
                    }
                    is ApiState.Empty -> {
                        println("Empty...")
                    }
                }

            }
        }
    }

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        private get() {
            locationEngine = LocationEngineProvider.getBestLocationEngine(requireActivity())
            val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build()
            locationEngine!!.requestLocationUpdates(request, callback!!, Looper.getMainLooper())
            locationEngine!!.getLastLocation(callback!!)
        }

    override fun onResume() {
        super.onResume()
        mSensorManagerCompass!!.registerAccelerometerListener(this)
        mSensorManagerCompass!!.registerMagneListener(this)
        mSensorManagerCompass!!.registerOriListener(this)
    }

    override fun onPause() {
        super.onPause()
        mSensorManagerCompass!!.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        mPresenter!!.changeDirection(sensorEvent)
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravity = xyzBall(sensorEvent.values.clone(), mGravity, AA)
            tiltDevice()
            val a1 = DoubleArray(2)
            a1[0] = startInclinationX
            a1[1] = startInclinationY
            val a2 = DoubleArray(2)
            a2[0] = -currentInclinationX
            a2[1] = -currentInclinationY
            //xyzAnim(ivBallCompass, a1, a2, 210);
            startInclinationX = -currentInclinationX
            startInclinationY = -currentInclinationY
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            when (i) {
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                    mPresenter!!.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_LOW)
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_LOW
                }
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                    mPresenter!!.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
                }
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                    mPresenter!!.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
                }
            }
        }
    }

    override fun setChangeDirectionCompass(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            valuesAccel = GpsUtil.lowPass(sensorEvent.values, valuesAccel)
            mSensorManagerCompass!!.gravity = valuesAccel
        } else if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            valuesMagnetic = GpsUtil.lowPass(
                sensorEvent.values,
                valuesMagnetic
            )
            mSensorManagerCompass!!.setGeoMagnetic(valuesMagnetic)
        }
        mSensorManagerCompass!!.loadAzimuth()
        val newAzimuth = mSensorManagerCompass!!.azimuthSensor
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth
            mCustomImageCompassView!!.setDegress(-mAzimuth)
            mCustomImageCompassView!!.invalidate()
            val degrees = Math.round(mAzimuth)
            val direction = GpsUtil.displayDirection(mAzimuth)
            //mTvDegreesDirection.setText(getString(R.string.text_direction, degrees, direction));
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_map_compass ->{
                val mapFragment = MapFragment.newInstance(latitude, longitude)
                (requireActivity() as MainActivity).addFragment(mapFragment)
            }
            R.id.btn_location_compass -> {
                val fragment = LocationFragment.newInstance(currentAddress, latitude, longitude)
                (requireActivity() as MainActivity).addFragment(fragment)
            }
            R.id.btn_weather_compass -> {
                val fragment = WeatherFragment.newInstance(shortAddress, latitude, longitude)
                (requireActivity() as MainActivity).addFragment(fragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (locationEngine != null) {
            locationEngine!!.removeLocationUpdates(callback!!)
        }
    }

    override fun showViewGoogleMapsCompass() {
//        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
//        startActivity(intent);
    }

    override fun showLocationCompass() {

    }

    override fun showIconLocation() {
        if (mBtnLocation != null) {
            mBtnLocation!!.visibility = View.VISIBLE
            mBtnMap!!.visibility = View.VISIBLE
        } else {
            mBtnLocation?.visibility = View.INVISIBLE
            mBtnMap!!.visibility = View.INVISIBLE
        }
    }

    override fun getCalibrate(calibrate: Int) {
//        if (1 == calibrate || 2 == calibrate) {
//            mBtnWarningApp.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.color_warning));
//        } else {
//            mBtnWarningApp.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.color_white));
//        }
    }

    override fun showWarningCompass() {
//        Intent intent = new Intent(getActivity(), HowToUseActivity.class);
//        intent.putExtra(AppConstantUtils.CALIBRATE_MAGENTIC_COMPASS, mCurrentMagnetic);
//        startActivity(intent);
    }

    override fun updateLocation(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        mViewModel!!.getFlowerList(latitude, longitude)
    }

    companion object {
        private const val CURRENT_REQUEST = 112

        @JvmStatic
        fun newInstance(): CompassFragment {
            return CompassFragment()
        }

        fun xyzAnim(
            paramView: View,
            paramArrayOfDouble1: DoubleArray,
            paramArrayOfDouble2: DoubleArray,
            paramInt: Int
        ) {
            try {
                val localTranslateAnimation = TranslateAnimation(
                    paramArrayOfDouble1[0].toFloat(),
                    paramArrayOfDouble2[0].toFloat(),
                    paramArrayOfDouble1[1].toFloat(),
                    paramArrayOfDouble2[1].toFloat()
                )
                localTranslateAnimation.duration = paramInt.toLong()
                localTranslateAnimation.isFillEnabled = true
                localTranslateAnimation.fillAfter = true
                paramView.startAnimation(localTranslateAnimation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}