/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:01
 * Last Modified 17-12-11 下午3:55
 *
 */

package com.piratebrook.sdk.widget.fly;

/**
 * Created by wyy on 2017-12-11.
 */

public class HeaderController implements IHeaderController {

    int mHeight;
    int mMaxHeight;
    int mMinHeight;
    float mOverDistance;

    float mResistance = 0.5f;
    boolean mIsInTouch = false;
    float mScroll = 0;
    int mMaxScroll = 0;
    int mMinScroll = 0;

    public HeaderController(int height, int maxHeight, int minHeight) {
        if (maxHeight <= 0) {
            throw new IllegalArgumentException("maxHeight must > 0");
        }

        setSize(height, maxHeight, minHeight);
    }

    @Override
    public void setSize(int height, int maxHeight, int minHeight) {
        mHeight = Math.max(0, height);
        mMaxHeight = Math.max(0, maxHeight);
        mMinHeight = Math.max(0, minHeight);
        mOverDistance = mMaxHeight - mHeight;

        mScroll = 0;
        mMaxScroll = mHeight - mMinHeight;
        mMinScroll = mHeight - mMaxHeight;
    }

    @Override
    public int getMaxHeight() {
        return mMaxHeight;
    }

    @Override
    public int getMinHeight() {
        return mMinHeight;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getScroll() {
        return (int) mScroll;
    }

    @Override
    public int getMaxScroll() {
        return mMaxScroll;
    }

    @Override
    public int getMinScroll() {
        return mMinScroll;
    }

    @Override
    public int getCurPosition() {
        return (int) (mHeight - mScroll);
    }

    @Override
    public boolean isInTouch() {
        return mIsInTouch;
    }

    /**
     * Check if can scroll down to show top
     * @return
     */
    @Override
    public boolean canScrollDown() {
        return mScroll > mMinScroll;
    }

    /**
     * check if can scroll up to show bottom
     * @return
     */
    @Override
    public boolean canScrollUp() {
        return mScroll < mMaxScroll;
    }

    @Override
    public int move(float deltaY) {
        float willTo;
        float consumed = deltaY;
        if (mScroll >= 0) {
            willTo = mScroll + deltaY;
            if (willTo < 0) {
                willTo = willTo * mResistance;
                if (willTo < mMinScroll) {
                    consumed -= (willTo - mMinScroll) / mResistance;
                    willTo = mMinScroll;
                }
            } else if (willTo > mMaxScroll) {
                consumed -= willTo - mMaxScroll;
                willTo = mMaxScroll;
            }
        } else {
            willTo = mScroll + deltaY * mResistance;
            if (willTo > 0) {
                willTo = willTo / mResistance;
                if (willTo > mMaxScroll) {
                    consumed -= willTo - mMaxScroll;
                    willTo = mMaxScroll;
                }
            } else if (willTo < mMinScroll) {
                consumed -= willTo - mMinScroll;
                willTo = mMinScroll;
            }
        }
        mScroll = willTo;
        return (int) consumed;
    }

    @Override
    public boolean isOverHeight() {
        return mScroll < 0;
    }

    @Override
    public float getMovePercentage() {
        return -mScroll / mOverDistance;
    }

    @Override
    public boolean needSendRefresh() {
        return getMovePercentage() > 0.9f;
    }
}
