package com.frestoinc.maildemo.di;

import com.frestoinc.maildemo.TestApplication;
import com.frestoinc.maildemo.di.factory.ViewModelFactoryModule;
import com.frestoinc.maildemo.di.scope.AppScope;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by frestoinc on 22,January,2020 for MailDemo.
 */
@AppScope
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ViewModelFactoryModule.class
})
public interface TestAppComponent extends AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(TestApplication application);

        TestAppComponent build();
    }
}
