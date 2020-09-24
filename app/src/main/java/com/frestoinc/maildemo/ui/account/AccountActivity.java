package com.frestoinc.maildemo.ui.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.databinding.ActivityPlainBinding;
import com.frestoinc.maildemo.ui.account.fragment.AccountFragment;
import com.frestoinc.maildemo.ui.main.MainActivity;
import com.frestoinc.maildemo.ui.sharedviewmodel.AccountViewModel;
import com.frestoinc.maildemo.utility.Utils;

/**
 * Created by frestoinc on 09,December,2019 for MailDemo.
 */
public class AccountActivity extends BaseActivity<ActivityPlainBinding, AccountViewModel> {


    @Override
    public int getBindingVariable() {
        return BR.authViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_plain;
    }

    @Override
    public AccountViewModel getViewModel() {
        return new ViewModelProvider(this, getFactory()).get(AccountViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPlainBinding binder = getViewDataBinding();
        setLoadingContainer(binder.loadingContainer);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(binder.activityFrame.getId(), AccountFragment.newInstance())
                .commit();
        observeError();
        observeSessionUser();
    }

    private void observeSessionUser() {
        getViewModel().init();
        getViewModel().getSessionAccountUser().observe(this, accountUser -> {
            if (accountUser != null) {
                getViewModel().setSessionAccountUser(accountUser);
                openMainActivity();
            }
        });
    }

    private void observeError() {
        getViewModel().getErrorResult().observe(
                this, s ->
                        runOnUiThread(() ->
                                Utils.showSnackBarError(this, s)));
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
