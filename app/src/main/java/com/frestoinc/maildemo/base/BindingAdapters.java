package com.frestoinc.maildemo.base;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.BindingAdapter;
import androidx.transition.TransitionManager;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by frestoinc on 14,November,2019 for MailDemo.
 * BINDING ADAPTERS NEED TO BE STATIC.
 */
public class BindingAdapters {

    @BindingAdapter("android:animatedVisibility")
    public static void setAnimatedVisibility(View v, boolean isVisible) {
        TransitionManager.beginDelayedTransition((ViewGroup) v);
        v.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("android:error")
    public static void setError(TextInputLayout view, CharSequence sequence) {
        if (sequence == null || TextUtils.isEmpty(sequence)) {
            view.setError(null);
        } else {
            view.setError(sequence);
        }
    }
}
