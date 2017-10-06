package com.swipe.demo;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by wwq on 2017/10/6.
 */
public class UIUtils {
    public static int dip2px(Context context, int i) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (i * scale + 0.5f);
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        return defaultDisplay.getWidth();

    }
}
