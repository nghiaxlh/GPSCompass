package com.bcg.gpscompass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bcg.gpscompass.ui.screen.compass.CompassFragment;
import com.bcg.gpscompass.utils.Navigator;

public class MainActivity extends AppCompatActivity {

    CompassFragment mCompassFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        if (mCompassFragment == null) {
            mCompassFragment = CompassFragment.newInstance();
            Navigator.replaceFragmentWithOutBackStack(this, R.id.frame_container, mCompassFragment);
        }
//        AdmobAdsUtils.getSharedInstance().init(getApplicationContext());
    }
}