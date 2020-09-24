package com.frestoinc.maildemo.ui.account.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.base.BaseFragment;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.databinding.FragmentAccountsBinding;
import com.frestoinc.maildemo.ui.sharedviewmodel.AccountViewModel;

import java.util.List;

/**
 * Created by frestoinc on 13,December,2019 for MailDemo.
 */
public class AccountFragment extends BaseFragment<FragmentAccountsBinding, AccountViewModel>
        implements DialogInterface.OnDismissListener {

    private AlertDialog alertDialog;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.authViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_accounts;
    }

    @Override
    public AccountViewModel getViewModel() {
        return (AccountViewModel) getBaseActivity().getViewModel();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewModel().getAccountUsers().observe(getBaseActivity(), this::createDialog);
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().getRoomAccountUsers();
    }

    @Override
    public void onDestroy() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

    private void createDialog(List<AccountUser> list) {
        boolean b = list == null || list.isEmpty();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getBaseActivity(), R.style.MyAlertDialogStyle)
                        .setTitle(b ? "No Accounts Found" : "Accounts");
        if (b) {
            buildNoUsers(builder);
        } else {
            buildGotUsers(builder, list);
        }
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void buildNoUsers(AlertDialog.Builder builder) {
        builder.setMessage("No accounts found on this phone.\nProceed to create one?");
        builder.setPositiveButton("Proceed", (dialog, which) -> {
            alertDialog.dismiss();
            navigateTo(R.id.activityFrame, AuthFragment.newInstance());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            alertDialog.dismiss();
            getBaseActivity().finish();
        });
    }

    private void buildGotUsers(AlertDialog.Builder builder, List<AccountUser> list) {
        String[] accounts = new String[list.size()];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = list.get(i).getAccountemail();
        }
        builder.setAdapter(
                new ArrayAdapter<>(getBaseActivity(), android.R.layout.simple_list_item_1, accounts),
                (dialog, pos) -> {
                    alertDialog.dismiss();
                    getViewModel().setAccountUser(list.get(pos));
                    syncData();
                });
        builder.setPositiveButton("Create New Account", (dialog, which) -> {
            alertDialog.dismiss();
            navigateTo(R.id.activityFrame, AuthFragment.newInstance());
        });

        builder.setNegativeButton("Remove All Accounts", (dialog, which) -> {
            alertDialog.dismiss();
            getViewModel().removeRoomAccountUsers();
        });
    }

    private void syncData() {
        if (!getBaseActivity().isNetworkConnected()) {
            getViewModel().setError(new Throwable(getString(R.string.no_network)));
            return;
        }
        getViewModel().synchroniseData();
        observeSyncState();
    }

    private void observeSyncState() {
        getViewModel().getSyncResource().observe(getViewLifecycleOwner(),
                authResource -> {
                    if (authResource != null) {
                        switch (authResource.status) {
                            case LOADING:
                                String data = authResource.data != null ? (String) authResource.data : "";
                                getBaseActivity().getNetworkFrameLayout().switchToLoading(data);
                                break;
                            case ERROR:
                                getBaseActivity().getNetworkFrameLayout().switchToNoNetwork();
                                getViewModel().getRoomAccountUsers();
                                break;
                            case SUCCESS:
                                getBaseActivity().getNetworkFrameLayout().switchToEmpty();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        alertDialog = null;
    }
}
