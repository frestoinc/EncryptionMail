package com.frestoinc.maildemo.ui.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.data.enums.ClassificationEnum;
import com.frestoinc.maildemo.data.enums.CryptoEnum;
import com.frestoinc.maildemo.data.local.prefs.AppPreferenceHelper;
import com.frestoinc.maildemo.utility.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by frestoinc on 25,December,2019 for MailDemo.
 */
//todo ldap fragment for configuration
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

  private AppPreferenceHelper manager;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    if (getActivity() != null) {
      getActivity().setTheme(R.style.PreferenceStyle);
    }
    manager = new AppPreferenceHelper(getActivity());
    setPreferencesFromResource(R.xml.setting_preference, rootKey);
    setupView();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (preference instanceof ListPreference) {
      ListPreference listPreference = (ListPreference) preference;
      int index = listPreference.findIndexOfValue(sharedPreferences.getString(key, "0"));
      switch (key) {
        case Constants.ENCRYPT_TYPE:
          manager.setEncryption(
                  CryptoEnum.valueOf(listPreference.getEntries()[index].toString()));
          setupAlgoView();
          break;
        case Constants.CLASSIFICATION_TYPE:
          manager.setClassification(
                  ClassificationEnum.valueOf(listPreference.getEntries()[index].toString()));
          break;
        default:
          break;
      }
    } else if (preference instanceof SwitchPreferenceCompat) {
      SwitchPreferenceCompat compat = (SwitchPreferenceCompat) preference;
      compat.setChecked(((SwitchPreferenceCompat) preference).isChecked());
      manager.setisEcc(compat.isChecked());
    }
  }

  private void setupView() {
    setupEncryptionView();
    setupClassificationView();
    setupAlgoView();
  }

  private void setupAlgoView() {
    SwitchPreferenceCompat compat = findPreference(Constants.ALGO_TYPE);
    if (compat != null) {
      compat.setEnabled(manager.getEncryption() != CryptoEnum.Normal);
      if (compat.isEnabled()) {
        compat.setChecked(manager.getIsEcc());
      }
    }
  }

  private void setupEncryptionView() {
    if (manager.getEncryption() == null) {
      manager.setEncryption(CryptoEnum.Normal);
    }
    ListPreference listPreference = findPreference(Constants.ENCRYPT_TYPE);
    if (listPreference != null) {
      listPreference.setValue(manager.getEncryption().toString());
      if (getArguments() != null && getArguments().containsKey(Constants.GLOBAL_ENCRYPTION)) {
        CryptoEnum cenum =
                CryptoEnum.valueOf(getArguments().getString(Constants.GLOBAL_ENCRYPTION));
        listPreference.setValue(cenum.toString());
        manager.setEncryption(cenum);
        if (getActivity() != null) {
          setLimitationField(getActivity(), cenum, listPreference);
        }
      }
    }
  }

  private void setupClassificationView() {
    if (manager.getClassification() == null) {
      manager.setClassification(ClassificationEnum.Unclassified);
    }
    ListPreference listPreference = findPreference(Constants.CLASSIFICATION_TYPE);
    if (listPreference != null) {
      listPreference.setValue(manager.getClassification().toString());
      if (getArguments() != null && getArguments().containsKey(Constants.GLOBAL_CLASSIFICATION)) {
        ClassificationEnum cenum =
                ClassificationEnum.valueOf(getArguments().getString(Constants.GLOBAL_CLASSIFICATION));
        listPreference.setValue(cenum.toString());
        manager.setClassification(cenum);
        if (getActivity() != null) {
          setLimitationField(getActivity(), cenum, listPreference);
        }
      }
    }
  }

  private void setLimitationField(Activity activity, Object o, ListPreference preference) {
    List<String> list;
    if (o instanceof CryptoEnum) {
      list = Arrays.asList(activity.getResources().getStringArray(R.array.default_encryption));
    } else {
      list = Arrays.asList(activity.getResources().getStringArray(R.array.default_classification));
    }
    CharSequence[] entries = filterSequence(filteredList(o, list));
    preference.setEntries(entries);
    preference.setEntryValues(entries);
  }

  private List<String> filteredList(Object o, List<String> list) {
    int index = list.indexOf(o.toString());
    return list.subList(index, list.size());
  }

  private CharSequence[] filterSequence(List<String> list) {
    CharSequence[] charSequences = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {
      charSequences[i] = list.get(i);
    }
    return charSequences;
  }

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
  }
}
