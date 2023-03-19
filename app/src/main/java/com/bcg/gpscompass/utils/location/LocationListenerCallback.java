package com.bcg.gpscompass.utils.location;

import android.app.Activity;
import android.location.Location;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

public class LocationListenerCallback
        implements LocationEngineCallback<LocationEngineResult> {

    private final WeakReference<Activity> activityWeakReference;
    private LocationUpdateListener mListener;

    public LocationListenerCallback(Activity activity, LocationUpdateListener listener) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.mListener = listener;
    }

    @Override
    public void onSuccess(LocationEngineResult result) {

        Location lastLocation = result.getLastLocation();
        mListener.updateLocation(lastLocation);
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
    }

    public static interface LocationUpdateListener {
        void updateLocation(Location location);
    }
}


