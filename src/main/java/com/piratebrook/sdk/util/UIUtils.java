/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:17
 * Last Modified 17-8-29 下午5:14
 *
 */

package com.piratebrook.sdk.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Jing on 15/5/18.
 */
public class UIUtils {

    public static final int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int lighterColor(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static int darkerColor(int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    public static int getThemeColor(Context ctx, int attr) {
        TypedValue tv = new TypedValue();
        if (ctx.getTheme().resolveAttribute(attr, tv,true)) {
            return tv.data;
        }
        return 0;
    }

    /**
     * helper method to get the color by attr (which is defined in the style) or by resource.
     *
     * @param ctx
     * @param attr
     * @param res
     * @return
     */
    public static int getThemeColorFromAttrOrRes(Context ctx, int attr, int res) {
        int color = getThemeColor(ctx, attr);
        if (color == 0) {
            color = ctx.getResources().getColor(res);
        }
        return color;
    }

    public static void clearAnimator(View v) {
        v.setAlpha(1);
        v.setScaleY(1);
        v.setScaleX(1);
        v.setTranslationY(0);
        v.setTranslationX(0);
        v.setRotation(0);
        v.setRotationY(0);
        v.setRotationX(0);
        // @TODO https://code.google.com/p/android/issues/detail?id=80863
        // v.setPivotY(v.getMeasuredHeight() / 2);
        v.setPivotY(v.getMeasuredHeight() / 2);
        v.setPivotX(v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null);
    }
}
