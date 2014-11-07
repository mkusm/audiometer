package com.skradacz.audiometer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

// todo NOT WORKING AT ALL AT THE MOMENT


public class SettingsActivity extends Activity {

    public CheckBox checkBox;
    public Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        saveButton = (Button) findViewById(R.id.saveButton);

        Global global = ((Global)getApplicationContext());
        checkBox.setChecked(global.getTestChecked());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global global = ((Global)getApplicationContext());
                global.setTestChecked(checkBox.isChecked());
            }
        });

    }

}
