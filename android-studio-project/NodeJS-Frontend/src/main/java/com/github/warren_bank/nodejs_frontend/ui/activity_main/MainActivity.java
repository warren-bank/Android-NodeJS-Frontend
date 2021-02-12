package com.github.warren_bank.nodejs_frontend.ui.activity_main;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.AbstractTabFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
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
  }

  private AbstractTabFragment getCurrentTabFragment() {
    Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem());
    AbstractTabFragment tabFragment = (fragment == null) ? null : (AbstractTabFragment) fragment;
    return tabFragment;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

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
    AbstractTabFragment tabFragment = getCurrentTabFragment();
    if (tabFragment != null) {
      done = tabFragment.onContextItemSelected(menuItem);
    }
    return done || super.onContextItemSelected(menuItem);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    boolean done = false;
    AbstractTabFragment tabFragment = getCurrentTabFragment();
    if (tabFragment != null) {
      done = tabFragment.onOptionsItemSelected(menuItem);
    }
    return done || super.onOptionsItemSelected(menuItem);
  }
}
