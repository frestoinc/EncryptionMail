/*
 *
 *
 *  * Copyright (C) 2006 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.frestoinc.maildemo.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by frestoinc on 04,October,2019 for FullMailDemo.
 */
public final class Utils {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static StringBuilder buildString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s).append(";\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb;
    }

    public static BottomSheetDialog getBottomSheetDialog(Context context) {
        final BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(context, R.style.DialogStyle);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (bottomSheetDialog.getWindow() != null) {
            bottomSheetDialog.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return bottomSheetDialog;
    }


    public static Address[] getAddresses(List<String> mlist) throws AddressException {
        if (mlist == null) {
            return new Address[]{};
        }
        Set<String> set = new HashSet<>(mlist);
        List<Address> list = new ArrayList<>();
        for (String s : set) {
            if (isEmailValid(s)) {
                list.add(new InternetAddress(s));
            }
        }
        return list.toArray(new Address[0]);
    }

    public static List<String> extractMailAddresses(String addr) {
        Matcher m = Pattern.compile("([^<]+@[^>]+)").matcher(addr);
        HashSet<String> addrList = new HashSet<>();
        while (m.find()) {
            addrList.add(m.group().toLowerCase(Locale.getDefault()));
        }

        return new ArrayList<>(addrList);
    }

    public static String extractUsername(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    public static String buildLdapQuery(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(|");
        for (String s : list) {
            sb.append(String.format("(mail=%s)", s));
        }
        sb.append(")");
        return sb.toString();
    }

    public static String buildMailAddresses(String addr1, String addr2) {
        List<String> toList = extractMailAddresses(addr1);
        List<String> ccList = new ArrayList<>();
        if (addr2 != null) {
            ccList = extractMailAddresses(addr2);
        }
        List<String> compiledList = getCompiledList(toList, ccList);
        StringBuilder sb = new StringBuilder();
        for (String s : compiledList) {
            sb.append(s).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static List<String> getCompiledList(List<String> listA, List<String> listB) {
        return Stream.of(listA, listB)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static IntentFilter getNetworkFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return intentFilter;
    }

    public static long getLongDate(String date) {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date newDate;
        try {
            newDate = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = null;
        }
        if (newDate == null) {
            return 0;
        }
        return newDate.getTime();
    }

    public static String convertDateToTimespan(String dateStr) {
        Instant.parse(dateStr);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date newDate;
        try {
            newDate = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = null;
        }

        if (newDate == null) {
            return "";
        }
        return DateUtils.getRelativeTimeSpanString(newDate.getTime()).toString();
    }

    public static String convertDateToMailDate(String dateStr) {
        Instant.parse(dateStr);
        final TimeZone timeZone = TimeZone.getTimeZone("Asia/Singapore");

        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date newDate;
        try {
            newDate = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = null;
        }

        if (newDate == null) {
            return "";
        }

        DateFormat out = new SimpleDateFormat("EEEE',' MMMM dd',' yyyy hh:mm a", Locale.getDefault());
        out.setTimeZone(timeZone);
        return out.format(newDate);
    }

    public static String parseUsername(String s) {
        if (!isStringValid(s)) {
            return "";
        }
        String[] pname = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s1 : pname) {
            String cha = s1.substring(0, s1.length() > 1 ? 2 : 1);
            sb.append(cha);
        }

        if (sb.length() == 2) {
            return sb.toString();
        }
        return sb.toString().substring(0, 2);
    }

    public static String parseUsername(EasMessage e) {
        String username = isStringValid(e.getFrom())
                ? e.getFrom() : "Unknown";

        List<String> set = Utils.extractMailAddresses(username);
        Iterator it = set.iterator();
        if (it.hasNext()) {
            username = (String) it.next();
        }
        return username;
    }

    public static void parseMessage(byte[] b, TextView v) {
        if (b == null) {
            return;
        }
        Document doc = Jsoup.parseBodyFragment(new String(b, StandardCharsets.UTF_8));
        v.setText(doc.body().text());
    }

    public static void setNoMessageBackground(Boolean b, View layout, TextView textView) {
        layout.setVisibility(Boolean.TRUE.equals(b) ? View.GONE : View.VISIBLE);
        textView.setVisibility(Boolean.TRUE.equals(b) ? View.VISIBLE : View.GONE);
    }

    public static void toggleViewState(Activity activity, boolean b, View v1, View v2) {
        activity.runOnUiThread(() -> {
            v1.setVisibility(b ? View.GONE : View.VISIBLE);
            v2.setVisibility(b ? View.VISIBLE : View.GONE);
        });
    }

    public static String createBodyText(String classification, String bodyText) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("<html>");
        sb.append(appendClassificationText(classification));
        sb.append("<body><p>");
        sb.append(bodyText);
        sb.append("<p><u><i>");
        sb.append("\n\nSent From Android Test App");
        sb.append("</i></u>");
        sb.append("</body></html>");
        return sb.toString();
    }

    public static String createReplyBodyText(String classification, String bodyText,
                                             EasMessage message) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(createBodyText(classification, bodyText));
        sb.append(appendReplyBodyText(message));
        return sb.toString();
    }

    private static SpannableStringBuilder appendReplyBodyText(EasMessage e) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String sentDate = convertDateToMailDate(e.getDateReceived());
        String body = new String(e.getMessage(), StandardCharsets.UTF_8);
        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < body.length(); i++) {
            sb.append(body.charAt(i));
        }

        return ssb
                .append("<hr>")
                .append("<b>----- Original Message ----- </b>" + "<br>" + "<b>From: </b>")
                .append(e.getFrom())
                .append("<br>")
                .append("<b>Sent: </b>")
                .append(sentDate)
                .append("<br>")
                .append("<b>To: </b>")
                .append(e.getDisplayTo())
                .append("<br>")
                .append("<b>Subject: </b>")
                .append(e.getSubject())
                .append("<br>")
                .append(sb.toString())
                .append("<p>")
                .append("</html>");
    }

    private static String appendClassificationText(String classification) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append("<header><p><font color=black size =3> <b><i>Message Classification: </font><font color=blue size =4> ")
                .append(classification).append("</i></b></font></p></header>");
        return sb.toString();
    }

    public static int getClassification(String s) {
        switch (s.toLowerCase(Locale.getDefault())) {
            case "restricted":
                return 1;
            case "confidential":
                return 2;
            case "secret":
                return 3;
            default:
                return 0;
        }
    }

    private static String getClassification(Context ctx, int i) {
        switch (i) {
            case 0:
                return ctx.getResources().getString(R.string.unclassified);
            case 2:
                return ctx.getResources().getString(R.string.confidential);
            case 3:
                return ctx.getResources().getString(R.string.secret);
            default:
                return ctx.getResources().getString(R.string.restricted);
        }
    }

    public static String getClassification(int i) {
        switch (i) {
            case 0:
                return "Unclassified";
            case 2:
                return "Confidential";
            case 3:
                return "Secret";
            default:
                return "Restricted";
        }
    }

    public static String getEncryption(int i) {
        switch (i) {
            case 1:
                return "RSA";
            case 2:
                return "ECC";
            default:
                return "Unencrypted";
        }
    }

    public static Drawable getDrawable(Context ctx) {
        Drawable d = ContextCompat.getDrawable(ctx, R.drawable.circle);
        if (d == null) {
            return null;
        }

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        d.setTint(color);
        return d;
    }

    public static void showProgressBar(
            Activity a, final boolean show, final ProgressBar bar, final LinearLayout layout) {
        int shortAnimTime =
                a.getApplicationContext()
                        .getResources()
                        .getInteger(android.R.integer.config_shortAnimTime);

        layout.setVisibility(show ? View.GONE : View.VISIBLE);
        layout.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                layout.setVisibility(show ? View.GONE : View.VISIBLE);
                            }
                        });

        bar.setVisibility(show ? View.VISIBLE : View.GONE);
        bar.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                bar.setVisibility(show ? View.VISIBLE : View.GONE);
                            }
                        });
        if (show) {
            a.getWindow()
                    .setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public static boolean isEmailValid(String email) {
        return email != null && EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isStringValid(String string) {
        return string != null && !TextUtils.isEmpty(string);
    }

    public static boolean isPasswordNotValid(String password) {
        return password.trim().isEmpty();
    }

    public static boolean isStringInteger(String number) {
        try {
            Integer.parseInt(number);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void chooseCertificate(Activity context, int i) {
        String[] mimeTypes = {
                "application/x-x509-user-cert",
                "application/x-x509-ca-cert",
                "application/x-pkcs12",
                "application/pkix-cert"
        };
        context.startActivityForResult(
                new Intent(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("*/*")
                        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes), i);
    }

    /**
     * @param activity Callable Activity
     * @param s        Message Output
     */
    public static void showSnackBar(Activity activity, String s) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), s, Snackbar.LENGTH_LONG);
        TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(4);
        tv.setTextColor(activity.getColor(android.R.color.white));
        snackbar.getView().setBackgroundColor(activity.getColor(android.R.color.black));
        snackbar.show();
    }

    public static void showSnackBarError(Activity activity, String s) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), String.format("Error: %s", s), Snackbar.LENGTH_LONG);
        TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(4);
        tv.setTextColor(activity.getColor(R.color.red));
        snackbar.getView().setBackgroundColor(activity.getColor(android.R.color.black));
        snackbar.setAction(activity.getString(R.string.dismiss), v -> snackbar.dismiss());
        snackbar.setActionTextColor(activity.getColor(R.color.red));
        snackbar.show();
    }

    public static String generateRandomDigits() {
        int m = (int) Math.pow(10, 9);
        return String.valueOf(m + new Random().nextInt(9 * m));
    }
}
