/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017 / 17-12-11 下午4:27
 * Last Modified 17-12-11 下午4:27
 *
 */

package com.piratebrook.sdk.widget.fly;

/**
 * Created by wyy on 2017-12-11.
 */

public interface IHeaderController {
    void setSize(int height, int maxHeight, int minHeight);
    int getMaxHeight();
    int getMinHeight();
    int getHeight();
    int getScroll();
    int getMaxScroll();
    int getMinScroll();
    int getCurPosition();
    boolean isInTouch();
    boolean canScrollDown();
    boolean canScrollUp();
    int move(float deltaY);
    boolean isOverHeight();
    float getMovePercentage();
    boolean needSendRefresh();
}
