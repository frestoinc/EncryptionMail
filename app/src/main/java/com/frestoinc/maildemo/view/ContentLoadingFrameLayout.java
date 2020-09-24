package com.frestoinc.maildemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.data.enums.NetworkState;

/**
 * Created by frestoinc on 23,January,2020 for MailDemo.
 */
public class ContentLoadingFrameLayout extends RelativeLayout implements View.OnClickListener, LoaderUI {

    private View loadingView;
    private View noNetworkView;
    private NetworkState state = NetworkState.SUCCESS;

    public ContentLoadingFrameLayout(Context context) {
        super(context);
    }

    public ContentLoadingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public ContentLoadingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {
        switchToEmpty();
    }

    @Override
    public void switchToNoNetwork() {
        state = NetworkState.NO_NETWORK;
        switchToNoNetworkView();
    }


    @Override
    public void switchToEmpty() {
        state = NetworkState.SUCCESS;
        switchToEmptyView();
    }

    @Override
    public void switchToLoading(String text) {
        state = NetworkState.LOADING;
        switchToLoadingView(text);
    }

    @Override
    public NetworkState getState() {
        return state;
    }

    private void switchToLoadingView(String text) {
        if (loadingView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            loadingView = inflater.inflate(R.layout.fragment_loading, this, false);
            addView(loadingView);
        } else {
            loadingView.setVisibility(View.VISIBLE);
        }

        TextView textLoading = loadingView.findViewById(R.id.loadingText);
        textLoading.setText(text);

        if (noNetworkView != null) {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void switchToNoNetworkView() {
        if (noNetworkView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            noNetworkView = inflater.inflate(R.layout.fragment_nonetwork, this, false);
            addView(noNetworkView);
            noNetworkView.findViewById(R.id.buttonRetry).setOnClickListener(this);
        } else {
            noNetworkView.setVisibility(View.VISIBLE);
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    private void switchToEmptyView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noNetworkView != null) {
            noNetworkView.setVisibility(View.GONE);
        }
    }


    private void initAttrs(AttributeSet attrs) {
        TypedArray a =
                getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.NetworkFrameLayout, 0, 0);
        a.recycle();
    }
}
