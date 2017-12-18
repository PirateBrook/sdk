/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:01
 * Last Modified 17-12-11 下午3:55
 *
 */

package com.piratebrook.sdk.widget.fly.internal;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;

/**
 * Created by wyy on 2017-12-11.
 */

public class RotatableDrawable extends LayerDrawable {

    private float mDegree = 0;
    /**
     * Creates a new layer drawable with the list of specified layers.
     *
     * @param layers a list of drawables to use as layers in this new drawable,
     *               must be non-null
     */
    public RotatableDrawable(@NonNull Drawable[] layers) {
        super(layers);
    }

    public RotatableDrawable(Drawable drawable) {
        this(new Drawable[]{drawable});
    }

    public void setDegree(float degree) {
        mDegree = degree;
    }

    public float getDegree() {
        return mDegree;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        Rect bounds = getBounds();
        canvas.rotate(mDegree, bounds.centerX(), bounds.centerY());
        super.draw(canvas);
        canvas.restore();
    }
}
