package com.example.jcd.qryesno;

import android.app.Application;

/**
 * Created by melekhin on 28.07.2017.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static String getUrl(String code) {
        if(isDebug()) {
            return "https://raw.githubusercontent.com/alexmelyon/QRYesNo/master/test_query.json";
        } else {
            return "http://tankionline.com/pages/moscow/get_info/?code=" + code;
        }
    }
}
