package com.frestoinc.maildemo.base.rx;


import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by frestoinc on 12,December,2019 for MailDemo.
 */
public class TestSchedulerProvider implements SchedulerProvider {
    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
