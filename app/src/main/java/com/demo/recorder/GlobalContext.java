package com.demo.recorder;

import android.content.Context;

/**
 * Created by wwq on 2017/10/7.
 */
public class GlobalContext {

    private static Context appContext;

    public static void setAppContext(Context appContext) {
        GlobalContext.appContext = appContext;
    }

    public static Context getAppContext() {
        return appContext;
    }
}
