package com.frestoinc.maildemo.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.frestoinc.maildemo.api.ConnectionTool;
import com.frestoinc.maildemo.api.NetworkLoader;
import com.frestoinc.maildemo.api.NetworkReceiver;
import com.frestoinc.maildemo.data.enums.NetworkState;
import com.frestoinc.maildemo.di.factory.ViewModelProviderFactory;
import com.frestoinc.maildemo.utility.Utils;
import com.frestoinc.maildemo.view.ContentLoadingFrameLayout;
import com.frestoinc.maildemo.view.LoaderUI;
import com.google.gson.Gson;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel>
        extends DaggerAppCompatActivity implements NetworkLoader, NetworkReceiver {

    @Inject
    ViewModelProviderFactory factory;

    @Inject
    Gson gson;
    private ContentLoadingFrameLayout loadingContainer;
    private ConnectionTool reciever;
    private T viewDataBinding;
    private V viewModel;

    public abstract int getBindingVariable();

    public abstract @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        reciever = new ConnectionTool(this);
        performDependencyInjection();
        super.onCreate(savedInstanceState);
        performDataBinding();
    }

    public T getViewDataBinding() {
        return viewDataBinding;
    }

    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public boolean isNetworkConnected() {
        return ConnectionTool.isNetworkAvailable(getApplicationContext());
    }

    public void performDependencyInjection() {
        AndroidInjection.inject(this);
    }

    public void requestPermissionsSafely(@NonNull String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    public boolean isRequestPermissionGranted(@NonNull String[] permissions) {
        boolean granted = true;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }
        return granted;
    }

    private void performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.viewModel = viewModel == null ? getViewModel() : viewModel;
        viewDataBinding.setVariable(getBindingVariable(), viewModel);
        viewDataBinding.executePendingBindings();
    }

    public Gson getGson() {
        return gson;
    }

    public ViewModelProviderFactory getFactory() {
        return factory;
    }

    public void navigateTo(Class className) {
        Intent intent = new Intent(this, className);
        ActivityCompat.startActivity(this, intent, null);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ContentLoadingFrameLayout getLoadingContainer() {
        if (loadingContainer == null) {
            throw new RuntimeException("No loadingContainer found");
        }
        return loadingContainer;
    }

    public void setLoadingContainer(ContentLoadingFrameLayout loadingContainer) {
        this.loadingContainer = loadingContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LoaderUI getNetworkFrameLayout() {
        return getLoadingContainer();
    }

    @Override
    public void onNetworkStateChanged(boolean connected) {
        if (!connected) {
            getNetworkFrameLayout().switchToNoNetwork();
        } else {
            getNetworkFrameLayout().switchToEmpty();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(reciever, Utils.getNetworkFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciever);
    }

    @Override
    public void onBackPressed() {
        if (getLoadingContainer().getState() == NetworkState.ERROR
                || getLoadingContainer().getState() == NetworkState.NO_NETWORK) {
            getLoadingContainer().switchToEmpty();
        } else {
            super.onBackPressed();
        }
    }


}
