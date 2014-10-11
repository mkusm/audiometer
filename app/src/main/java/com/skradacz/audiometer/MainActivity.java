package com.skradacz.audiometer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {

    public Button button, button2;
    public TextView textview2, textview3, textview4, textview5;

    public AudioManager audioManager;
    public ToneGen toneGen;
    private Handler handler = new Handler();

    public double frequency = 1000;                 // toneGen frequency
    public int duration = 15;                       // toneGen duration in seconds
    public double amplitude = 1.1f;                 // toneGen amplitude

    public int time = 0;                            // VolumeUp time counter
    public int delay = 1000 * 1;                    // 1000 = 1s; time it takes for VolumeUp to post

    public int klikles = 0;

    public int mode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: button doing stuff
                klikles++;
                button.setText("KLIKNIJ MJE PO RAZ " + klikles);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeUp();
            }
        });

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);


        toneGen = new ToneGen(frequency, duration, amplitude);
        toneGen.play();

    }

    private void VolumeUp(){
            handler.postDelayed(volumeChanger, delay);
        }

    private Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            if (mode == 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                amplitude = 1.0;
            }else if (mode == 1) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                amplitude = 1.1;
            }else if (mode > 1 && mode < 9) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
                amplitude = (Math.pow(2, mode-1));
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
            textview2.setText("toneGen.volume: " + String.valueOf(mode/10f));
            textview3.setText("Amplitude: " + String.valueOf(amplitude));
            textview4.setText("StreamVolume: " + String.valueOf(audioManager.getStreamVolume(
                    AudioManager.STREAM_MUSIC)));
            textview5.setText(String.valueOf(time));

            VolumeUp();
        }
    };

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

