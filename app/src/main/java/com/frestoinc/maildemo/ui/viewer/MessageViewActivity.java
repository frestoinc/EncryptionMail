package com.frestoinc.maildemo.ui.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.api.SmartCardHelper;
import com.frestoinc.maildemo.api.crypto.CryptoAttributes;
import com.frestoinc.maildemo.api.listener.MainCardListener;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.data.enums.ComposeEnum;
import com.frestoinc.maildemo.data.enums.NetworkState;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.databinding.ActivityViewerBinding;
import com.frestoinc.maildemo.ui.compose.ComposeActivity;
import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

/**
 * Created by frestoinc on 20,December,2019 for MailDemo.
 */
public class MessageViewActivity extends BaseActivity<ActivityViewerBinding, MessageViewModel>
        implements View.OnClickListener {

    @Inject
    public MainCardListener listener;
    private SmartCardHelper helper;
    private ActivityViewerBinding binder;

    @Override
    public int getBindingVariable() {
        return BR.msgViewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_viewer;
    }

    @Override
    public MessageViewModel getViewModel() {
        return new ViewModelProvider(this, getFactory()).get(MessageViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinder();
        initView();
        subscribeObservers();
        parseIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.resync) {
            getViewModel().getEasMessageFromAs(getViewModel().getEasMessage().getValue());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getNetworkFrameLayout().getState() == NetworkState.ERROR) {
            getNetworkFrameLayout().switchToEmpty();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resync, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vRecipient:
                toggleRecipients();
                break;
            case R.id.vAttach:
                getViewModel().getAttachmentFromAS();
                observeSmartCard();
                break;
            default:
                break;
        }
    }

    private void registerBinder() {
        binder = getViewDataBinding();
        setLoadingContainer(binder.loadingContainer);
    }

    private void subscribeObservers() {
        observeError();
        observeNetworkState();
        observeEasMessage();
    }

    private void initView() {
        setSupportActionBar(binder.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binder.vAttach.setOnClickListener(this);
        binder.vBottomBar.setOnNavigationItemSelectedListener(
                menuItem -> {
                    selectBottomItem(menuItem);
                    return true;
                });
    }

    private void selectBottomItem(MenuItem item) {
        ComposeEnum composeEnum;
        switch (item.getItemId()) {
            case R.id.reply:
                composeEnum = ComposeEnum.REPLY;
                break;
            case R.id.reply_all:
                composeEnum = ComposeEnum.REPLY_ALL;
                break;
            case R.id.forward:
                composeEnum = ComposeEnum.FORWARD;
                break;
            default:
                composeEnum = ComposeEnum.NEW;
                break;
        }
        getViewModel().setComposeEnum(composeEnum);
        startActivity(new Intent(this, ComposeActivity.class));
    }

    private void observeError() {
        getViewModel().getErrorResult().observe(
                this, s ->
                        runOnUiThread(() ->
                                Utils.showSnackBarError(MessageViewActivity.this, s)));
    }

    private void observeEasMessage() {
        getViewModel().getEasMessage().observe(this,
                this::setMessageView);
    }

    private void observeNetworkState() {
        getViewModel().getSyncResource().observe(this, authResource -> {
            if (authResource != null) {
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
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void observeSmartCard() {
        getViewModel().getShouldReadSmartCard().observe(this,
                bool -> {
                    if (Boolean.TRUE.equals(bool)) {
                        getViewModel().getSyncResource().removeObservers(this);
                        try {
                            helper = new SmartCardHelper(MessageViewActivity.this, listener);
                            helper.setTerminalFactory();
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
                                            Utils.showSnackBar(this, "Decrypted..");
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
                    }
                });
    }

    private void parseIntent() {
        if (getIntent() != null && getIntent().getStringExtra(Constants.EAS_MESSAGE_VIEWER) != null) {
            EasMessage msg =
                    getGson().fromJson(
                            getIntent().getStringExtra(Constants.EAS_MESSAGE_VIEWER), EasMessage.class);
            if (!msg.isSync()) {
                getViewModel().getEasMessageFromAs(msg);
            } else {
                getViewModel().getEasMessageFromDb(msg);
            }
        }
    }

    private void setMessageView(EasMessage e) {
        final String username = Utils.parseUsername(e);
        final String recipients = Utils.buildMailAddresses(e.getTo(), e.getCc());
        binder.vSender.setText(username);
        binder.vExpandable.vExSenderCont.setText(username);
        binder.vRecipient.setText(getString(R.string.me));
        binder.vExpandable.vExRecipientCont.setText(recipients);
        binder.vSubject.setText(e.getSubject());
        /*GlideImageGetter imageGetter = new GlideImageGetter(this, binder.vMsg);
        String text = new String(e.getMessage(), StandardCharsets.UTF_8);
        Spanned spanned = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, null);

        binder.vMsg.setText(spanned);*/
        binder.vMsg.setText(Html.fromHtml(
                Jsoup.clean(new String(e.getMessage(), StandardCharsets.UTF_8), Whitelist.relaxed()),
                Html.FROM_HTML_MODE_LEGACY));
        binder.vImage.setImageDrawable(Utils.getDrawable(this));
        binder.vImgText.setText(Utils.parseUsername(username));
        binder.vTime.setText(Utils.convertDateToTimespan(e.getDateReceived()));
        binder.vExpandable.vExTimeCont.setText(Utils.convertDateToMailDate(e.getDateReceived()));
        binder.vExpandable.vExCryptoCont.setText(getString(R.string.unencrypted));
        binder.vAttach.setVisibility(e.hasAttachment() ? View.VISIBLE : View.GONE);
        if (binder.vAttach.getVisibility() == View.VISIBLE) {
            binder.vAttach.setText(e.getAttachmentName());
            if (e.getAttachmentName().contains(".p7*")) {
                binder.vExpandable.vExCryptoCont.setText(getString(R.string.encrypted));
            }
        }
        binder.vExpandable.vExClassCont.setText(
                Utils.isStringValid(e.getClassification())
                        ? e.getClassification() : getString(R.string.unclassified));
        binder.vRecipient.setOnClickListener(this);
        if (e.isDecrypted()) {
            binder.vAttach.setVisibility(View.GONE);
            binder.vExpandable.vExAttr.setVisibility(View.VISIBLE);
            binder.vExpandable.vExAttr.setText(getCryptoParams());
        }

        getViewModel().setGlobalMessage(e);

    }

    private String getCryptoParams() {
        CryptoAttributes attributes = getViewModel().getAttributes().getValue();
        if (attributes == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Attributes:\n");
        if (attributes.isEncrypt()) {
            sb.append(String.format("-Encryption Algo: %s", !TextUtils.isEmpty(attributes.geteAlgorithm())
                    ? attributes.geteAlgorithm() : "Unable to parse Encryption algorithm ID"));
            sb.append("\n");
        }

        if (attributes.isCompress()) {
            sb.append("-Compressed \n");
        }

        if (attributes.isSign()) {
            sb.append(String.format("-Signing Algo: %s",
                    !TextUtils.isEmpty(attributes.getsAlgorithm())
                            ? attributes.getsAlgorithm() : "Unable to parse Signing algorithm ID"));
            sb.append("\n");

            sb.append(String.format("-Signer: %s", !TextUtils.isEmpty(attributes.getSigner())
                    ? attributes.getSigner() : "Unable to parse Signer Information"));
            sb.append("\n");
        }

        sb.append(String.format("-Verified: %s", attributes.isVerified() ? "True" : "False"));

        return sb.toString();
    }

    private void toggleRecipients() {
        if (binder.vExpandable.vExpandable.isExpanded()) {
            binder.vExpandable.vExpandable.collapse(true);
            binder.vRecipient.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_more_24dp, 0);
        } else {
            binder.vExpandable.vExpandable.expand(true);
            binder.vRecipient.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_expand_less_24dp, 0);
        }
    }
}
