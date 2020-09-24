package com.frestoinc.maildemo.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by frestoinc on 12,December,2019 for MailDemo.
 */
public class Executor {

    public static void ioThread(Runnable t) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(t);
    }
}
