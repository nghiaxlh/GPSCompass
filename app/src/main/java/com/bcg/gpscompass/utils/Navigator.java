package com.bcg.gpscompass.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Navigator {
    static public void addFragment(AppCompatActivity activity, int layoutId, Fragment newFragment) {
        activity.getSupportFragmentManager().findFragmentById(layoutId);
        if (newFragment != null) {
            activity.getSupportFragmentManager().beginTransaction().addToBackStack(null).add(layoutId, newFragment).commitAllowingStateLoss();
        }
    }

    static public void addFragmentWithOutBackStack(AppCompatActivity activity, int layoutId, Fragment newFragment, boolean isSaveState) {
        activity.getSupportFragmentManager().findFragmentById(layoutId);
        if (newFragment != null) {
            activity.getSupportFragmentManager().beginTransaction().add(layoutId, newFragment).commitAllowingStateLoss();
        }
    }

    static public void replaceFragment(AppCompatActivity activity, int layoutId, Fragment newFragment) {
        activity.getSupportFragmentManager().findFragmentById(layoutId);
        if (newFragment != null) {
            activity.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(layoutId, newFragment).commitAllowingStateLoss();
        }
    }

    static public void replaceFragmentWithOutBackStack(AppCompatActivity activity, int layoutId, Fragment newFragment) {
        activity.getSupportFragmentManager().findFragmentById(layoutId);
        if (newFragment != null) {
            activity.getSupportFragmentManager().beginTransaction().replace(layoutId, newFragment).commitAllowingStateLoss();
        }
    }
}
