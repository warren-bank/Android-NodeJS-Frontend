package com.github.warren_bank.nodejs_frontend.ui.activity_read_output;

import com.github.warren_bank.nodejs_frontend.R;
import com.github.warren_bank.nodejs_frontend.helpers.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StandardOutputActivity extends AppCompatActivity {
  private BufferedReader input;
  private boolean        isInitialized;
  private boolean        isRunning;
  private boolean        isHighlighted;

  private File           stdout_file;
  private TextView       stdout_textView;
  private ViewGroup      stdout_container;
  private ViewGroup      search_container;
  private EditText       search_textField;
  private Button         search_button;
  private int            search_position;
  private Handler        handler;
  private Runnable       runner;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read_output);

    Intent intent = getIntent();
    String title = intent.getStringExtra("title");
    stdout_file = (File) intent.getSerializableExtra("file");

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Display icon in the toolbar
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayUseLogoEnabled(true);
    getSupportActionBar().setLogo(R.mipmap.launcher);
    getSupportActionBar().setTitle(title);

    stdout_textView  = (TextView)  findViewById(R.id.stdout);
    stdout_container = (ViewGroup) findViewById(R.id.stdout_container);
    search_container = (ViewGroup) findViewById(R.id.search_container);
    search_textField = (EditText)  findViewById(R.id.search_text);
    search_button    = (Button)    findViewById(R.id.search_button);

    handler = new Handler();
    runner  = new Runnable() {
      public void run() {
        String currentLine = null;

        try {
          while (isRunning && ((currentLine = input.readLine()) != null)) {
            stdout_textView.append(currentLine + "\n");
          }
        }
        catch(Exception e){}

        if (isRunning)
          handler.postDelayed(this, 250);
      }
    };

    initializeSearchBar();
  }

  @Override
  public void onStart() {
    super.onStart();

    if (stdout_file.exists()) {
      try {
        input = new BufferedReader(new FileReader(stdout_file));
        isInitialized = true;
      }
      catch(Exception e) {
        stdout_textView.setText(R.string.error_stdout_not_readable);
        isInitialized = false;
      }
    }
    else {
      stdout_textView.setText(R.string.error_stdout_not_found);
      isInitialized = false;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!isInitialized) return;

    isRunning = true;
    handler.post(runner);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (!isInitialized) return;

    isRunning = false;
  }

  @Override
  public void onStop() {
    super.onStop();
    if (!isInitialized) return;

    try {
      input.close();
    }
    catch(Exception e) {}

    input = null;
    stdout_textView.setText("");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_read_output, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch(menuItem.getItemId()) {
      case R.id.menu_toggle_search: {
        toggleSearchBar();
        return true;
      }
    }
    return false;
  }

  private void initializeSearchBar() {
    isHighlighted = false;

    search_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // hide soft keyboard
        UI.hideKeyboard(StandardOutputActivity.this);

        String filter = search_textField.getText().toString();
        String stdout = stdout_textView.getText().toString();

        if (filter.isEmpty() || !stdout.contains(filter)) {
          Snackbar.make(stdout_container, R.string.notification_no_search_results, Snackbar.LENGTH_SHORT).show();
          return;
        }

        if (!isHighlighted) {
          String highlight = getHighlight(filter);
          stdout = stdout.replace(filter, highlight);
          stdout_textView.setText(Html.fromHtml(stdout));

          isHighlighted   = true;
          search_position = 0;
        }

        // advance to position of next match
        int next_match_index = stdout.indexOf(filter, search_position);
        if (next_match_index >= 0) {
          search_position = next_match_index + 1;

          int next_match_line = stdout_textView.getLayout().getLineForOffset(next_match_index);
          stdout_container.scrollTo(0, stdout_textView.getLayout().getLineTop(next_match_line));
        }
      }
    });

    search_textField.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isHighlighted) {
          String stdout = stdout_textView.getText().toString();
          stdout = stdout.replace(getHighlightPre(),  "");
          stdout = stdout.replace(getHighlightPost(), "");
          stdout_textView.setText(stdout);

          isHighlighted = false;
        }
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });
  }

  private String getHighlightPre() {
    String color = getString(R.string.color_search_results);
    String text = "<font color='" + color + "'>";
    return text;
  }

  private String getHighlightPost() {
    String text = "</font>";
    return text;
  }

  private String getHighlight(String text) {
    String highlight = getHighlightPre() + text + getHighlightPost();
    return highlight;
  }

  private void toggleSearchBar() {
    toggleView(search_container);
  }

  private void toggleView(View view) {
    if (view.getVisibility() == View.GONE)
      view.setVisibility(View.VISIBLE);
    else if (view.getVisibility() == View.VISIBLE)
      view.setVisibility(View.GONE);
  }
}
