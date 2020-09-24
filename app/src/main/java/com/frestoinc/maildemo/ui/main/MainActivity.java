package com.frestoinc.maildemo.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.frestoinc.maildemo.BR;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.activesync.EasFolderType;
import com.frestoinc.maildemo.base.BaseActivity;
import com.frestoinc.maildemo.data.enums.NetworkState;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.databinding.ActivityMainBinding;
import com.frestoinc.maildemo.ui.account.AccountActivity;
import com.frestoinc.maildemo.ui.compose.ComposeActivity;
import com.frestoinc.maildemo.ui.main.container.FolderFragment;
import com.frestoinc.maildemo.ui.setting.SettingsActivity;
import com.frestoinc.maildemo.ui.sharedviewmodel.MainViewModel;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;

import java.util.List;


/**
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

  private ActivityMainBinding binder;

  private EasFolderType type = null;

  @Override
  public int getBindingVariable() {
    return BR.mainViewModel;
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override
  public MainViewModel getViewModel() {
    return new ViewModelProvider(this, getFactory()).get(MainViewModel.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    registerBinder();
    initView();
    subscribeObservers();
    if (savedInstanceState == null) {
      Fragment fragment = new FolderFragment();
      fragment.setArguments(generateBundle(EasFolderType.DefaultInbox));
      type = EasFolderType.DefaultInbox;
      replaceFragment(fragment);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.full_sync:
        getViewModel().synchroniseData();
        return true;
      case R.id.ldap:
        //todo
        return true;
      case R.id.settings:
        navigateTo(SettingsActivity.class);
        return true;
      case R.id.sign_out:
        getViewModel().signOut();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  //todo review
  @Override
  public void onBackPressed() {
    if (getNetworkFrameLayout().getState() == NetworkState.ERROR) {
      getNetworkFrameLayout().switchToEmpty();
    } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
      getSupportFragmentManager().popBackStack();
    } else {
      finish();
    }
  }

  private void registerBinder() {
    binder = getViewDataBinding();
    setLoadingContainer(binder.loadingContainer);
  }

  private void subscribeObservers() {
    observeError();
    observeSession();
    observeFolders();
  }

  private void observeSession() {
    getViewModel().getSessionAccountUser()
            .observe(this, accountUser -> {
              if (accountUser == null) {
                navigateToAccount();
              } else {
                if (getSupportActionBar() != null) {
                  getSupportActionBar().setSubtitle(accountUser.getAccountemail());
                }
              }
            });
  }

  private void observeError() {
    getViewModel().getErrorResult().observe(
            this, s ->
                    runOnUiThread(() ->
                            Utils.showSnackBarError(MainActivity.this, s)));
  }

  private void observeFolders() {
    getViewModel().getAppFolders().observe(this, this::setupCustomMenu);
  }

  private void initView() {
    setSupportActionBar(binder.toolbar.toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("Mail Demo");
      getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
    binder.drawerLayout.addDrawerListener(setupDrawerToggle());
    setupDrawerToggle().syncState();
    binder.mainFab.setOnClickListener(
            v -> navigateToComposeActivity());
  }

  private ActionBarDrawerToggle setupDrawerToggle() {
    return new ActionBarDrawerToggle(
            this, binder.drawerLayout, binder.toolbar.toolbar,
            R.string.drawer_open, R.string.drawer_close);
  }

  private void setupCustomMenu(List<EasFolder> easFolders) {
    binder.nvView.getMenu().clear();
    for (int i = 0; i < easFolders.size(); i++) {
      binder.nvView.getMenu().add(
              Menu.NONE, i, i,
              easFolders.get(i).getName());
    }
    setIconMenu();
    binder.nvView.getMenu().setGroupCheckable(Menu.NONE, true, true);
    binder.nvView.setNavigationItemSelectedListener(menuItem -> {
      int order = menuItem.getOrder();
      binder.nvView.setCheckedItem(order);
      binder.drawerLayout.closeDrawer(GravityCompat.START);
      selectDrawerItem(easFolders.get(order));
      return true;
    });
  }

  private void setIconMenu() {
    for (int i = 0; i < binder.nvView.getMenu().size(); i++) {
      String title = binder.nvView.getMenu().getItem(i).getTitle().toString();
      if (Constants.FILTERED_FOLDERS.indexOf(title) != -1) {
        binder.nvView.getMenu().getItem(i).setIcon(Constants.FILTERED_FOLDERS_DRAWABLE.get(
                Constants.FILTERED_FOLDERS.indexOf(title)));
      } else {
        binder.nvView.getMenu().getItem(i).setIcon(R.drawable.ic_label_black_24dp);
      }
    }
  }

  private void selectDrawerItem(EasFolder folder) {
    if (type.equals(folder.getType())) {
      return;
    }
    type = folder.getType();
    Fragment fragment = FolderFragment.newInstance();
    fragment.setArguments(generateBundle(folder.getType()));
    replaceFragment(fragment);
  }


  private void replaceFragment(Fragment fragment) {
    getSupportFragmentManager()
            .beginTransaction()
            .replace(binder.mainFrame.getId(), fragment)
            .addToBackStack(null)
            .commit();
  }

  private Bundle generateBundle(EasFolderType type) {
    Bundle b = new Bundle();
    b.putString(Constants.EAS_FOLDER_TYPE, getGson().toJson(type));
    return b;
  }

  private void navigateToAccount() {
    Intent intent = new Intent(this, AccountActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();
  }

  private void navigateToComposeActivity() {
    getViewModel().setGlobalMessage();
    navigateTo(ComposeActivity.class);
  }
}
