package com.minla.cpwb;

import android.app.Application;
import android.content.Context;

public class CPWBApplication extends Application {

    private Context mAppCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppCtx = getApplicationContext();
    }

    public Context getmAppCtx() {
        return mAppCtx;
    }
}
