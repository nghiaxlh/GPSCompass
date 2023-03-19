//package com.bcg.gpscompass.ui.screen.location;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.AppCompatTextView;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.bcg.gpscompass.R;
//import com.mapbox.maps.MapView;
//import com.mapbox.maps.Style;
//import com.mapbox.maps.plugin.Plugin;
//import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
//import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//
//public class LocationFragment extends Fragment {
//
//    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//        if (isGranted) {
//            //TODO progress
//        } else {
//            // Reject
//        }
//    });
//    private MapView mMapView;
//    private AppCompatTextView mTvAddress;
//    private AppCompatTextView mTvLongitude;
//    private AppCompatTextView mTvLatitude;
//
//    public LocationFragment() {
//    }
//
//    public static LocationFragment newInstance() {
//        LocationFragment fragment = new LocationFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_location, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMapView = view.findViewById(R.id.mapView);
//        mTvAddress = view.findViewById(R.id.tv_address);
//        mTvLatitude = view.findViewById(R.id.tv_latitude);
//        mTvLongitude = view.findViewById(R.id.tv_longitude);
//        mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//                LocationComponentPlugin componentPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
//                componentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
//                    @Override
//                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
////                        locationComponentSettings.setEnabled();
//                        return null;
//                    }
//                });
//            }
//        });
//    }
//
//
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
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
//    }
