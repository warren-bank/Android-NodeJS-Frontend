package com.github.warren_bank.nodejs_frontend.ui.activity_read_output;

import com.github.warren_bank.nodejs_frontend.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StandardOutputActivity extends AppCompatActivity {
  private BufferedReader input;
  private boolean        isInitialized;
  private boolean        isRunning;

  private File           file;
  private TextView       text;
  private Handler        handler;
  private Runnable       runner;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_read_output);

    Intent intent = getIntent();
    String title = intent.getStringExtra("title");
    file = (File) intent.getSerializableExtra("file");

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Display icon in the toolbar
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setDisplayUseLogoEnabled(true);
    getSupportActionBar().setLogo(R.mipmap.launcher);
    getSupportActionBar().setTitle(title);

    text = (TextView) findViewById(R.id.stdout);

    handler = new Handler();
    runner  = new Runnable() {
      public void run() {
        String currentLine = null;

        try {
          while (isRunning && ((currentLine = input.readLine()) != null)) {
            text.append(currentLine + "\n");
          }
        }
        catch(Exception e){}

        if (isRunning)
          handler.postDelayed(this, 250);
      }
    };
  }

  @Override
  public void onStart() {
    if (file.exists()) {
      try {
        input = new BufferedReader(new FileReader(file));
        isInitialized = true;
      }
      catch(Exception e) {
        text.setText(R.string.error_stdout_not_readable);
        isInitialized = false;
      }
    }
    else {
      text.setText(R.string.error_stdout_not_found);
      isInitialized = false;
    }
  }

  @Override
  public void onResume() {
    if (!isInitialized) return;

    isRunning = true;
    handler.post(runner);
  }

  @Override
  public void onPause() {
    if (!isInitialized) return;

    isRunning = false;
  }

  @Override
  public void onStop() {
    if (!isInitialized) return;

    try {
      input.close();
    }
    catch(Exception e) {}

    input = null;
    text.setText("");
  }
}
