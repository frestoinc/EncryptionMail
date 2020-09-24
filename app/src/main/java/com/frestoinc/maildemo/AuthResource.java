package com.frestoinc.maildemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frestoinc.maildemo.data.enums.NetworkState;

/**
 * Created by frestoinc on 09,December,2019 for MailDemo.
 */

public class AuthResource<T> {

    @NonNull
    public final NetworkState status;

    @Nullable
    public final T data;

    @Nullable
    public final String errorMessage;


    public AuthResource(@NonNull NetworkState status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.errorMessage = message;
    }

    public static <T> AuthResource<T> authenticated(@Nullable T data) {
        return new AuthResource<>(NetworkState.SUCCESS, data, null);
    }

    public static <T> AuthResource<T> error(@Nullable String msg) {
        return new AuthResource<>(NetworkState.ERROR, null, msg);
    }

    public static <T> AuthResource<T> loading(@Nullable T data) {
        return new AuthResource<>(NetworkState.LOADING, data, null);
    }

}
