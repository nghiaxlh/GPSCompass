package com.bcg.gpscompass.utils.gps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bcg.gpscompass.R;
import com.bcg.gpscompass.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsLocationService extends IntentService {
    private static final String TAG = GpsLocationService.class.getSimpleName();
    private Location mLocation;
    private ResultReceiver resultReceiver;
    public GpsLocationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER_EXTRA);
        if (resultReceiver == null) {
            Log.d(TAG, "null");
            return;
        }

        String messageError = "";
        mLocation = intent.getParcelableExtra(Constants.LOCATION_EXTRA);
        if (mLocation == null) {
            messageError = getString(R.string.no_location_data_provided);
            Log.wtf(TAG, messageError);
            getResultToReceiver(Constants.FAILURE_RESULT, messageError, messageError);
            return;
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresseList = null;

        try {
            addresseList = geocoder.getFromLocation(mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    1);
        } catch (IOException e) {
            messageError = getString(R.string.noti_service_not_available);
            Log.e(TAG, messageError, e);
        } catch (IllegalArgumentException illegalArgumentException) {
            messageError = getString(R.string.text_invalid_lat_long_used);
            Log.e(TAG, messageError + ". " +
                    "Latitude = " + mLocation.getLatitude() +
                    ", Longitude = " +
                    mLocation.getLongitude(), illegalArgumentException);
        }

        if (addresseList == null || addresseList.size() == 0) {
            if (messageError.isEmpty()) {
                {
                    messageError = getString(R.string.text_no_address_found);
                    Log.e(TAG, messageError);
                }
            }
            getResultToReceiver(Constants.FAILURE_RESULT, messageError, messageError);
        } else {
            Address address = addresseList.get(0);
            String addressStr = "";
            String addressFull = "";
            String feature = address.getFeatureName();
            String thoroughfare = address.getThoroughfare();
            String subArea = address.getSubAdminArea();
            String locality = address.getLocality();
            String area = address.getAdminArea();
            String country = address.getCountryName();

            Log.i(TAG, getString(R.string.text_address_found));
            if (feature != null) {
                addressFull += " " + feature;
            }
            if (thoroughfare != null) {
                addressStr = thoroughfare;
                addressFull += " " + thoroughfare;
            }
            if (locality != null) {
                if (addressStr.length() == 0) {
                    addressStr = locality;
                }
                addressFull += " " + locality;
            }
            if (subArea != null) {
                if (addressStr.length() == 0) {
                    addressStr = subArea;
                }
                addressFull += " " + subArea;
            }
            if (area != null) {
                addressFull += " " + area;
            }
            if (country != null) {
                addressFull += " " + country;
            }
            getResultToReceiver(Constants.SUCCESS_RESULT, addressStr, addressFull);
        }
    }
    private void getResultToReceiver(int resultCode, String address, String locationFull) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_KEY_ADDRESS, address);
        bundle.putString(Constants.RESULT_KEY_ADDRESS_FULL, locationFull);
        resultReceiver.send(resultCode, bundle);
    }
}
