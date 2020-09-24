package com.frestoinc.maildemo.testrule;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.ui.account.AccountActivity;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Created by frestoinc on 22,January,2020 for MailDemo.
 */
@LargeTest
public class AccountActivityTestRule extends TestWatcher {

  public ActivityTestRule<AccountActivity> activityTestRule =
          new ActivityTestRule<>(AccountActivity.class, false, false);

  AccountUser accountUser;

  @Override
  protected void starting(Description description) {
    activityTestRule.launchActivity(null);

  }

  public AccountActivity getActivity() {
    return activityTestRule.getActivity();
  }

}
