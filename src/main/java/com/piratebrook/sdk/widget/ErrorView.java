package com.piratebrook.sdk.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.piratebrook.sdk.R;
import com.piratebrook.sdk.util.LogUtils;

public class ErrorView extends RelativeLayout implements IErrorView {

    protected IEventCallback mEventCallback;

    private View mRootView;

    private Button mBtnRetry;

    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.simple_error_view, this);
        mBtnRetry = mRootView.findViewById(R.id.btn_retry);

        mBtnRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventCallback != null) {
                    mEventCallback.onRetry();
                }
            }
        });
    }

    @Override
    public boolean showErrorView() {
        LogUtils.d("show error view");
        setVisibility(VISIBLE);
        return true;
    }

    @Override
    public boolean showNoDataView() {
        LogUtils.d("show no data view");
        return true;
    }

    @Override
    public boolean dismissErrorView() {
        LogUtils.d("show error view");
        setVisibility(GONE);
        return true;
    }

    @Override
    public void setEventCallback(@NonNull IEventCallback eventCallback) {
        mEventCallback = eventCallback;
    }
}
