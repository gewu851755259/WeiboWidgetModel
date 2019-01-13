package com.minla.cpwb;

import android.app.Application;
import android.content.Context;

import com.minla.cpwb.constants.Constants;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

public class CPWBApplication extends Application {

    private Context mAppCtx;
    private AuthInfo mAuthInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppCtx = getApplicationContext();

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        WbSdk.install(this,mAuthInfo);
    }

    public Context getmAppCtx() {
        return mAppCtx;
    }
}
