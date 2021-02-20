package com.github.warren_bank.nodejs_frontend.ui.activity_main;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.BackupRestore;

import lib.folderpicker.support.FolderPicker;

import android.app.Activity;
import android.content.Intent;

public final class BackupRestoreMgr {

  public static boolean onOptionsItemSelected(Activity activity, int menuItemId) {
    switch(menuItemId) {
      case R.id.menu_export: {
        initiateDataExport(activity);
        return true;
      }
      case R.id.menu_import: {
        initiateDataImport(activity);
        return true;
      }
    }
    return false;
  }

  private static void initiateDataExport(Activity activity) {
    FolderPicker
      .withBuilder()
      .withActivity(activity)
      .withRequestCode(Constants.REQUEST_CODE_FILE_PICKER_JSON_EXPORT)
      .withTitle(activity.getString(R.string.app_folderpicker_json_export_filepath_title))
      .withDescription(activity.getString(R.string.app_folderpicker_json_export_filepath_description))
      .withNewFilePrompt(activity.getString(R.string.app_folderpicker_json_export_filepath_prompt_newfile))
      .withFileFilter("^.*\\.(?:json|txt)$")
      .withTheme(R.style.AppTheme_FolderPicker)
      .start();
  }

  private static void initiateDataImport(Activity activity) {
    FolderPicker
      .withBuilder()
      .withActivity(activity)
      .withRequestCode(Constants.REQUEST_CODE_FILE_PICKER_JSON_IMPORT)
      .withTitle(activity.getString(R.string.app_folderpicker_json_import_filepath_title))
      .withDescription(activity.getString(R.string.app_folderpicker_json_import_filepath_description))
      .withFilePicker(true)
      .withFileFilter("^.*\\.(?:json|txt)$")
      .withHomeButton(true)
      .withTheme(R.style.AppTheme_FolderPicker)
      .start();
  }

  public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
    String path;

    switch(requestCode) {
      case Constants.REQUEST_CODE_FILE_PICKER_JSON_EXPORT: {
        if (resultCode == Activity.RESULT_OK && intent.hasExtra(FolderPicker.EXTRA_DATA)) {
          path = intent.getExtras().getString(FolderPicker.EXTRA_DATA);

          if (path != null)
            BackupRestore.exportBackup(activity, path);
        }
        break;
      }
      case Constants.REQUEST_CODE_FILE_PICKER_JSON_IMPORT: {
        if (resultCode == Activity.RESULT_OK && intent.hasExtra(FolderPicker.EXTRA_DATA)) {
          path = intent.getExtras().getString(FolderPicker.EXTRA_DATA);

          if (path != null)
            if (BackupRestore.importBackup(activity, path))
              activity.recreate();
        }
        break;
      }
    }
  }

}
