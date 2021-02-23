package com.github.warren_bank.nodejs_frontend.ui.activity_main;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.ExecTabFragment;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.ForkTabFragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
  private Context context;

  public ViewPagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    Fragment fragment = null;
    switch(position) {
      case 0:
        fragment = new ExecTabFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setUserVisibleHint(true);
        break;
      case 1:
        fragment = new ForkTabFragment();
        break;
    }
    return fragment;
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    String[] tab_titles = context.getResources().getStringArray(R.array.activity_main_tab_titles);
    return tab_titles[position];
  }
}
