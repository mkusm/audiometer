package com.skradacz.audiometr;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {

    public Button button;
    public TextView textview2, textview3, textview4, textview5;

    public MediaPlayer mediaPlayer;
    public AudioManager audioManager;

    public int klikles = 0;
    public int volumeChange;
    public int volume;

    public int czas = 0;
    public int delay = 1000 * 5;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.laik);
        mediaPlayer.start();
        //mediaPlayer.setVolume(leftVolume, rightVolume);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);

        textview2.setText(String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
        textview3.setText(""+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeChange = 0;
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: button doing stuff
                //leftVolume -= volumeChange;
                //rightVolume -= volumeChange;
                //mediaPlayer.setVolume(leftVolume, rightVolume);
                klikles++;
                button.setText("KLIKNIJ MJE PO RAZ " + klikles);
                //volume = 10 - volumeChange;
                //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                //volumeChange += 1;
                textview4.setText(""+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        });

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
        VolumeUp();

    }

    private void VolumeUp(){
            handler.postDelayed(volumeChanger, delay);
        }

    private Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            volumeChange += 1;
            volume = 0 + volumeChange;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

            czas++;
            textview5.setText(String.valueOf(czas));

            VolumeUp();
        }
    };

    public void onPause(){
        super.onPause();
        //TODO: mediaPlayer pause
        if(mediaPlayer != null)
        {
            if(mediaPlayer.isPlaying())mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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

