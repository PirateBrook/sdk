/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:01
 * Last Modified 17-12-11 下午3:55
 *
 */

package com.piratebrook.sdk.widget.fly.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.piratebrook.sdk.widget.fly.IPullHeader;
import com.piratebrook.sdk.widget.fly.PullHeaderLayout;

/**
 * Created by wyy on 2017-12-11.
 */

public class MountainSceneView extends View implements IPullHeader {

    private static final int COLOR_BACKGROUND = Color.parseColor("#7ECEC9");
    private static final int COLOR_MOUNTAIN_1 = Color.parseColor("#86DAD7");
    private static final int COLOR_MOUNTAIN_2 = Color.parseColor("#3C929C");
    private static final int COLOR_MOUNTAIN_3 = Color.parseColor("#3E5F73");
    private static final int COLOR_TREE_1_BRANCH = Color.parseColor("#1F7177");
    private static final int COLOR_TREE_1_TRUNK = Color.parseColor("#0C3E48");
    private static final int COLOR_TREE_2_BRANCH = Color.parseColor("#34888F");
    private static final int COLOR_TREE_2_TRUNK = Color.parseColor("#1B6169");
    private static final int COLOR_TREE_3_BRANCH = Color.parseColor("#57B1AE");
    private static final int COLOR_TREE_3_TRUNK = Color.parseColor("#62A4AD");

    private static final int WIDTH = 240;
    private static final int HEIGHT = 180;

    private static final int TREE_WIDTH = 100;
    private static final int TREE_HEIGHT = 200;

    private Paint mMountPaint = new Paint();
    private Paint mTrunkPaint = new Paint();
    private Paint mBranchPaint = new Paint();
    private Paint mBoarderPaint = new Paint();

    private Path mMount1 = new Path();
    private Path mMount2 = new Path();
    private Path mMount3 = new Path();
    private Path mTrunk = new Path();
    private Path mBranch = new Path();

    private float mScaleX = 5f;
    private float mScaleY = 5f;
    private float mMoveFactor = 0;
    private float mBounceMax = 1;
    private float mTreeBendFactor = Float.MAX_VALUE;
    private Matrix mTransMatrix = new Matrix();

    public MountainSceneView(Context context) {
        this(context, null);
    }

    public MountainSceneView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MountainSceneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMountPaint.setAntiAlias(true);
        mMountPaint.setStyle(Paint.Style.FILL);

        mTrunkPaint.setAntiAlias(true);
        mBranchPaint.setAntiAlias(true);

        mBoarderPaint.setAntiAlias(true);
        mBoarderPaint.setStyle(Paint.Style.STROKE);
        mBoarderPaint.setStrokeWidth(2);
        mBoarderPaint.setStrokeJoin(Paint.Join.ROUND);

        updateMountainPath(mMoveFactor);
        updateTreePath(mMoveFactor, true);
    }

    private void updateTreePath(float factor, boolean force) {
        if (factor == mTreeBendFactor && !force) {
            return;
        }

        final Interpolator interpolator = PathInterpolatorCompat.create(0.8f, -0.5f * factor);

        final float width = TREE_WIDTH;
        final float height = TREE_HEIGHT;

        final float maxMove = width * 0.3f * factor;
        final float trunkSize = width * 0.05f;
        final float branchSize = width * 0.2f;
        final float x0 = width / 2;
        final float y0 = height;

        final int N = 25;
        final float dp = 1f / N;
        final float dy = -dp * height;
        float y = y0;
        float p = 0;
        float[] xx = new float[N + 1];
        float[] yy = new float[N + 1];
        for (int i = 0; i < N; i++) {
            xx[i] = interpolator.getInterpolation(p) * maxMove + x0;
            yy[i] = y;

            y += dy;
            p += dp;
        }

        mTrunk.reset();
        mTrunk.moveTo(x0 - trunkSize, y0);
        int max = (int) (N * 0.7f);
        int max1 = (int) (max * 0.5f);
        float diff = max - max1;
        for (int i = 0; i < max; i++) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] - trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] - trunkSize * (max - i) / diff, yy[i]);
            }
        }

        for (int i = max - 1; i >= 0; i--) {
            if (i < max1) {
                mTrunk.lineTo(xx[i] + trunkSize, yy[i]);
            } else {
                mTrunk.lineTo(xx[i] - trunkSize * (max - i), yy[i]);
            }
        }
        mTrunk.close();

        mBranch.reset();
        int min = (int) (N * 0.4f);
        diff = N - min;

        mBranch.moveTo(xx[min] - branchSize, yy[min]);
        mBranch.addArc(new RectF(xx[min] - branchSize, yy[min] - branchSize, xx[min] + branchSize,
                yy[min] + branchSize), 0f, 180f);
        for (int i = min; i <= N; i++) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] - branchSize + f * f * branchSize, yy[i]);
        }
        for (int i = N; i >= min; i--) {
            float f = (i - min) / diff;
            mBranch.lineTo(xx[i] + branchSize - f * f * branchSize, yy[i]);
        }
    }

    private void updateMountainPath(float factor) {

        mTransMatrix.reset();
        mTransMatrix.setScale(mScaleX, mScaleY);

        int offset1 = (int) (10 * factor);
        mMount1.reset();
        mMount1.moveTo(0, 95 + offset1);
        mMount1.lineTo(55, 74 + offset1);
        mMount1.lineTo(146, 104 + offset1);
        mMount1.lineTo(227, 72 + offset1);
        mMount1.lineTo(WIDTH, 80 + offset1);
        mMount1.lineTo(WIDTH, HEIGHT);
        mMount1.lineTo(0, HEIGHT);
        mMount1.close();
        mMount1.transform(mTransMatrix);
    }

    @Override
    public void onPullProgress(PullHeaderLayout parent, int state, float factor) {
        float bendFactor;
        if (state == PullHeaderLayout.STATE_BOUNCE) {
            if (factor < mBounceMax) {
                mBounceMax = factor;
            }
            bendFactor = factor;
        } else {
            mBounceMax = factor;
            bendFactor = Math.max(0, factor);
        }

        mMoveFactor = Math.max(0, mBounceMax);
        updateMountainPath(mMoveFactor);
        updateTreePath(bendFactor, false);

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(COLOR_BACKGROUND);

        mMountPaint.setColor(COLOR_MOUNTAIN_1);
        canvas.drawPath(mMount1, mMountPaint);

        canvas.save();
        canvas.scale(-1, 1, getWidth() / 2, 0);
        drawTree(canvas, 0.12f * mScaleX, 180 * mScaleX, (93 + 20 * mMoveFactor) * mScaleY,
                COLOR_TREE_3_TRUNK, COLOR_TREE_3_BRANCH);
        drawTree(canvas, 0.1f * mScaleX, 200 * mScaleX, (96 + 20 * mMoveFactor) * mScaleY,
                COLOR_TREE_3_TRUNK, COLOR_TREE_3_BRANCH);
        canvas.restore();
        mMountPaint.setColor(COLOR_MOUNTAIN_2);
        canvas.drawPath(mMount2, mMountPaint);

        drawTree(canvas, 0.2f * mScaleX, 160 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_1_TRUNK, COLOR_TREE_1_BRANCH);

        drawTree(canvas, 0.14f * mScaleX, 180 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_TRUNK ,COLOR_TREE_2_BRANCH);

        drawTree(canvas, 0.16f * mScaleX, 140 * mScaleX, (105 + 30 * mMoveFactor) * mScaleY,
                COLOR_TREE_2_TRUNK ,COLOR_TREE_2_BRANCH);

        mMountPaint.setColor(COLOR_MOUNTAIN_3);
        canvas.drawPath(mMount3, mMountPaint);
    }

    private void drawTree(Canvas canvas, float scale, float baseX, float baseY, int colorTrunk, int colorBranch) {

        canvas.save();

        final float dx = baseX - TREE_WIDTH * scale / 2;
        final float dy = baseY - TREE_HEIGHT * scale;
        canvas.translate(scale, scale);

        mBranchPaint.setColor(colorBranch);
        canvas.drawPath(mBranch, mBranchPaint);
        mTrunkPaint.setColor(colorTrunk);
        canvas.drawPath(mTrunk, mTrunkPaint);
        mBoarderPaint.setColor(colorTrunk);
        canvas.drawPath(mBranch, mBoarderPaint);

        canvas.restore();
    }
}
































