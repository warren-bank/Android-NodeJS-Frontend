package lib.folderpicker.support;

// based on sample code:
//   https://android.googlesource.com/platform/development/+/refs/heads/marshmallow-release/samples/Support7Demos/src/com/example/android/supportv7/app/AppCompatPreferenceActivity.java

// another helpful example:
//   https://github.com/SKaDiZZ/MytecApp/blob/master/Preferences.java

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FolderPicker extends lib.folderpicker.FolderPicker {

    private AppCompatDelegate mDelegate;

    public static FolderPickerBuilder withBuilder() {
        return new FolderPickerBuilder();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    @Override
    protected void handle_no_intent_EXTRA_THEME() {
        setTheme(R.style.FolderPickerTheme_Activity);
    }

    @Override
    protected void initContentView() {
        setContentView(R.layout.fp_support_main_layout);
    }

    @Override
    protected void handle_intent_EXTRA_TITLE(String title) {
        super.handle_intent_EXTRA_TITLE(title);
        //getSupportActionBar().setTitle(title);
    }

    @Override
    protected void handle_intent_EXTRA_DESCRIPTION(String desc) {
        super.handle_intent_EXTRA_DESCRIPTION(desc);
        //getSupportActionBar().setSubtitle(desc);
    }

    @Override
    protected void handle_intent_EXTRA_EMPTY_FOLDER() {
        super.handle_intent_EXTRA_EMPTY_FOLDER();
    }

    @Override
    protected void listClick(int position) {
        super.listClick(position);
    }

    @Override
    protected void initListView() {
        try {
            FolderAdapter folderAdapter = new FolderAdapter(FolderPicker.this, mFolderAndFileList);
            RecyclerView listView = (RecyclerView) findViewById(R.id.fp_listView);
            listView.setLayoutManager(new LinearLayoutManager(FolderPicker.this));
            listView.setAdapter(folderAdapter);

            mListView = (View) listView;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showList() {
        RecyclerView listView = (RecyclerView) mListView;
        FolderAdapter folderAdapter = (FolderAdapter) listView.getAdapter();
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    public void setTheme(int themeResId) {
        super.setTheme(themeResId);
        getDelegate().setTheme(themeResId);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public void setTitle(CharSequence title) {
        getDelegate().setTitle(title);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(FolderPicker.this, FolderPicker.this, null);
        }
        return mDelegate;
    }
}
