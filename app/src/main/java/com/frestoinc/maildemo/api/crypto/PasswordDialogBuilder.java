/*
 *
 *
 *  * Copyright (C) 2006 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.frestoinc.maildemo.api.crypto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frestoinc.maildemo.R;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Created by frestoinc on 18,November,2019 for FullMailDemo.
 */
public abstract class PasswordDialogBuilder extends AlertDialog.Builder
        implements DialogInterface.OnClickListener {

    private TextInputEditText editText;
    private Context ctx;

    public PasswordDialogBuilder(Context context) {
        super(context, R.style.MyAlertDialogStyle);
        this.ctx = context;
        setTitle("Password");
        editText = new TextInputEditText(context);
        editText.setBackgroundTintList(ColorStateList.valueOf(context.getColor(android.R.color.white)));
        editText.setHint("Please enter your card password: ");
        editText.setSingleLine();
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onClicked(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(80, 0, 80, 0);
        editText.setLayoutParams(params);
        LinearLayout layout = new LinearLayout(context);
        layout.addView(editText);
        setView(layout);
        setCancelable(false);
        setPositiveButton("OK", this);
        setNegativeButton("Cancel", this);
        //setOnCancelListener(this);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE && onClicked(String.valueOf(editText.getText()))) {
            ((Activity) ctx).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialogInterface.dismiss();
        } else {
            onCancel(dialogInterface);
        }
    }

    private void onCancel(DialogInterface dialogInterface) {
        ((Activity) ctx).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogInterface.dismiss();
        onCancelled();
    }

    public abstract boolean onClicked(String pwd);

    public abstract boolean onCancelled();
}
