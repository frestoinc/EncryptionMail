package com.frestoinc.maildemo;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
public class FormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer serverError;

    private boolean isDataValid;

    public FormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer serverError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.serverError = serverError;
        this.isDataValid = false;
    }

    public FormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.serverError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getServerError() {
        return serverError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
