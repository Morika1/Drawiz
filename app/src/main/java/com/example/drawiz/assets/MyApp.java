package com.example.drawiz.assets;

import android.app.Application;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MySignal.init(this);
        MyImageUtils.initImageHelper(this);
        MySPV.init(this);
    }



}
