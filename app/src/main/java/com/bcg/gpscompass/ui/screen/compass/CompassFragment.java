package com.bcg.gpscompass.ui.screen.compass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcg.gpscompass.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.pyxis.compass.gpscompassnavigationmap.BaseFragment;
import com.pyxis.compass.gpscompassnavigationmap.R;
import com.pyxis.compass.gpscompassnavigationmap.SensorManagerCompass;
import com.pyxis.compass.gpscompassnavigationmap.activity.GoogleMapsActivity;
import com.pyxis.compass.gpscompassnavigationmap.activity.HowToUseActivity;
import com.pyxis.compass.gpscompassnavigationmap.activity.LocationActivity;
import com.pyxis.compass.gpscompassnavigationmap.activity.SettingsActivity;
import com.pyxis.compass.gpscompassnavigationmap.activity.weatherfull.MyWeatherActivity;
import com.pyxis.compass.gpscompassnavigationmap.dialog.RateUsDialogApp;
import com.pyxis.compass.gpscompassnavigationmap.listener.CompassListener;
import com.pyxis.compass.gpscompassnavigationmap.presenter.CompassHomePre;
import com.pyxis.compass.gpscompassnavigationmap.service.GpsCompassLocationService;
import com.pyxis.compass.gpscompassnavigationmap.utils.AppConstantUtils;
import com.pyxis.compass.gpscompassnavigationmap.utils.GpsCompassUtils;
import com.pyxis.compass.gpscompassnavigationmap.view.CustomImageCompassView;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class CompassFragment extends BaseFragment<CompassPresenter> implements SensorEventListener, CompassListener, View.OnClickListener {

    private SensorManagerCompass mSensorManagerCompass;
    private CustomImageCompassView mCustomImageCompassView;
    private TextView mTvCity;
    private TextView mTvDegreesDirection;
    private ImageButton mBtnGoogleMaps;
    private ImageButton mBtnSettingApp;
    public ImageButton mBtnRateApp;
    private ImageButton mBtnWarningApp;
    private ImageButton mBtnLocation;
    private ImageButton mBtnFlashLight;
    private ImageButton mBtnWeather;
    private Typeface mTypeface;
    private int mCurrentMagnetic = 0;
    private float[] valuesAccel = new float[]{0f, 0f, 9.8f};
    private float[] valuesMagnetic = new float[]{0.5f, 0f, 0f};
    private float mAzimuth;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private AddressResultReceiver addressResultReceiver;
    private String mLocationOutput;
    private String mFullLocation;
    private boolean isFlashOn;
    private Camera camera;
    private boolean hasFlash = false;
    Camera.Parameters mParams;
    //    private AdView nativeExpressAdView;
    private static final int CURRENT_REQUEST = 112;

    private RelativeLayout rlLimited;
    private float[] mGravity;
    private double currentInclinationX;
    private double currentInclinationY;
    private double startInclinationX;
    private double startInclinationY;
    private ImageView ivBallCompass;
    private float AA = 0.3F;

    public CompassFragment() {
    }

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    protected CompassHomePre createPresenter() {
        return new CompassHomePre(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.font_roboto_bold);
        mSensorManagerCompass = new SensorManagerCompass(getActivity());
        addressResultReceiver = new AddressResultReceiver(new Handler());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mLocationOutput = "";
        mFullLocation = "";
        createLocationRequest();
        locationSetting();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        RateUsDialogApp.showDialogRateApp(getActivity());
        mBtnGoogleMaps = (ImageButton) view.findViewById(R.id.btn_google_map_home);
        mBtnSettingApp = (ImageButton) view.findViewById(R.id.btn_settings_home);
        mBtnRateApp = (ImageButton) view.findViewById(R.id.btn_rate_app_home);
        mBtnWarningApp = (ImageButton) view.findViewById(R.id.btn_warning_home);
        mBtnLocation = (ImageButton) view.findViewById(R.id.btn_location_home);
        mBtnFlashLight = (ImageButton) view.findViewById(R.id.btn_flash_light_home);
        mCustomImageCompassView = (CustomImageCompassView) view.findViewById(R.id.iv_compass_home);
        mTvCity = (TextView) view.findViewById(R.id.tv_location_city_home);
        mTvDegreesDirection = (TextView) view.findViewById(R.id.tv_degrees_direction_home);
        mTvCity.setTypeface(mTypeface);
        mTvCity.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTvCity.setSelected(true);
        mTvCity.setText("Wait...");
        mTvDegreesDirection.setTypeface(mTypeface);
//        nativeExpressAdView = view.findViewById(R.id.ads_banner_home);

        rlLimited = view.findViewById(R.id.rl_limit);
        ivBallCompass = view.findViewById(R.id.iv_ball);

        mBtnWeather = view.findViewById(R.id.btn_weather_home);
        mBtnWeather.setOnClickListener(this);

        mBtnGoogleMaps.setOnClickListener(this);
        mBtnSettingApp.setOnClickListener(this);
        mBtnRateApp.setOnClickListener(this);
        mBtnWarningApp.setOnClickListener(this);
        mBtnLocation.setOnClickListener(this);
        mBtnFlashLight.setOnClickListener(this);

//        admobBannerUnit();
        getLastLocation();
        return view;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                mPresenter.setTextLocation(location.getLatitude(), location.getLongitude());
                mLastLocation = location;
                getLocation();
            }
        }
    };

    public static void xyzAnim(View paramView, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt) {
        try {
            TranslateAnimation localTranslateAnimation = new TranslateAnimation((float) paramArrayOfDouble1[0], (float) paramArrayOfDouble2[0], (float) paramArrayOfDouble1[1], (float) paramArrayOfDouble2[1]);
            localTranslateAnimation.setDuration(paramInt);
            localTranslateAnimation.setFillEnabled(true);
            localTranslateAnimation.setFillAfter(true);
            paramView.startAnimation(localTranslateAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected float[] xyzBall(float[] mFloat1, float[] mFloat2, float currentFloat) {
        if (mFloat2 == null) {
            return mFloat1;
        }
        for (int i = 0; i < mFloat1.length; i++) {
            mFloat2[i] += currentFloat * (mFloat1[i] - mFloat2[i]);
        }
        return mFloat2;
    }

    private void tiltDevice() {
        double dbMath = Math.sqrt(mGravity[0] * mGravity[0] + mGravity[1] * mGravity[1] + mGravity[2] * mGravity[2]);
        double[] dbListArray = new double[3];
        dbListArray[0] = (mGravity[0] / dbMath);
        dbListArray[1] = (mGravity[1] / dbMath);
        dbListArray[2] = (mGravity[2] / dbMath);
        currentInclinationX = (-Math.toDegrees(Math.asin(dbListArray[0])));
        currentInclinationY = Math.toDegrees(Math.asin(dbListArray[1]));
        double dbLayout = rlLimited.getWidth() / 2 - ivBallCompass.getWidth() / 2;
        currentInclinationX = (dbLayout * -dbListArray[0]);
        currentInclinationY = (dbLayout * dbListArray[1]);
    }

    private void admobBannerUnit() {
        final AdView mAdView = new AdView(getContext());
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_id_1));
//        nativeExpressAdView.addView(mAdView);
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLastLocation = location;
                            mPresenter.setTextLocation(location.getLatitude(), location.getLongitude());
                            getLocation();
                        } else {
                        }
                    }
                });
    }

    private void getLocation() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        startIntentService();
    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), GpsCompassLocationService.class);
        intent.putExtra(AppConstantUtils.RECEIVER_EXTRA, addressResultReceiver);
        intent.putExtra(AppConstantUtils.LOCATION_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManagerCompass.registerAccelerometerListener(this);
        mSensorManagerCompass.registerMagneListener(this);
        mSensorManagerCompass.registerOriListener(this);
        startLocationUpdates();
//        if (hasFlash)
//            turnOnFlash();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManagerCompass.unregisterListener(this);
        stopUpdateLocation();
    }

    private void stopUpdateLocation() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mPresenter.changeDirection(sensorEvent);
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = xyzBall(sensorEvent.values.clone(), mGravity, AA);
            tiltDevice();
            double[] a1 = new double[2];
            a1[0] = startInclinationX;
            a1[1] = startInclinationY;
            double[] a2 = new double[2];
            a2[0] = (-currentInclinationX);
            a2[1] = (-currentInclinationY);
            xyzAnim(ivBallCompass, a1, a2, 210);
            startInclinationX = (-currentInclinationX);
            startInclinationY = (-currentInclinationY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            switch (i) {
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_LOW);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    mPresenter.calibrateCompass(SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                    mCurrentMagnetic = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
                    break;
            }
        }
    }

    @Override
    public void setChangeDirectionCompass(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            valuesAccel = GpsCompassUtils.lowPass(sensorEvent.values, valuesAccel);
            mSensorManagerCompass.setGravity(valuesAccel);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            valuesMagnetic = GpsCompassUtils.lowPass(sensorEvent.values,
                    valuesMagnetic);
            mSensorManagerCompass.setGeoMagnetic(valuesMagnetic);
        }
        mSensorManagerCompass.loadAzimuth();
        float newAzimuth = mSensorManagerCompass.getAzimuthSensor();
        if (mAzimuth != newAzimuth) {
            mAzimuth = newAzimuth;
            mCustomImageCompassView.setDegress(-mAzimuth);
            mCustomImageCompassView.invalidate();
            int degrees = Math.round(mAzimuth);
            String direction = GpsCompassUtils.displayDirection(mAzimuth);
            mTvDegreesDirection.setText(getString(R.string.text_direction, degrees, direction));
        }
    }

    @Override
    public void setTextForLocation(double lat, double lon) {
        String latitude = GpsCompassUtils.decimalToDMS(lat) + GpsCompassUtils.getSymbol(lat, true);
        String longitude = GpsCompassUtils.decimalToDMS(lon) + GpsCompassUtils.getSymbol(lon, false);
        /*mTvLat.setText(latitude);
        mTvLon.setText(longitude);*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_settings_home:
//                AdmobAdsUtils.getSharedInstance().showInterstitialAd(new AdmobAdsUtils.AdCloseListener() {
//                    @Override
//                    public void onAdClosed() {
//                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
//                        startActivity(intent);
//                    }
//                });
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_google_map_home:
                mPresenter.openViewGoogleMaps();
                break;

            case R.id.btn_rate_app_home:
                new RateUsDialogApp(getActivity()).show();
//                showDialogRate();
                break;

            case R.id.btn_warning_home:
                mPresenter.openWarning();
                break;

            case R.id.btn_location_home:
                mPresenter.openLocation();
                break;

            case R.id.btn_weather_home:
                Intent mWeather = new Intent(getActivity(), MyWeatherActivity.class);
                startActivity(mWeather);
                break;

            case R.id.btn_flash_light_home:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                100);
                    } else {

                        PackageManager pm = getContext().getPackageManager();
                        if (AppConstantUtils.isFlashSupported(pm)) {
                            if (hasFlash) {
                                startFlashLight(false);
                                hasFlash = false;
                            } else {
                                startFlashLight(true);
                                hasFlash = true;
                            }
                        }
                    }
                } else {
                    PackageManager pm = getContext().getPackageManager();
                    if (AppConstantUtils.isFlashSupported(pm)) {
                        if (hasFlash) {
                            startFlashLight(false);
                            hasFlash = false;
                        } else {
                            startFlashLight(true);
                            hasFlash = true;
                        }
                    }
                }
                break;
        }
    }

    public void startFlashLight(boolean show) {

        if (camera == null) {
            camera = Camera.open();
        }
        Camera.Parameters p = camera.getParameters();
        boolean on = show;
        if (on) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
            try {
                camera.setPreviewTexture(mPreviewTexture);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            camera.startPreview();
        } else {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CURRENT_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCamera();
            } else {
                Toast.makeText(getContext(), "The app was not allowed to access camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("LongLogTag")
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                mParams = camera.getParameters();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void showViewGoogleMapsCompass() {
        Intent intent = new Intent(getActivity(), GoogleMapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLocationCompass() {
        Intent intent = new Intent(getActivity(), LocationActivity.class);
        intent.putExtra(AppConstantUtils.LOCATION_EXTRA, mLastLocation);
        intent.putExtra(AppConstantUtils.LOCATION_STRING_EXTRA, mFullLocation);
        startActivity(intent);
    }

    @Override
    public void showIconLocation() {
        if (mBtnLocation != null) {
            mBtnLocation.setVisibility(View.VISIBLE);
            mBtnGoogleMaps.setVisibility(View.VISIBLE);
        } else {
            mBtnLocation.setVisibility(View.INVISIBLE);
            mBtnGoogleMaps.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void getCalibrate(int calibrate) {
        if (1 == calibrate || 2 == calibrate) {
            mBtnWarningApp.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_warning));
        } else {
            mBtnWarningApp.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_white));
        }
    }

    @Override
    public void showWarningCompass() {
        Intent intent = new Intent(getActivity(), HowToUseActivity.class);
        intent.putExtra(AppConstantUtils.CALIBRATE_MAGENTIC_COMPASS, mCurrentMagnetic);
        startActivity(intent);
    }


    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData == null) {
                return;
            }
            mLocationOutput = resultData.getString(AppConstantUtils.RESULT_KEY_ADDRESS);
            mFullLocation = resultData.getString(AppConstantUtils.RESULT_KEY_ADDRESS_FULL);

            displayAddressOutput(mFullLocation);
            if (resultCode == AppConstantUtils.SUCCESS_RESULT) {
                mPresenter.showIconLocation();
            }
        }
    }

    private void displayAddressOutput(String currentAddress) {
        mTvCity.setText(currentAddress);
    }

    private void locationSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(getActivity(), AppConstantUtils.REQUEST_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }
}
