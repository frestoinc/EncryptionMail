package com.frestoinc.maildemo.ui.account.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.api.CustomTextWatcher;
import com.frestoinc.maildemo.base.BaseFragment;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.databinding.FragmentAuthenticateBinding;
import com.frestoinc.maildemo.ui.sharedviewmodel.AccountViewModel;

import java.util.Objects;

/**
 * Created by frestoinc on 13,December,2019 for MailDemo.
 */
public class AuthFragment extends BaseFragment<FragmentAuthenticateBinding, AccountViewModel>
        implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

    private FragmentAuthenticateBinding binder;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.authViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_authenticate;
    }

    @Override
    public AccountViewModel getViewModel() {
        return (AccountViewModel) getBaseActivity().getViewModel();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBinder();
        registerListener();
        observeLoginFormState();
        observeNetworkState();
    }

    private void registerBinder() {
        binder = getViewDataBinding();
    }

    private void registerListener() {
        binder.fragAuthEmail.addTextChangedListener(getTextWatcher());
        binder.fragAuthPwd.addTextChangedListener(getTextWatcher());
        binder.fragAuthWebServer.addTextChangedListener(getTextWatcher());
        binder.onPremBox.setOnCheckedChangeListener(this);
        binder.fragAuthBtn.setOnClickListener(this);
    }

    private void observeLoginFormState() {
        getViewModel().getLoginFormState().observe(getViewLifecycleOwner(), state -> {
            if (null == state) {
                return;
            }
            binder.fragAuthBtn.setEnabled(state.isDataValid());
            if (!state.isDataValid()) {
                if (state.getUsernameError() != null) {
                    binder.fragAuthEmailLayout.setError(getString(state.getUsernameError()));
                }
                if (state.getPasswordError() != null) {
                    binder.fragAuthPwdLayout.setError(getString(state.getPasswordError()));
                }
                if (state.getServerError() != null) {
                    binder.fragAuthWebServerLayout.setError(getString(state.getServerError()));
                }
            }
        });
    }

    private void observeNetworkState() {
        getViewModel().getSyncResource().observe(getViewLifecycleOwner(), authResource -> {
            if (authResource != null) {
                switch (authResource.status) {
                    case LOADING:
                        getBaseActivity().getNetworkFrameLayout().switchToLoading("Authenticating");
                        break;
                    case ERROR:
                        getBaseActivity().getNetworkFrameLayout().switchToNoNetwork();
                        break;
                    case SUCCESS:
                        getBaseActivity().getNetworkFrameLayout().switchToEmpty();
                        getBaseActivity().getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {
        AccountUser accountUser = new AccountUser(
                String.valueOf(binder.fragAuthEmail.getText()),
                String.valueOf(binder.fragAuthPwd.getText()),
                String.valueOf(binder.fragAuthWebServer.getText())
        );

        if (!getBaseActivity().isNetworkConnected()) {
            getViewModel().setError(new Throwable(getString(R.string.no_network)));
            return;
        }
        getViewModel().getAccountFromAs(accountUser);
    }

    private CustomTextWatcher getTextWatcher() {
        return new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binder.fragAuthEmailLayout.setError(null);
                binder.fragAuthPwdLayout.setError(null);
                binder.fragAuthWebServerLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                getViewModel().observeFormDataChanged(
                        Objects.requireNonNull(binder.fragAuthEmail.getText()).toString(),
                        Objects.requireNonNull(binder.fragAuthPwd.getText()).toString(),
                        Objects.requireNonNull(binder.fragAuthWebServer.getText()).toString()
                );
            }
        };
    }
}
