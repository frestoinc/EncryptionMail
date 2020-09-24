package com.frestoinc.maildemo;

import com.frestoinc.maildemo.di.DaggerTestAppComponent;
import com.frestoinc.maildemo.di.TestAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by frestoinc on 22,January,2020 for MailDemo.
 */
public class TestApplication extends DemoApplication {

    public static TestAppComponent testAppComponent;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        testAppComponent = DaggerTestAppComponent.builder().application(this).build();
        return testAppComponent;
    }
}
