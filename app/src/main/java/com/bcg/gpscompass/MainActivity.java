package com.bcg.gpscompass;

import static com.bcg.gpscompass.utils.Constants.PREF_FIRST_OPEN_KEY;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bcg.gpscompass.ui.screen.compass.CompassFragment;
import com.bcg.gpscompass.ui.screen.privacy.PrivacyFragment;
import com.bcg.gpscompass.utils.Navigator;

public class MainActivity extends AppCompatActivity {

    private CompassFragment mCompassFragment;
    private PrivacyFragment mPrivacyFragment;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getPreferences(MODE_PRIVATE);
        boolean firstOpen = prefs.getBoolean(PREF_FIRST_OPEN_KEY, true);
        if (firstOpen) {
            showPrivacyUI();
        } else {
            showCompassUI();
        }
    }

    public void showCompassUI() {
        mCompassFragment = CompassFragment.newInstance();
        Navigator.replaceFragmentWithOutBackStack(this, R.id.frame_container, mCompassFragment);
    }

    public void showPrivacyUI() {
        mPrivacyFragment = PrivacyFragment.newInstance();
        Navigator.replaceFragmentWithOutBackStack(this, R.id.frame_container, mPrivacyFragment);
    }
}