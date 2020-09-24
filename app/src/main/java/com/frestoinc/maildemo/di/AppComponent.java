package com.frestoinc.maildemo.di;

import android.app.Application;

import com.frestoinc.maildemo.DemoApplication;
import com.frestoinc.maildemo.SessionManager;
import com.frestoinc.maildemo.di.factory.ViewModelFactoryModule;
import com.frestoinc.maildemo.di.module.DemoAppModule;
import com.frestoinc.maildemo.di.scope.AppScope;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * ROOT DAGGER COMPONENT {@link AndroidSupportInjectionModule}
 * HELPS WIT GENERATION AND LOCATIONS OF SUB-COMPONENTS
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
@AppScope
@Component(modules = {
        AndroidSupportInjectionModule.class,
        DemoAppModule.class,
        ActivityBuilder.class,
        ViewModelFactoryModule.class
})

public interface AppComponent extends AndroidInjector<DemoApplication> {

    SessionManager sessionManager();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
