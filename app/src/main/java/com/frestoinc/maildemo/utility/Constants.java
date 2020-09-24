package com.frestoinc.maildemo.utility;

import com.frestoinc.maildemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frestoinc on 26,December,2019 for MailDemo.
 */
public class Constants {

    public static final int ADDRESS_BOOK_CODE = 0x01;

    //sharedpreferences
    public static final String CLASSIFICATION_TYPE = "classificationType";
    public static final String ENCRYPT_TYPE = "encryptionType";
    public static final String ALGO_TYPE = "algoType";

    public static final String EAS_MESSAGE_VIEWER = "EAS_MESSAGE_VIEWER";
    public static final String EAS_FOLDER_TYPE = "EAS_FOLDER_TYPE";
    public static final String ADDRESS_BOOK_LIST = "ADDRESS_BOOK_LIST";

    public static final String ROOM_DATABASE = "maildemo.db";

    //eas message
    public static final String GLOBAL_CLASSIFICATION = "GLOBAL_CLASSIFICATION";
    public static final String GLOBAL_ENCRYPTION = "GLOBAL_ENCRYPTION";

    //card
    public static final String CARD_PROVIDER = "Card Provider 2019";
    public static final String CARD_PROVIDER_NAME = "Card";
    public static final double CARD_VERISON = 1.3d;

    public static final String X_CLASSIFICATION = "x-classification";
    public static final String SECURAGE_CLASSIFICATION = "X-SecureAge-Classification";
    public static final String MESSAGE_CLASSIFICATION = "message classification";

    //drawer menu
    public static final List<String> FILTERED_FOLDERS = new ArrayList<>(
            Arrays.asList(
                    "Archive",
                    "Outbox",
                    "Sent Items",
                    "Deleted Items",
                    "Drafts",
                    "Inbox"
            )
    );

    public static final List<Integer> FILTERED_FOLDERS_DRAWABLE = new ArrayList<>(
            Arrays.asList(
                    R.drawable.ic_archive_24dp,
                    R.drawable.ic_outbox_24dp,
                    R.drawable.ic_send_24dp,
                    R.drawable.ic_delete_24dp,
                    R.drawable.ic_draft_24dp,
                    R.drawable.ic_inbox_24dp
            )
    );
}
