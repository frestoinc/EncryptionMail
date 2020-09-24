package com.frestoinc.maildemo.ui.contact;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.api.CustomTextWatcher;
import com.frestoinc.maildemo.api.listener.MainRowListener;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.data.model.GalContact;
import com.frestoinc.maildemo.databinding.ActivityAddressbookBinding;
import com.frestoinc.maildemo.utility.Utils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by frestoinc on 08,January,2020 for MailDemo.
 */

public class AddressBookActivity extends BaseActivity<ActivityAddressbookBinding, AddressBookViewModel>
        implements TextView.OnEditorActionListener, View.OnClickListener {

    @Inject
    public LinearLayoutManager layoutManager;

    @Inject
    public DividerItemDecoration decoration;

    @Inject
    public AddressBookAdapter adapter;

    @Inject
    public MainRowListener listener;

    private ActivityAddressbookBinding binder;

    @Override
    public int getBindingVariable() {
        return BR.adViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_addressbook;
    }

    @Override
    public AddressBookViewModel getViewModel() {
        return new ViewModelProvider(this, getFactory()).get(AddressBookViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinder();
        subscribeObservers();
        initView();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        hideSoftKeyboard();
        if (v.length() < 4) {
            getViewModel().setError(new Throwable("Character length must be more than 3!"));
            return false;
        }
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                && Utils.isStringValid(v.getText().toString())) {
            getViewModel().getContactsFromAS(v.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.abBtnSearch:
                onEditorAction(binder.abName, EditorInfo.IME_ACTION_SEARCH, null);
                break;
            case R.id.abBtnAdd:
                parseInputText();
                break;
            default:
                break;
        }
    }

    private void registerBinder() {
        binder = getViewDataBinding();
        setLoadingContainer(binder.loadingContainer);
    }

    private void initView() {
        setSupportActionBar(binder.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Address Book");
        }
        binder.abName.addTextChangedListener(getTextWatcher());
        binder.abName.setOnEditorActionListener(this);
        binder.abBtnAdd.setOnClickListener(this);
        binder.abBtnSearch.setOnClickListener(this);
        binder.adRc.setLayoutManager(layoutManager);
        binder.adRc.addItemDecoration(decoration);
        binder.adRc.setAdapter(adapter);
        getViewDataBinding().fragmentCaPasswordTwo.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getViewDataBinding().fragmentCaScroll.fullScroll(View.FOCUS_DOWN)
                return true;
            }
            return false;
        });
    }

    private void subscribeObservers() {
        observeError();
        observeIsInserted();
        observeAddressBook();
        observeNetworkState();
    }

    private void observeAddressBook() {
        getViewModel().getContacts().observe(this, galContacts -> {
            Utils.setNoMessageBackground(
                    galContacts.isEmpty(),
                    binder.adRc, binder.adNoAccounts);
            setRecyclerView(galContacts);
        });
    }

    private void setRecyclerView(List<GalContact> list) {
        if (list.isEmpty()) {
            return;
        }
        adapter.setSource(list);

    }

    private void observeNetworkState() {
        getViewModel().getSyncResource().observe(this, authResource -> {
            if (authResource != null) {
                switch (authResource.status) {
                    case LOADING:
                        String data = authResource.data != null ? (String) authResource.data : "";
                        getNetworkFrameLayout().switchToLoading(data);
                        binder.adNoAccounts.setVisibility(View.GONE);
                        binder.adRc.setVisibility(View.GONE);
                        break;
                    case ERROR:
                        getNetworkFrameLayout().switchToNoNetwork();
                        break;
                    case SUCCESS:
                        getNetworkFrameLayout().switchToEmpty();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void observeError() {
        getViewModel().getErrorResult().observe(
                this, s ->
                        runOnUiThread(() ->
                                Utils.showSnackBarError(this, s)));
    }

    private void observeIsInserted() {
        getViewModel().getIsInserted().observe(this,
                contact -> {
                    if (contact != null) {
                        listener.onAddressBookItemClicked(
                                AddressBookActivity.this, contact);
                    }
                });
    }

    private void parseInputText() {
        String email = String.valueOf(binder.abName.getText());
        GalContact galContact = new GalContact();
        galContact.setDisplayName(Utils.extractUsername(email));
        galContact.setEmailAddress(email);
        getViewModel().insertContacts(galContact);
    }

    private CustomTextWatcher getTextWatcher() {
        return new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binder.abBtnAdd.setVisibility(Utils.isEmailValid(s.toString()) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                binder.abBtnAdd.setVisibility(Utils.isEmailValid(s.toString()) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binder.abBtnAdd.setVisibility(Utils.isEmailValid(s.toString()) ? View.VISIBLE : View.GONE);
            }
        };
    }
}
