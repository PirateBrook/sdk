/*
 * Copyright (c) 2017.
 * Create by PirateBrook 2017/today
 * Last Modified 17-11-21 下午4:54
 *
 */

package com.piratebrook.sdk.widget;

/**
 * Created by wyy on 2017-11-21.
 */

public interface IErrorView {

    boolean showErrorView();

    boolean showNoDataView();

    boolean dismissErrorView();

    void setEventCallback(IEventCallback eventCallback);
}
