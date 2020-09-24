package com.frestoinc.maildemo.di.module.activity;

import android.content.Context;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.frestoinc.maildemo.api.listener.CardListener;
import com.frestoinc.maildemo.api.listener.MainCardListener;
import com.frestoinc.maildemo.api.listener.MainRowListener;
import com.frestoinc.maildemo.api.listener.RowListener;
import com.frestoinc.maildemo.di.scope.MainActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by frestoinc on 08,January,2020 for MailDemo.
 */
@Module
public class ActivityModule {

    @MainActivityScope
    @Provides
    LinearLayoutManager provideLinearLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @MainActivityScope
    @Provides
    DividerItemDecoration provideDividerItemDecoration(Context context, LinearLayoutManager manager) {
        return new DividerItemDecoration(context, manager.getOrientation());
    }

    @MainActivityScope
    @Provides
    RowListener provideMessageRowListener() {
        return new MainRowListener();
    }

    @MainActivityScope
    @Provides
    CardListener provideCardListener() {
        return new MainCardListener();
    }


}
