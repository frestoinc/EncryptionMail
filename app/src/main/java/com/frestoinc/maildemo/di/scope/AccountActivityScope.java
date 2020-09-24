package com.frestoinc.maildemo.di.scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by frestoinc on 13,December,2019 for MailDemo.
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface AccountActivityScope {
}
