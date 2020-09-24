package com.frestoinc.maildemo.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View v) {
        super(v);
    }

    public abstract void bind(int position);
}
