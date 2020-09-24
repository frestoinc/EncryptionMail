package com.frestoinc.maildemo.ui.main.container;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.activesync.EasFolderType;
import com.frestoinc.maildemo.base.BaseFragment;
import com.frestoinc.maildemo.databinding.FragmentFolderBinding;
import com.frestoinc.maildemo.ui.sharedviewmodel.MainViewModel;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;

import javax.inject.Inject;

/**
 * Created by frestoinc on 23,December,2019 for MailDemo.
 */
public class FolderFragment extends BaseFragment<FragmentFolderBinding, MainViewModel> {

    @Inject
    public MessageAdapter adapter;

    @Inject
    public DividerItemDecoration decoration;
    private FragmentFolderBinding binder;
    private EasFolderType type;

    public static FolderFragment newInstance() {
        return new FolderFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.mainViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_folder;
    }

    @Override
    public MainViewModel getViewModel() {
        return (MainViewModel) getBaseActivity().getViewModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString(Constants.EAS_FOLDER_TYPE) != null) {
            type =
                    getBaseActivity().getGson().fromJson(
                            getArguments().getString(Constants.EAS_FOLDER_TYPE), EasFolderType.class);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBinder();
        observeGeneralFolder();
        observeNetworkState();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        getViewModel().getFolder(type);
    }

    private void init() {
        ActionBar actionBar = getBaseActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(EasFolderType.valueOf(type));
        }

        binder.containerRc.setLayoutManager(new LinearLayoutManager(getBaseActivity()));
        binder.containerRc.addItemDecoration(decoration);
        binder.containerRc.setAdapter(adapter);
        binder.containerSrl.setOnRefreshListener(() -> {
            if (!getBaseActivity().isNetworkConnected()) {
                getViewModel().setError(new Throwable(getString(R.string.no_network)));
                return;
            }
            getViewModel().getFolderAS(type);
        });
    }

    private void registerBinder() {
        binder = getViewDataBinding();
    }

    private void observeGeneralFolder() {
        getViewModel().getAppFolders().observe(getViewLifecycleOwner(),
                folders -> {
                    if (folders != null && !folders.isEmpty() && type != null) {
                        getViewModel().getFolder(type);
                        getViewModel().getGeneral()
                                .observe(getViewLifecycleOwner(),
                                        messageList -> {
                                            Utils.setNoMessageBackground(
                                                    messageList.isEmpty(),
                                                    binder.containerForm,
                                                    binder.containerNoMessages);
                                            adapter.setSource(messageList);
                                        });
                    }
                });
    }

    private void observeNetworkState() {
        getViewModel().getSyncResource().observe(getViewLifecycleOwner(), authResource -> {
            if (authResource != null) {
                switch (authResource.status) {
                    case LOADING:
                        String data = authResource.data != null ? (String) authResource.data : "";
                        getBaseActivity().getNetworkFrameLayout().switchToLoading(data);
                        break;
                    case ERROR:
                        getBaseActivity().getNetworkFrameLayout().switchToNoNetwork();
                        binder.containerSrl.setRefreshing(false);
                        break;
                    case SUCCESS:
                        getBaseActivity().getNetworkFrameLayout().switchToEmpty();
                        binder.containerSrl.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
