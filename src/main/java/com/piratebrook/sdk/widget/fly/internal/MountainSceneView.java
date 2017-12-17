/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:01
 * Last Modified 17-12-11 下午3:55
 *
 */

package com.piratebrook.sdk.widget.fly.internal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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
    private static final int COLOR_TREE_1_BTRUNK = Color.parseColor("#0C3E48");
    private static final int COLOR_TREE_2_BRANCH = Color.parseColor("#34888F");
    private static final int COLOR_TREE_2_BTRUNK = Color.parseColor("#1B6169");
    private static final int COLOR_TREE_3_BRANCH = Color.parseColor("#57B1AE");
    private static final int COLOR_TREE_3_BTRUNK = Color.parseColor("#62A4AD");

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
    public void onPullProgress(PullHeaderLayout parent, int state, float progress) {

    }
}
