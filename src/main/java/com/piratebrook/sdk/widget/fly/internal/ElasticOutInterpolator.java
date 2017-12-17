/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:01
 * Last Modified 17-12-11 下午3:55
 *
 */

package com.piratebrook.sdk.widget.fly.internal;

import android.view.animation.Interpolator;

/**
 * Created by wyy on 2017-12-11.
 */

public class ElasticOutInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float t) {
        if (t == 0) return 0;
        if (t >= 1) return 1;
        float p = .3f;
        float s = p / 4;
        return ((float) Math.pow(2, -10*t) * (float)Math.sin( (t-s) * (2*(float)Math.PI)/p) + 1);
    }
}
