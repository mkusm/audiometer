package com.skradacz.audiometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {

    public Button button, button2;
    public TextView textview2, textview3, textview4, textview5, textview6;

    public AudioManager audioManager;
    public ToneGen toneGen;
    private Handler handler = new Handler();
    StringBuilder stringBuilder = new StringBuilder();

    public double frequency = 250;                  // toneGen frequency
    public int duration = 6;                        // toneGen duration in seconds
    public double amplitude = 1.0f;                 // toneGen amplitude

    public int time = 0;                            // VolumeUp time counter
    public int delay = 1000 * 4;                    // 1000 = 1s; time it takes for VolumeUp to post

    public int mode = 0;
    public boolean stop = false;
    public String result;
    //public double freqChecker = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);
        textview6 = (TextView) findViewById(R.id.textView6);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        textview2.setText("toneGen.volume: " + String.valueOf(mode/10f));
        textview3.setText("Amplitude: " + String.valueOf(amplitude));
        textview4.setText("StreamVolume: " + String.valueOf(audioManager.getStreamVolume(
                AudioManager.STREAM_MUSIC)));
        textview5.setText(String.valueOf(mode-1));
        textview6.setText("frequency: " + String.valueOf(frequency));

        button.setText("CLICK WHEN YOU HEAR THE BEEP");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //if (freqChecker != frequency) {
                    //'if' makes sure frequency changed before button was clicked
                    stringBuilder.append("For freq " + frequency + " mode is " + mode + "\n"); //TODO
                    mode = 11;
                    if (frequency==8000) {
                        result = stringBuilder.toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("RESULT");
                        builder.setMessage(result);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    //freqChecker = frequency;
                //}

            }
        });

        button2.setText("CLICK TO STOP");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = true;
                toneGen.stop();
            }
        });

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);

        toneGen = new ToneGen(frequency, duration, amplitude);
        toneGen.play();

        VolumeUp();

    }

    private void VolumeUp(){
            // gets volumeChanger running every 'delay' seconds
            handler.postDelayed(volumeChanger, delay);
        }

    private Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            if (mode == 11) {
                mode = 0;
                if (frequency == 0){
                    frequency = 250;
                }else if (frequency == 250) {
                    frequency = 500;
                }else if (frequency == 500) {
                    frequency = 1000;
                }else if (frequency == 1000) {
                    frequency = 2000;
                }else if (frequency == 2000) {
                    frequency = 4000;
                }else if (frequency == 4000) {
                    frequency = 8000;
                }else if (frequency == 8000) {
                    //TODO: do intent
                    stop = true;
                }
            }

            if (!stop){

                if (mode == 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                    amplitude = 1.0;
                }else if (mode == 1) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                    amplitude = 1.1;
                }else if (mode == 2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                    amplitude = 1.2;
                }else if (mode > 2 && mode < 9) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                    amplitude = (Math.pow(2, mode-2));
                }else if (mode == 9){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, 0);
                    amplitude = 128;
                }else if (mode == 10){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
                    amplitude = 128;
                }
                toneGen.stop();
                toneGen = new ToneGen(frequency, duration, amplitude);
                toneGen.play();
                toneGen.volume(0.0f, mode/10f);
                mode++;

                time++;
                //textview2.setText("toneGen.volume: " + String.valueOf(mode/10f));
                //textview3.setText("Amplitude: " + String.valueOf(amplitude));
                //textview4.setText("StreamVolume: " + String.valueOf(audioManager.getStreamVolume(
                //        AudioManager.STREAM_MUSIC)));
                //textview5.setText(String.valueOf(mode-1));
                //textview6.setText("frequency: " + String.valueOf(frequency));

                VolumeUp();
            }

        }
    };

    @Override
    public void onPause(){
        super.onPause();
        toneGen.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

