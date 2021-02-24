package com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.data_model.NodeJsApp;
import com.github.warren_bank.nodejs_frontend.data_model.Preferences;
import com.github.warren_bank.nodejs_frontend.helpers.NodeJsAppRunner;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.Constants;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list.ItemMoveCallback;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list.RecyclerViewAdapter;
import com.github.warren_bank.nodejs_frontend.ui.activity_main.tabs.app_list.RecyclerViewHolder;
import com.github.warren_bank.nodejs_frontend.ui.activity_read_output.StandardOutputActivity;

import lib.folderpicker.support.FolderPicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class AbstractTabFragment extends Fragment implements ItemMoveCallback.OnDragListener, RecyclerViewAdapter.ColorPalette {

  private boolean isDaemon;
  private Dialog  dialog;

  private ArrayList<NodeJsApp> nodeJsApps;
  private ArrayList<NodeJsApp> listItems;
  private RecyclerView         listView;
  private RecyclerViewAdapter  listAdapter;
  private ItemTouchHelper      listItemTouchHelper;

  // ---------------------------------------------------------------------------------------------
  // Constructor:
  // ---------------------------------------------------------------------------------------------

  public AbstractTabFragment(boolean isDaemon) {
    this.isDaemon = isDaemon;
  }

  // ---------------------------------------------------------------------------------------------
  // Data Mutation:
  // ---------------------------------------------------------------------------------------------

  private void handleDataSetChange(String mode) {
    handleDataSetChange(mode, -1);
  }

  private void handleDataSetChange(String mode, int items_index) {
    if (mode != null) {
      switch(mode) {
        case "add":
          items_index = listItems.size() - 1;
          if (items_index >= 0)
            listAdapter.notifyItemInserted(items_index);
          break;
        case "update":
          if (items_index >= 0)
            listAdapter.notifyItemChanged(items_index);
          break;
        case "remove":
          if (items_index >= 0)
            listAdapter.notifyItemRemoved(items_index);
          break;
      }
    }

    Preferences.setNodeJsApps(getContext(), isDaemon, nodeJsApps);
  }

  private void addListItem(NodeJsApp listItem) {
    listItems.add(listItem);
    handleDataSetChange("add");
  }

  private void updateListItem(int items_index) {
    handleDataSetChange("update", items_index);
  }

  private void removeListItem(int items_index) {
    if (isDaemon) {
      final NodeJsApp listItem = listItems.get(items_index);
      listItem.remove();

      int apps_index = nodeJsApps.indexOf(listItem);
      if (apps_index >= 0) {
        nodeJsApps.remove(apps_index);
        nodeJsApps.add(listItem);
      }
    }

    listItems.remove(items_index);
    handleDataSetChange("remove", items_index);
  }

  private NodeJsApp getUnusedListItem() {
    int index = listItems.size();

    final NodeJsApp listItem = (index == nodeJsApps.size())
      ? null
      : nodeJsApps.get(index);

    return listItem;
  }

  // ---------------------------------------------------------------------------------------------
  // Lifecycle Events:
  // ---------------------------------------------------------------------------------------------

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    return inflater.inflate(R.layout.fragment_list, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    nodeJsApps  = Preferences.getNodeJsApps(getContext(), isDaemon);
    listItems   = isDaemon
                    ? NodeJsApp.filterActive(nodeJsApps)
                    : nodeJsApps;
    listView    = (RecyclerView) view.findViewById(R.id.app_list);
    listAdapter = new RecyclerViewAdapter(listItems, this, this, this);

    ItemTouchHelper.Callback callback = new ItemMoveCallback(listAdapter);
    listItemTouchHelper = new ItemTouchHelper(callback);
    listItemTouchHelper.attachToRecyclerView(listView);

    listView.setLayoutManager(new LinearLayoutManager(getContext()));
    listView.setAdapter(listAdapter);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    String path;

    switch(requestCode) {
      case Constants.REQUEST_CODE_FOLDER_PICKER_CWD:
        if (resultCode == Activity.RESULT_OK && intent.hasExtra(FolderPicker.EXTRA_DATA)) {
          path = intent.getExtras().getString(FolderPicker.EXTRA_DATA);

          if ((path != null) && (dialog != null) && (dialog.isShowing())) {
            final TextView inputCwdDirpath = (TextView) dialog.findViewById(R.id.input_cwd_dirpath);
            inputCwdDirpath.setText(path);
          }
        }
        break;

      case Constants.REQUEST_CODE_FILE_PICKER_JS:
        if (resultCode == Activity.RESULT_OK && intent.hasExtra(FolderPicker.EXTRA_DATA)) {
          path = intent.getExtras().getString(FolderPicker.EXTRA_DATA);

          if ((path != null) && (dialog != null) && (dialog.isShowing())) {
            final TextView inputJsFilepath = (TextView) dialog.findViewById(R.id.input_js_filepath);
            inputJsFilepath.setText(path);
          }
        }
        break;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Drag/Drop:
  // ---------------------------------------------------------------------------------------------

  @Override
  public void onDragBegin(RecyclerViewHolder holder) {
    listItemTouchHelper.startDrag(holder);
  }

  @Override
  public void onDragComplete(int fromPosition, int toPosition) {
    if (isDaemon)
      NodeJsApp.movePosition(nodeJsApps, fromPosition, toPosition);

    handleDataSetChange(null);
  }

  // ---------------------------------------------------------------------------------------------
  // Row Colors:
  // ---------------------------------------------------------------------------------------------

  @Override
  public int getColorOnRowSelected(NodeJsApp listItem) {
    return getResources().getColor(R.color.fragmentListItemOnRowSelected);
  }

  @Override
  public int getColorOnRowClear(NodeJsApp listItem) {
    return getResources().getColor(R.color.fragmentListItemOnRowClear);
  }

  protected void updateRowBackgroundColor(NodeJsApp listItem, int color) {
    int position = listItems.indexOf(listItem);
    if (position < 0) return;

    RecyclerViewHolder holder = (RecyclerViewHolder) listView.findViewHolderForAdapterPosition(position);
    if (holder == null) return;

    listAdapter.updateRowBackgroundColor(holder, color);
  }

  protected void updateRowBackgroundColorSelected(NodeJsApp listItem) {
    int color = getColorOnRowSelected(listItem);
    updateRowBackgroundColor(listItem, color);
  }

  protected void updateRowBackgroundColorClear(NodeJsApp listItem) {
    int color = getColorOnRowClear(listItem);
    updateRowBackgroundColor(listItem, color);
  }

  // ---------------------------------------------------------------------------------------------
  // Menu:
  // ---------------------------------------------------------------------------------------------

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch(menuItem.getItemId()) {
      case R.id.menu_add: {
        showEditDialog(-1);
        return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------------------------------------------------
  // Context Menu:
  // ---------------------------------------------------------------------------------------------

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    if (v.getId() == R.id.app_title) {
      try {
        Object tag = v.getTag();
        int position = ((Integer) tag).intValue();
        final NodeJsApp listItem = listItems.get(position);

        if (isDaemon) {
          menu.setHeaderTitle( listItem.toString() );
          menu.add(R.id.app_title, position, 1, R.string.app_context_menuitem_label_edit_settings);
          menu.add(R.id.app_title, position, 2, R.string.app_context_menuitem_label_start);
          menu.add(R.id.app_title, position, 3, R.string.app_context_menuitem_label_stop);
          menu.add(R.id.app_title, position, 4, R.string.app_context_menuitem_label_read_stdout);
          menu.add(R.id.app_title, position, 5, R.string.app_context_menuitem_label_delete);
        }
        else {
          menu.setHeaderTitle( listItem.toString() );
          menu.add(R.id.app_title, position, 1, R.string.app_context_menuitem_label_edit_settings);
          menu.add(R.id.app_title, position, 2, R.string.app_context_menuitem_label_run);
        //menu.add(R.id.app_title, position, 3, R.string.app_context_menuitem_label_read_stdout);
          menu.add(R.id.app_title, position, 4, R.string.app_context_menuitem_label_delete);
        }
      }
      catch(Exception e) {}
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem menuItem) {
    int groupId = menuItem.getGroupId();
    int itemId  = menuItem.getItemId();
    int order   = menuItem.getOrder();

    if (groupId == R.id.app_title) {
      final NodeJsApp listItem = listItems.get(itemId);

      if (isDaemon) {
        switch(order) {
          case 1:
            // edit settings
            showEditDialog(itemId);
            break;
          case 2:
            // start service
            startService(listItem);
            break;
          case 3:
            // stop service
            stopService(listItem);
            break;
          case 4:
            // read stdout
            readStandardOutput(listItem);
            break;
          case 5:
            // delete from list
            removeListItem(itemId);
            break;
        }
      }
      else {
        switch(order) {
          case 1:
            // edit settings
            showEditDialog(itemId);
            break;
          case 2:
            // run
            runApplication(listItem);
            break;
          case 3:
            // read stdout
            readStandardOutput(listItem);
            break;
          case 4:
            // delete from list
            removeListItem(itemId);
            break;
        }
      }

      return true;
    }
    else {
      return false;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Abstract Methods:
  // ---------------------------------------------------------------------------------------------

  protected abstract void startService   (NodeJsApp listItem);
  protected abstract void stopService    (NodeJsApp listItem);
  protected abstract void runApplication (NodeJsApp listItem);

  // ---------------------------------------------------------------------------------------------
  // Common Methods:
  // ---------------------------------------------------------------------------------------------

  protected void readStandardOutput(NodeJsApp listItem) {
    String title = listItem.toString();
    String id    = listItem.getId();

    readStandardOutput(title, id);
  }

  protected void readStandardOutput(String title, String id) {
    File file = NodeJsAppRunner.getStandardOutputFile(getContext(), id, true);

    if (file == null) {
      Snackbar.make(getView(), R.string.error_stdout_not_found, Snackbar.LENGTH_SHORT).show();
      return;
    }

    Intent intent = new Intent(getContext(), StandardOutputActivity.class);
    intent.putExtra("title", title);
    intent.putExtra("file",  (Serializable) file);
    startActivity(intent);
  }

  // ---------------------------------------------------------------------------------------------
  // Add/Edit Dialog:
  // ---------------------------------------------------------------------------------------------

  protected String getDirectoryPathFromFile(String value) {
    if (value == null) return null;

    File file = new File(value);
    if (!file.exists()) return null;

    try {
      return file.isDirectory()
        ? file.getCanonicalPath()
        : file.getParent();
    }
    catch(Exception e) {
      return null;
    }
  }

  private void showEditDialog(int position) {
    final boolean isAdd = (position < 0);

    final NodeJsApp listItem;
    final String[]  values;

    if (!isAdd) {
      listItem = listItems.get(position);
      values   = listItem.getStringArray();
    }
    else {
      listItem = isDaemon
                   ? getUnusedListItem()
                   : new NodeJsApp(null);
      values   = null;

      if (listItem == null) {
        Snackbar.make(getView(), R.string.error_using_max_apps, Snackbar.LENGTH_SHORT).show();
        return;
      }
    }

    dialog = new Dialog(getContext(), R.style.AppTheme);
    dialog.setContentView(R.layout.dialog_app_settings);

    final EditText inputCwdDirpath  = (EditText) dialog.findViewById(R.id.input_cwd_dirpath);
    final EditText inputTitle       = (EditText) dialog.findViewById(R.id.input_title);
    final EditText inputEnvVars     = (EditText) dialog.findViewById(R.id.input_env_vars);
    final EditText inputNodeOptions = (EditText) dialog.findViewById(R.id.input_node_options);
    final EditText inputJsFilepath  = (EditText) dialog.findViewById(R.id.input_js_filepath);
    final EditText inputJsOptions   = (EditText) dialog.findViewById(R.id.input_js_options);
    final Button buttonBrowseCwd    = (Button) dialog.findViewById(R.id.button_browse_cwd_dirpath);
    final Button buttonBrowseJs     = (Button) dialog.findViewById(R.id.button_browse_js_filepath);
    final Button buttonCancel       = (Button) dialog.findViewById(R.id.button_cancel);
    final Button buttonSave         = (Button) dialog.findViewById(R.id.button_save);

    if (!isAdd) {
      if (values[0] != null) inputTitle.setText(values[0]);
      if (values[1] != null) inputCwdDirpath.setText(values[1]);
      if (values[2] != null) inputEnvVars.setText(values[2]);
      if (values[3] != null) inputNodeOptions.setText(values[3]);
      if (values[4] != null) inputJsFilepath.setText(values[4]);
      if (values[5] != null) inputJsOptions.setText(values[5]);

      buttonSave.setText(R.string.label_button_update);
    }

    buttonBrowseCwd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String currentPath = null;
        if (!isAdd) {
          if (currentPath == null) currentPath = getDirectoryPathFromFile(values[1]); // default  to cwd_dirpath (if defined)
          if (currentPath == null) currentPath = getDirectoryPathFromFile(values[4]); // fallback to js_filepath (if defined)
        }

        FolderPicker
          .withBuilder()
          .withActivity(getActivity())
          .withRequestCode(Constants.REQUEST_CODE_FOLDER_PICKER_CWD)
          .withTitle(getResources().getString(R.string.app_folderpicker_cwd_dirpath_title))
          .withDescription(getResources().getString(R.string.app_folderpicker_cwd_dirpath_description))
          .withPath(currentPath)
          .withTheme(R.style.AppTheme_FolderPicker)
          .start();
      }
    });

    buttonBrowseJs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String currentPath = null;
        if (!isAdd) {
          if (currentPath == null) currentPath = getDirectoryPathFromFile(values[4]); // default  to js_filepath (if defined)
          if (currentPath == null) currentPath = getDirectoryPathFromFile(values[1]); // fallback to cwd_dirpath (if defined)
        }

        FolderPicker
          .withBuilder()
          .withActivity(getActivity())
          .withRequestCode(Constants.REQUEST_CODE_FILE_PICKER_JS)
          .withFilePicker(true)
          .withFileFilter("^.*\\.(?:js)$")
          .withTitle(getResources().getString(R.string.app_folderpicker_js_filepath_title))
          .withDescription(getResources().getString(R.string.app_folderpicker_js_filepath_description))
          .withHomeButton(true)
          .withPath(currentPath)
          .withTheme(R.style.AppTheme_FolderPicker)
          .start();
      }
    });

    buttonCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        dialog = null;
      }
    });

    buttonSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String title             = inputTitle.getText().toString().trim();
        final String cwd_dirpath       = inputCwdDirpath.getText().toString().trim();
        final String env_vars_text     = inputEnvVars.getText().toString().trim();
        final String node_options_text = inputNodeOptions.getText().toString().trim();
        final String js_filepath       = inputJsFilepath.getText().toString().trim();
        final String js_options_text   = inputJsOptions.getText().toString().trim();

        if (
             title.isEmpty()
          || ( isDaemon && js_filepath.isEmpty())
          || (!isDaemon && js_filepath.isEmpty() && node_options_text.isEmpty())
        ) {
          Snackbar.make(dialog.getWindow().findViewById(android.R.id.content), R.string.error_missing_required_value, Snackbar.LENGTH_SHORT).show();
          return;
        }

        if (
             !isAdd
          && (values[0] == title)
          && (values[1] == cwd_dirpath)
          && (values[2] == env_vars_text)
          && (values[3] == node_options_text)
          && (values[4] == js_filepath)
          && (values[5] == js_options_text)
        ){
          // no change
          dialog.dismiss();
          dialog = null;
          return;
        }

        listItem.add(title, cwd_dirpath, env_vars_text, node_options_text, js_filepath, js_options_text);

        if (isAdd)
          addListItem(listItem);
        else
          updateListItem(position);

        dialog.dismiss();
        dialog = null;
      }
    });

    dialog.show();
  }

}
