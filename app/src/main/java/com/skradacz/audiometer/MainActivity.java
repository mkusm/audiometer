package com.skradacz.audiometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView startTextView = (TextView) findViewById(R.id.start_text_view);
        TextView graphTextView = (TextView) findViewById(R.id.graph_text_view);
        TextView howToTextView = (TextView) findViewById(R.id.how_to_text_view);
        TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
        TextView aboutTextView = (TextView) findViewById(R.id.about_text_view);

        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ExaminationActivity.class);
            }
        });
        graphTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GraphActivity.class);
            }
        });
        howToTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(HowToActivity.class);
            }
        });
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SettingsActivity.class);
            }
        });
        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutAlertDialog();
            }
        });

    }

    private void showAboutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder
            .setTitle(getString(R.string.about_alert_title))
            .setMessage(String.format("%s %s", getString(R.string.about_alert_message), getString(R.string.version_number)))
            .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        MainActivity.this.startActivity(intent);
    }

}

