package com.frestoinc.maildemo.api;

import android.nfc.NfcAdapter;
import android.os.CountDownTimer;

import com.frestoinc.maildemo.api.crypto.PinInputHandler;
import com.frestoinc.maildemo.api.listener.MainCardListener;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;
import com.frestoinc.maildemo.utility.Constants;

import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.EnumSet;
import java.util.List;

import timber.log.Timber;

/**
 * Created by frestoinc on 17,January,2020 for MailDemo.
 */
public class SmartCardHelper implements ICardTerminalListener {

    private Object terminalFactory; //todo

    private Object cardProvider = null; ///todo

    private BaseActivity activity;

    private MainCardListener listener;

    private CountDownTimer countDownTimer;

    /**
     * Instantiates a new Smart card helper.
     *
     * @param baseActivity the base activity
     * @param listener     the listener
     */
    public SmartCardHelper(BaseActivity baseActivity, MainCardListener listener) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.listener = listener;
        this.activity = baseActivity;
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(activity);
        if (!adapter.isEnabled()) {
            listener.onError((MessageViewModel) activity.getViewModel(), new Exception("NFC not enabled!"));
        } else {
            this.terminalFactory = getTerminalFactory();
        }
        if (terminalFactory == null) {
            Timber.e("cardInserted terminal null");
        }
    }

    @Override
    public void cardInserted(Object terminal, Object callbackParameter) {
        Timber.e("cardInserted terminal");
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            if (terminal.getType() == TerminalType.NFC && terminal.isCardPresent()) {
                init(terminal);
                listener.onCardDetected((MessageViewModel) activity.getViewModel(), getCardProvider());
            }
        } catch (CardException e) {
            e.printStackTrace();
            unregisterCardProvider();
            listener.onError((MessageViewModel) activity.getViewModel(), e);
        }

    }

    @Override
    public void cardRemoved(Object terminal, Object callbackParameter) {
        Timber.e("cardRemoved terminal");
    }

    @Override
    public void cardTerminalsAdded(List<Object> list, Object callbackParameter) {
        Timber.e("cardTerminalAdded list");
    }

    @Override
    public void cardTerminalAdded(Object terminal, Object callbackParameter) {
        Timber.e("cardTerminalAdded terminal");
        if (terminal.getType() == TerminalType.NFC) {
            countDownTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long m) {
                    updateDialog(String.format("Please tap your card now...%ss", m / 1000));
                }

                @Override
                public void onFinish() {
                    listener.onError((MessageViewModel) activity.getViewModel(),
                            new Throwable("Operation cancelled after 10s inactivity"));
                }
            }.start();
        }
    }

    @Override
    public void cardTerminalRemoved(Object terminal, Object callbackParameter) {
        //nth
    }

    private void init(Object reader) {
        SmartCardSecurityParameters scParams = new SmartCardSecurityParameters();
        scParams.setReadersList(reader);

        cardProvider = new Object(
                Constants.CARD_PROVIDER_NAME,
                Constants.CARD_VERISON,
                Constants.CARD_PROVIDER,
                scParams);
        cardProvider.setCallbackHandler(new PinInputHandler(activity.getContext()));
        cardProvider.put("Property.lock type", "PAIRING CODE");
        Security.addProvider(cardProvider);
    }

    /**
     * Unregister card provider and remove from security provider.
     */
    public void unregisterCardProvider() {
        if (getCardProvider() != null) {
            Security.removeProvider(Constants.CARD_PROVIDER_NAME);
            Security.removeProvider("BC");
        }
        terminalFactory = null;
        cardProvider = null;
        activity.getNetworkFrameLayout().switchToEmpty();
    }

    /**
     * Update UI dialog.
     *
     * @param msg the msg
     */
    public synchronized void updateDialog(String msg) {
        activity.getNetworkFrameLayout().switchToLoading(msg);
    }

    private Object getTerminalFactory()
            throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {
        if (terminalFactory == null) {
            terminalFactory = Object.getFactory(activity, this, EnumSet.of(Object.NFC));
        }
        return terminalFactory;
    }

    /**
     * Sets terminal factory to listen to NFC.
     *
     * @throws NoSuchMethodException     the no such method exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    public void setTerminalFactory()
            throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        terminalFactory = getTerminalFactory();
        Timber.e("terminal factory set");
    }

    private Object getCardProvider() {
        return cardProvider;
    }
}
