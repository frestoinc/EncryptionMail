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
import android.os.CountDownTimer;

import com.frestoinc.maildemo.utility.Utils;

import java.util.concurrent.Semaphore;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Created by frestoinc on 18,November,2019 for FullMailDemo.
 */
public class PinInputHandler implements CallbackHandler {

    private final Semaphore semaphore = new Semaphore(0, true);

    private PasswordCallback pinCallback;
    private Context ctx;
    private AlertDialog dialog;
    private boolean init = false;
    private final Runnable passwordDialog = new Runnable() {
        public void run() {
            PasswordDialogBuilder builder = new PasswordDialogBuilder(ctx) {
                @Override
                public boolean onClicked(String pwd) {
                    init = true;
                    pinCallback.setPassword(pwd.toCharArray());
                    semaphore.release();
                    return true;
                }

                @Override
                public boolean onCancelled() {
                    init = true;
                    semaphore.release();
                    return true;
                }
            };
            dialog = builder.create();
            dialog.show();
            new CountDownTimer(20000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //nth
                }

                @Override
                public void onFinish() {
                    if (!init && dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        semaphore.release();
                    }
                }
            }.start();
        }
    };

    public PinInputHandler(Context context) {
        this.ctx = context;
    }

    @Override
    public void handle(Callback[] callbacks) {
        for (Callback cb : callbacks) {
            if (cb instanceof PasswordCallback) {
                pinCallback = (PasswordCallback) cb;
                showPasswordDialog();
            } else {
                showError(new UnsupportedCallbackException(cb,
                        "Unknown callback not supported: " + cb.getClass().getName()));
            }
        }
    }

    private void showPasswordDialog() {
        ((Activity) ctx).runOnUiThread(passwordDialog);
        try {
            semaphore.acquire();
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        ((Activity) ctx)
                .runOnUiThread(() -> Utils.showSnackBarError((Activity) ctx, e.getMessage()));
    }
}
