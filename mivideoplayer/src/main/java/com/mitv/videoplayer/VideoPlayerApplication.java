package com.mitv.videoplayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by chengsl on 8/4/17.
 */

public class VideoPlayerApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        android.support.multidex.MultiDex.install(this);
    }
}
