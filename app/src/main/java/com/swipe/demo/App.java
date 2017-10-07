package com.swipe.demo;

import android.app.Application;

import com.demo.recorder.GlobalContext;

/**
 * Created by wwq on 2017/10/7.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.setAppContext(this);
    }
}
