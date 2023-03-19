package com.bcg.gpscompass.ui.screen.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.bcg.gpscompass.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.Style.Companion.MAPBOX_STREETS
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location

private const val ARG_ADDRESS = "address"
private const val ARG_LATITUDE = "latitude"
private const val ARG_LONGITUDE = "longitude"

class LocationFragment : Fragment() {
    private var address: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var mMapView: MapView
    private lateinit var mTvAddress: AppCompatTextView
    private lateinit var mTvLongitude: AppCompatTextView
    private lateinit var mTvLatitude: AppCompatTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            address = it.getString(ARG_ADDRESS)
            latitude = it.getDouble(ARG_LATITUDE)
            longitude = it.getDouble(ARG_LONGITUDE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView = view.findViewById(R.id.mapView)
        mTvAddress = view.findViewById(R.id.tv_address)
        mTvLatitude = view.findViewById(R.id.tv_latitude)
        mTvLongitude = view.findViewById(R.id.tv_longitude)
        mMapView.getMapboxMap().loadStyleUri(MAPBOX_STREETS) {
            mMapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
        }
        mTvAddress.text = address
        mTvLatitude.text = latitude?.toString()
        mTvLongitude.text = longitude?.toString()
        updateCamera();
    }

    private fun updateCamera() {
        val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
        mMapView.camera.easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(105.745779, 21.066171))
                .zoom(12.0)
                .padding(EdgeInsets(0.0, 0.0, 500.0, 0.0))
                .build(),
            mapAnimationOptions
        )
    }


    private fun enableLocationComponent(loadedMapStyle: Style) {
//        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(requireActivity()).pulseEnabled(true).build();
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle).locationComponentOptions(customLocationComponentOptions).build());
//            locationComponent.setLocationComponentEnabled(true);
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//            locationComponent.setRenderMode(RenderMode.NORMAL);
//        } else if (shouldShowRequestPermissionRationale("ABC")) {
//            //showInContextUI(...);
//        } else {
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
    }


    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mMapView.location
//            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
//        mMapView.location
//            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        mMapView.gestures.removeOnMoveListener(onMoveListener)
        mMapView!!.onDestroy()
    }

    companion object {
        fun newInstance(address: String, latitude: Double, longitude: Double) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ADDRESS, address)
                    putDouble(ARG_LATITUDE, latitude)
                    putDouble(ARG_LONGITUDE, longitude)
                }
            }
    }
}