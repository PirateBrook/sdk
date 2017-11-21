package com.piratebrook.sdk.component;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.piratebrook.sdk.R;
import com.piratebrook.sdk.util.LogUtils;
import com.piratebrook.sdk.widget.ErrorView;
import com.piratebrook.sdk.widget.IEventCallback;

/**
 * Created by wyy on 2017-11-21.
 */

public abstract class ErrorViewActivity extends BaseActivity implements IEventCallback {

    public static final String TAG = "ErrorViewActivity";

    private ViewGroup mRootView;

    private FrameLayout mContentView, mErrorContentView;

    private ErrorView mErrorView;


    protected ErrorView setErrorView() {
        mErrorView = new ErrorView(this);
        return mErrorView;
    }

    @Override
    public void setContentView(View view) {
        mRootView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_error, null);
        mContentView = mRootView.findViewById(R.id.vg_content);
        mErrorContentView = mRootView.findViewById(R.id.vg_error);
        mContentView.addView(view);
        ErrorView errorView = setErrorView();
        if (errorView != null) {
            mErrorContentView.addView(errorView);
        }
        getDelegate().setContentView(mRootView);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    protected void log(String message) {
        LogUtils.i(TAG, message);
    }

    public boolean showErrorView() {
        return mErrorView.showErrorView();
    }

    public boolean dismissErrorView() {
        return mErrorView.dismissErrorView();
    }

    public boolean showNoDataView() {
        return mErrorView.showNoDataView();
    }

    @Override
    public void onRetry() {

    }

    @Override
    public void onClose() {

    }
}
