package com.frestoinc.maildemo.ui.setting;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.databinding.ActivitySettingBinding;
import com.frestoinc.maildemo.utility.Constants;

/**
 * Created by frestoinc on 02,January,2020 for MailDemo.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        setSupportActionBar(binder.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_settings));
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        }

        SettingsFragment fragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        if (getIntent().getStringExtra(Constants.GLOBAL_CLASSIFICATION) != null) {
            String classification = getIntent().getStringExtra(Constants.GLOBAL_CLASSIFICATION);
            bundle.putString(Constants.GLOBAL_CLASSIFICATION, classification);
        }

        if (getIntent().getStringExtra(Constants.GLOBAL_ENCRYPTION) != null) {
            String encryption = getIntent().getStringExtra(Constants.GLOBAL_ENCRYPTION);
            bundle.putString(Constants.GLOBAL_ENCRYPTION, encryption);
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingFrame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
