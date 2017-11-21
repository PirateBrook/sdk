package com.piratebrook.sdk.component;

import android.app.Application;
import android.content.Context;

/**
 * For Hotfix function
 */
public class BaseApplication extends Application {

    public static Application sInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
