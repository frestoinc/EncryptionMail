package com.frestoinc.maildemo.ui.compose;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.api.CustomTextWatcher;
import com.frestoinc.maildemo.api.SmartCardHelper;
import com.frestoinc.maildemo.api.listener.MainCardListener;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.base.BaseViewHolder;
import com.frestoinc.maildemo.data.enums.ComposeEnum;
import com.frestoinc.maildemo.data.enums.NetworkState;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;
import com.frestoinc.maildemo.databinding.ActivityComposeBinding;
import com.frestoinc.maildemo.databinding.BottomcardRecipientListBinding;
import com.frestoinc.maildemo.databinding.ViewholderEmailBinding;
import com.frestoinc.maildemo.ui.contact.AddressBookActivity;
import com.frestoinc.maildemo.ui.setting.SettingsActivity;
import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.mail.MessagingException;

import timber.log.Timber;

/**
 * Created by frestoinc on 24,December,2019 for MailDemo.
 */

public class ComposeActivity extends BaseActivity<ActivityComposeBinding, MessageViewModel>
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Inject
    public MainCardListener listener;
    @Inject
    public DividerItemDecoration decoration;
    private ActivityComposeBinding binder;
    private Snackbar dismissSnackbar;
    private List<String> toList = new ArrayList<>();
    private List<String> ccList = new ArrayList<>();
    private boolean isTo = false;
    private SmartCardHelper helper;

    @Override
    public int getBindingVariable() {
        return BR.msgViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_compose;
    }

    @Override
    public MessageViewModel getViewModel() {
        return new ViewModelProvider(this, getFactory()).get(MessageViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getViewModel().getGlobalComposeEnum() != null) {
            getViewModel().setComposeEnum(getViewModel().getGlobalComposeEnum());
        }

        if (getViewModel().getGlobalEasMessage() != null
                && getViewModel().getGlobalEasMessage().getServerId() != null) {
            getViewModel().getEasMessageFromDb(getViewModel().getGlobalEasMessage());
        } else {
            getViewModel().setNewMessage();
        }

        registerBinder();
        subscribeObservers();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADDRESS_BOOK_CODE && resultCode == RESULT_OK && data != null) {
            GalContact g =
                    getGson().fromJson(data.getStringExtra(Constants.ADDRESS_BOOK_LIST), GalContact.class);
            if (g != null) {
                parseContact(g);
            } else {
                getViewModel().setError(new Throwable("Could not retrieve contact information"));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getViewModel().setComposeEnum(ComposeEnum.valueOf(position + 1));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nth
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.composeExBtn:
                togglePreviousMessage();
                break;
            case R.id.composeRecipientCancel:
                isTo = true;
                editRecipientList();
                break;
            case R.id.composeCcCancel:
                isTo = false;
                editRecipientList();
                break;
            case R.id.composeRecipient:
                isTo = true;
                navigateToAddressBook();
                break;
            case R.id.composeCc:
                isTo = false;
                navigateToAddressBook();
                break;
            case R.id.composeFab:
                checkForNull();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            navigateToSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dismissOnTop()) {
            if (dismissSnackbar.isShown()) {
                super.onBackPressed();
            } else {
                dismissSnackbar.show();
            }
        } else if (getNetworkFrameLayout().getState() == NetworkState.ERROR) {
            getNetworkFrameLayout().switchToEmpty();
        } else {
            super.onBackPressed();
        }
    }

    private boolean dismissOnTop() {
        return dismissSnackbar.isShown()
                || getViewModel().getToList() != null
                || binder.composeForm.composeSubject.getText() != null
                || binder.composeForm.composeBody.getText() != null;
    }

    private void registerBinder() {
        binder = getViewDataBinding();
        setLoadingContainer(binder.loadingContainer);
    }

    private void initView() {
        setupToolbar();
        setupDismissSnackbar();
        setupListener();
    }

    private void subscribeObservers() {
        observeError();
        observeEasMessage();
        observeComposeEnum();
        observeToList();
        observeCcList();
        observeNetworkState();
    }

    private void observeEasMessage() {
        getViewModel().getEasMessage().observe(this,
                message -> {
                    setGeneralView(message);
                    ComposeEnum composeEnum = getViewModel().getComposeEnum().getValue();
                    if (composeEnum != null) {
                        switch (composeEnum) {
                            case REPLY:
                                getViewModel().parseSingleRecipientList(message);
                                break;
                            case REPLY_ALL:
                                getViewModel().parseMultipleRecipientList(message);
                                break;
                            case FORWARD:
                                getViewModel().parseForwardRecipientList();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void observeComposeEnum() {
        getViewModel().getComposeEnum().observe(this,
                composeEnum -> {
                    EasMessage message = getViewModel().getEasMessage().getValue();
                    if (message != null) {
                        switch (composeEnum) {
                            case REPLY:
                                getViewModel().parseSingleRecipientList(message);
                                break;
                            case REPLY_ALL:
                                getViewModel().parseMultipleRecipientList(message);
                                break;
                            case FORWARD:
                                getViewModel().parseForwardRecipientList();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void observeToList() {
        getViewModel().getToList().observe(this,
                strings -> {
                    setButtonCancelVisibility(binder.composeForm.composeRecipientCancel, strings);
                    toList = strings;
                    StringBuilder sb = toList.isEmpty() ? null : Utils.buildString(toList);
                    binder.composeForm.composeRecipient.setText(sb == null ? "" : sb.toString());
                });
    }

    private void observeCcList() {
        getViewModel().getCcList().observe(this,
                strings -> {
                    setButtonCancelVisibility(binder.composeForm.composeCcCancel, strings);
                    ccList = strings;
                    StringBuilder sb = ccList.isEmpty() ? null : Utils.buildString(ccList);
                    binder.composeForm.composeCc.setText(sb == null ? "" : sb.toString());
                });
    }

    private void observeError() {
        getViewModel().getErrorResult().observe(
                this, s ->
                        runOnUiThread(() ->
                                Utils.showSnackBarError(ComposeActivity.this, s)));
    }

    private void observeNetworkState() {
        getViewModel().getSyncResource().observe(this, authResource -> {
            if (authResource != null) {
                Timber.e("observeNetworkState authresource: %s", authResource.status);
                switch (authResource.status) {
                    case LOADING:
                        String data = authResource.data != null ? (String) authResource.data : "";
                        getNetworkFrameLayout().switchToLoading(data);
                        break;
                    case ERROR:
                        getNetworkFrameLayout().switchToNoNetwork();
                        break;
                    case SUCCESS:
                        getNetworkFrameLayout().switchToEmpty();
                        boolean b = authResource.data != null && (boolean) authResource.data;
                        parseSendStatus(b);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setupListener() {
        binder.composeForm.composeRecipient.setOnClickListener(this);
        binder.composeForm.composeCc.setOnClickListener(this);
        binder.composeForm.composeFab.setOnClickListener(this);
        binder.composeForm.composeRecipient.addTextChangedListener(getTextWatcher());
        binder.composeForm.composeSubject.addTextChangedListener(getTextWatcher());
    }

    private void setupDismissSnackbar() {
        dismissSnackbar =
                Snackbar.make(
                        findViewById(android.R.id.content), "Discard Changes?", Snackbar.LENGTH_LONG);
        TextView tv =
                dismissSnackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(2);
        tv.setTextColor(getColor(android.R.color.white));
        dismissSnackbar.setAction("Discard", v -> finish());
        dismissSnackbar.setActionTextColor(getColor(R.color.red));
    }

    private void setupToolbar() {
        setSupportActionBar(binder.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            ComposeEnum composeEnum = getViewModel().getComposeEnum().getValue();
            if (composeEnum != null && composeEnum != ComposeEnum.NEW) {
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(this,
                                R.layout.spinner_item, getResources().getStringArray(R.array.reply_entries));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binder.toolbar.toolbarSp.setAdapter(adapter);
                binder.toolbar.toolbarSp.setSelection(ComposeEnum.getStatusCode(composeEnum) - 1);

                binder.toolbar.toolbarSp.setOnItemSelectedListener(this);
            } else {
                binder.toolbar.toolbarSp.setVisibility(View.GONE);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getViewModel().setComposeEnum(ComposeEnum.NEW);
                getSupportActionBar().setTitle("NEW");
            }
        }
    }

    private void parseContact(GalContact g) {
        if (toList.contains(g.getEmailAddress()) || ccList.contains(g.getEmailAddress())) {
            return;
        }
        if (isTo) {
            toList.add(g.getEmailAddress());
            getViewModel().updateToList(toList);
        } else {
            ccList.add(g.getEmailAddress());
            getViewModel().updateCcList(ccList);
        }
    }

    private void setButtonCancelVisibility(View v, List<String> strings) {
        v.setVisibility(strings.isEmpty() ? View.GONE : View.VISIBLE);
        v.setOnClickListener(strings.isEmpty() ? null : this);
    }

    private void editRecipientList() {
        BottomcardRecipientListBinding bind =
                DataBindingUtil.inflate(
                        getLayoutInflater(),
                        R.layout.bottomcard_recipient_list, null, false);

        final BottomSheetDialog bd = Utils.getBottomSheetDialog(this);
        bd.setContentView(bind.getRoot());
        bind.bcRecipientListRc.setLayoutManager(new LinearLayoutManager(this));
        bind.bcRecipientListRc.addItemDecoration(decoration);
        RecipientListAdapter adapter = new RecipientListAdapter(isTo);
        bind.bcRecipientListRc.setAdapter(adapter);
        adapter.setSource(isTo ? toList : ccList);
        bd.show();
    }

    private void setGeneralView(EasMessage e) {
        binder.composeForm.composeSubject.setText(e.getSubject());
        binder.composeForm.composeExBtn.setVisibility(
                getViewModel().getComposeEnum().getValue() == ComposeEnum.NEW ? View.GONE : View.VISIBLE);
        binder.composeForm.composeExBtn.setOnClickListener(
                getViewModel().getComposeEnum().getValue() == ComposeEnum.NEW ? null : this);
        binder.composeForm.composeSender.setText(getViewModel().getAccountEmail());
        binder.composeForm.composeExBody.setText(e.getMessage() != null ? Html.fromHtml(
                Jsoup.clean(new String(e.getMessage(), StandardCharsets.UTF_8), Whitelist.relaxed()),
                Html.FROM_HTML_MODE_LEGACY) : "");
        binder.composeForm.composeBody.requestFocus();
    }

    private void togglePreviousMessage() {
        if (binder.composeForm.composeEx.isExpanded()) {
            binder.composeForm.composeEx.collapse(true);
        } else {
            binder.composeForm.composeEx.expand(true);
        }
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        if (getViewModel().getGlobalComposeEnum() != ComposeEnum.NEW) {
            intent.putExtra(Constants.GLOBAL_ENCRYPTION, "Sign");
            intent.putExtra(Constants.GLOBAL_CLASSIFICATION,
                    getViewModel().getEasMessage().getValue().getClassification());
        }
        startActivity(intent);
    }

    private void navigateToAddressBook() {
        Intent intent = new Intent(this, AddressBookActivity.class);
        startActivityForResult(intent, Constants.ADDRESS_BOOK_CODE);
    }

    private void checkForNull() {
        boolean cancel = false;
        View focusView = null;

        List<String> tolist = getViewModel().getToList().getValue();
        if (tolist == null || tolist.isEmpty()) {
            binder.composeForm.composeRecipientSubj.setError(getString(R.string.no_recipients_found));
            focusView = binder.composeForm.composeRecipientSubj;
            cancel = true;
        }

        if (!Utils.isStringValid(String.valueOf(binder.composeForm.composeSubject.getText()))) {
            binder.composeForm.composeSubjectSubj.setError(getString(R.string.error_field_required));
            focusView = binder.composeForm.composeSubjectSubj;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showSendDialog();
        }
    }

    private void showSendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("Sending with following attributes:")
                .setMessage(getViewModel().buildMessageAttribute())
                .setNegativeButton(getString(R.string.cancel),
                        (dialog, which) -> dialog.dismiss())
                .setNeutralButton(getString(R.string.edit),
                        (dialog, which) -> navigateToSettings())
                .setPositiveButton(getString(R.string.proceed),
                        (dialog, which) -> {
                            dialog.dismiss();
                            sendMail();
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendMail() {
        try {
            getViewModel().sendMail(
                    String.valueOf(binder.composeForm.composeSubject.getText()),
                    String.valueOf(binder.composeForm.composeBody.getText()));
            observeSmartCard();
        } catch (MessagingException e) {
            e.printStackTrace();
            getViewModel().setError(e);
        }
    }

    private void observeSmartCard() {
        Timber.e("observeSmartCard");
        getViewModel().getShouldReadSmartCard().observe(this,
                bool -> {
                    if (Boolean.TRUE.equals(bool)) {
                        getViewModel().getSyncResource().removeObservers(this);
                        try {
                            helper = new SmartCardHelper(ComposeActivity.this, listener);
                            getViewModel().getSyncResource().observe(this, authResource -> {
                                if (authResource != null) {
                                    switch (authResource.status) {
                                        case LOADING:
                                            if (authResource.data != null) {
                                                helper.updateDialog((String) authResource.data);
                                            }
                                            break;
                                        case SUCCESS:
                                            helper.unregisterCardProvider();
                                            if (authResource.data != null) {
                                                parseSendStatus((boolean) authResource.data);
                                            }
                                            break;
                                        case ERROR:
                                        default:
                                            helper.unregisterCardProvider();
                                            break;
                                    }
                                }
                            });
                        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                                | InvocationTargetException e) {
                            helper.unregisterCardProvider();
                            helper = null;
                            getViewModel().setError(e);
                            e.printStackTrace();
                        }
                        getViewModel().getShouldReadSmartCard().removeObservers(this);
                    }
                });
    }

    private void parseSendStatus(boolean b) {
        Utils.showSnackBar(ComposeActivity.this,
                Boolean.TRUE.equals(b) ? "Message Sent" : "Message Not Send");
        if (Boolean.TRUE.equals(b)) {
            new Handler().postDelayed(this::finish, 1500);
        }
    }

    private CustomTextWatcher getTextWatcher() {
        return new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binder.composeForm.composeRecipientSubj.setError(null);
                binder.composeForm.composeSubjectSubj.setError(null);
            }
        };
    }

    private class RecipientListAdapter extends RecyclerView.Adapter<RecipientListAdapter.RecipientListHolder> {

        private boolean isToList;

        private List<String> source;

        RecipientListAdapter(boolean b) {
            this.source = new ArrayList<>();
            this.isToList = b;
        }

        @NonNull
        @Override
        public RecipientListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecipientListHolder(
                    ViewholderEmailBinding.inflate(LayoutInflater.from(parent.getContext()),
                            parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecipientListHolder holder, int position) {
            holder.bind(position);
        }

        protected void setSource(List<String> source) {
            this.source = source;
            notifyDataSetChanged();
            if (isToList) {
                getViewModel().updateToList(this.source);
            } else {
                getViewModel().updateCcList(this.source);
            }

        }

        @Override
        public int getItemCount() {
            return source.size();
        }

        protected class RecipientListHolder extends BaseViewHolder implements View.OnClickListener {

            private ViewholderEmailBinding binder;

            RecipientListHolder(ViewholderEmailBinding binding) {
                super(binding.getRoot());
                this.binder = binding;
                this.binder.cardsEmailBtn.setOnClickListener(this);
            }

            @Override
            public void bind(int position) {
                this.binder.cardsEmailTxt.setText(source.get(position));
            }

            @Override
            public void onClick(View v) {
                source.remove(getAdapterPosition());
                setSource(source);
            }
        }
    }
}
