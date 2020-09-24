package com.frestoinc.maildemo.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.di.factory.ViewModelProviderFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerDialogFragment;

/**
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
public abstract class BaseDialog<V extends BaseViewModel> extends DaggerDialogFragment {

    @Inject
    ViewModelProviderFactory factory;

    private BaseActivity baseActivity;

    private V viewModel;

    public abstract int getBindingVariable();

    public abstract @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.baseActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection();
        super.onCreate(savedInstanceState);
        viewModel = getViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final RelativeLayout rootlayout = new RelativeLayout(getActivity());
        rootlayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        final BottomSheetDialog dialog = new BottomSheetDialog(baseActivity, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootlayout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        return dialog;
    }


    @Override
    public void onDetach() {
        baseActivity = null;
        super.onDetach();
    }

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public ViewModelProviderFactory getFactory() {
        return factory;
    }

    private void performDependencyInjection() {
        AndroidSupportInjection.inject(this);
    }

    public void hideKeyboard() {
        if (baseActivity != null) {
            baseActivity.hideSoftKeyboard();
        }
    }
}
