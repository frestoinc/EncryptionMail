package com.frestoinc.maildemo.api;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by frestoinc on 26,January,2020 for MailDemo.
 */
public class CustomTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nth
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //nth
    }

    @Override
    public void afterTextChanged(Editable s) {
        //nth
    }
}
