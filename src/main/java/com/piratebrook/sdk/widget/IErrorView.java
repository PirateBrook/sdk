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
