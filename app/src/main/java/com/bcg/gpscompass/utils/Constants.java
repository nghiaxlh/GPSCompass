package com.bcg.gpscompass.utils;

public class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String CALIBRATE_MAGENTIC_COMPASS = "CALIBRATE_MAGENTIC_COMPASS";
    public static final int REQUEST_SETTINGS = 100;

    private static final int DAY_UNTIL = 3;
    public static final int LAUNCHER_UNTIL = 10;
    public static final int MILLI_UNTIL = DAY_UNTIL * 24 * 60 * 60 * 1000;
    public static final String PREF_NAME = "app_rater";
    public static final String LAST_PROMPT = "LAST_PROMPT";
    public static final String LAUNCHES = "LAUNCHES";
    public static final String DISABLED = "DISABLED";
    public static final String FILE_DATA_APP = "FILE_DATA_APP";
    public static final String LAST_TIME_INSIDE = "LAST_TIME_INSIDE";
    public static final String TIME_SHOW_BETTWEN_IN_APP = "TIME_SHOW_BETTWEN_IN_APP";

    public static final String TIME_ADS = "TIME_ADS";


    public static int TYPE_WIFI = 1;
    public static int TYPE_DEVICE = 2;
    public static int TYPE_NOT_INTERNET = 0;
    public static final String FILE_NAME_SHARE = "FILE_NAME_SHARE";
    public static final String RATED_IN_STORE = "RATED_IN_STORE";

    public static final String SHARED_SAVE_RATE = "SHARED_SAVE_RATE";

    public static final String EMAIL_FEEDBACK = "pyxispie@gmail.com";
    public static final String MAIL_SUBJECT = "Feedback ";
    public static final String BASE_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

    private static final String PACKAGE_NAME =
            "com.bcg.gpscompass";
    public static final String RECEIVER_EXTRA = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_KEY_ADDRESS = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String RESULT_KEY_ADDRESS_FULL = PACKAGE_NAME +
            ".RESULT_DATA_KEY_FULL";
    public static final String LOCATION_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String LOCATION_STRING_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_STRING_EXTRA";
    public static final String POLICY_URL = "https://sites.google.com/view/gps-compass-navigation-map";

    ///preference
    public static final String PREF_PRIVATE_POLICY_KEY = "PREF_PRIVATE_POLICY_KEY";
    public static final String PREF_FIRST_OPEN_KEY = "PREF_FIRST_OPEN_KEY";
}
