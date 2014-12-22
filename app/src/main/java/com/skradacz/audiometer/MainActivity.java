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

        TextView textView1Start = (TextView) findViewById(R.id.start_text_view);
        TextView textView2Graph = (TextView) findViewById(R.id.graph_text_view);
        TextView textView3HowTo = (TextView) findViewById(R.id.how_to_text_view);
        TextView textView4Settings = (TextView) findViewById(R.id.settings_text_view);
        TextView textView5About = (TextView) findViewById(R.id.about_text_view);

        textView1Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExaminationActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView2Graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView3HowTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HowToActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView4Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView5About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.about_alert_title));
                builder.setMessage(getString(R.string.about_alert_message) + " " + getString(R.string.version_number));
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

}

