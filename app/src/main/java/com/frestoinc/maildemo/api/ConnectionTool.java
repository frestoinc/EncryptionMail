package com.frestoinc.maildemo.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by frestoinc on 24,January,2020 for MailDemo.
 */
public class ConnectionTool extends BroadcastReceiver {

    private NetworkReceiver receiver;

    public ConnectionTool(NetworkReceiver receiver) {
        this.receiver = receiver;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            receiver.onNetworkStateChanged(isNetworkAvailable(context));
        }
    }
}
