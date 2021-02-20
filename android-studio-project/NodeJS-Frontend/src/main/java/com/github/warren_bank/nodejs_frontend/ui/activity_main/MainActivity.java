package com.github.warren_bank.nodejs_frontend.ui.activity_main;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.helpers.RuntimePermissionsMgr;
import com.github.warren_bank.nodejs_frontend.helpers.UI;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.AbstractTabFragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements RuntimePermissionsMgr.OnRuntimePermissionsListener {
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private ViewPagerAdapter viewPagerAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Display icon in the toolbar
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayUseLogoEnabled(true);
    getSupportActionBar().setLogo(R.mipmap.launcher);
    getSupportActionBar().setTitle(R.string.app_name);

    tabLayout = (TabLayout) findViewById(R.id.tabs);
    viewPager = (ViewPager) findViewById(R.id.viewPager);
    viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager());
    viewPager.setAdapter(viewPagerAdapter);
    tabLayout.setupWithViewPager(viewPager);

    checkRuntimePermissions();
  }

  private AbstractTabFragment getCurrentTabFragment() {
    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem());
    AbstractTabFragment tabFragment = (fragment == null) ? null : (AbstractTabFragment) fragment;
    return tabFragment;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    BackupRestoreMgr.onActivityResult(/* activity= */ MainActivity.this, requestCode, resultCode, data);

    AbstractTabFragment tabFragment = getCurrentTabFragment();
    if (tabFragment != null) {
      tabFragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    AbstractTabFragment tabFragment = getCurrentTabFragment();
    if (tabFragment != null) {
      tabFragment.onCreateContextMenu(menu, v, menuInfo);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem menuItem) {
    boolean done = false;

    if (!done) {
      AbstractTabFragment tabFragment = getCurrentTabFragment();
      if (tabFragment != null) {
        done = tabFragment.onContextItemSelected(menuItem);
      }
    }

    return done || super.onContextItemSelected(menuItem);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    boolean done = false;

    if (!done) {
      done = BackupRestoreMgr.onOptionsItemSelected(/* activity= */ MainActivity.this, menuItem.getItemId());
    }

    if (!done) {
      AbstractTabFragment tabFragment = getCurrentTabFragment();
      if (tabFragment != null) {
        done = tabFragment.onOptionsItemSelected(menuItem);
      }
    }

    return done || super.onOptionsItemSelected(menuItem);
  }

  // ---------------------------------------------------------------------------------------------
  // Runtime Permissions:
  // ---------------------------------------------------------------------------------------------

  private void checkRuntimePermissions() {
    if (RuntimePermissionsMgr.isEnabled(/* activity= */ MainActivity.this))
      onPermissionsGranted();
  }

  // ---------------------------------------------------------------------------------------------
  // dangerous:
  // ---------------------------------------------------------------------------------------------
  // READ_EXTERNAL_STORAGE
  //   * required by frontend to browse the filesystem to pick JS files
  //   * required by frontend to import data backups
  //   * required by node.js  to read JS files
  // WRITE_EXTERNAL_STORAGE
  //   * required by frontend to export data backups
  //   * required by node.js  to write to the filesystem
  // ---------------------------------------------------------------------------------------------
  // normal:
  // ---------------------------------------------------------------------------------------------
  // FOREGROUND_SERVICE
  //   * required by frontend to run node.js daemons
  // WAKE_LOCK
  //   * required by frontend to prevent resources from going into low power state while node.js daemons are running
  // INTERNET
  //   * required by node.js  to do networking; as either a client or server
  // ---------------------------------------------------------------------------------------------
  @Override
  public String[] getMandatoryPermissions() {
    return RuntimePermissionsMgr.getAllDangerousRequestedPermissions(/* context= */ MainActivity.this);
  }

  @Override
  public void onRequestCancelled() {
    // show the prompt again
    checkRuntimePermissions();
  }

  @Override
  public void onPermissionsGranted() {
    // OK
  }

  @Override
  public void onPermissionsDenied(String[] permissions) {
    // show a snackbar
    showPermissionsDeniedNotification(permissions);

    // show the prompt again
    checkRuntimePermissions();
  }

  @Override
  public void onPermissionsDeniedWithoutPrompt(String[] permissions) {
    // caveat emptor
  }

  private void showPermissionsDeniedNotification(String[] permissions) {
    String divider = "\n• ";

    String message;
    message = getString(R.string.warning_runtime_permissions_denied) + ":\n" + divider + TextUtils.join(divider, permissions);
    message = message.replace("android.permission.", "");

    UI.getSnackbar(tabLayout, message, /* maxLines= */ 6, Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    RuntimePermissionsMgr.onRequestPermissionsResult(/* activity= */ MainActivity.this, /* listener= */ MainActivity.this, requestCode, permissions, grantResults);
  }
}
