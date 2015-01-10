package com.skradacz.audiometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ExaminationActivity extends Activity {

    final Context context = this;
    private static final String TAG = ExaminationActivity.class.getSimpleName();

    private TextView textview2, textview3, textview4, textview5, textview6, textview7,
            testInfoTextView;
    private TextView clickHere;

    private AudioManager audioManager;
    private ToneGen toneGen;
    private final Handler handler = new Handler();
    private final StringBuilder stringBuilder = new StringBuilder();
    private final List<Integer> listLeftEar = new ArrayList<>();
    private final List<Integer> listRightEar = new ArrayList<>();

    private double frequency = 0;                 // toneGen frequency
    private double amplitude = 0;                 // toneGen amplitude
    private double base = 0;

    private int mode = 11;
    private int textview7Counter = 1;
    private boolean stop = false;
    private String result;
    private boolean rightEar = false;
    private boolean leftEar = true;
    private double freqChecker = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        clickHere = (TextView) findViewById(R.id.start_text_view);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);
        textview5 = (TextView) findViewById(R.id.textView5);
        textview6 = (TextView) findViewById(R.id.textView6);
        textview7 = (TextView) findViewById(R.id.textView7);
        testInfoTextView = (TextView) findViewById(R.id.testInfoTextView);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        toneGen = new ToneGen(frequency, amplitude);

        Global global = ((Global)getApplicationContext());
        if (global.getTestChecked()) {
            textview2.setVisibility(View.VISIBLE);
            textview3.setVisibility(View.VISIBLE);
            textview4.setVisibility(View.VISIBLE);
            textview5.setVisibility(View.VISIBLE);
            textview6.setVisibility(View.VISIBLE);
            testInfoTextView.setVisibility(View.VISIBLE);
        }else {
            textview2.setVisibility(View.INVISIBLE);
            textview3.setVisibility(View.INVISIBLE);
            textview4.setVisibility(View.INVISIBLE);
            textview5.setVisibility(View.INVISIBLE);
            textview6.setVisibility(View.INVISIBLE);
            testInfoTextView.setVisibility(View.INVISIBLE);
        }

        stringBuilder.append(getString(R.string.details_left_ear_text));

        clickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (freqChecker != frequency) {
                    //'if' makes sure frequency changed before button was clicked
                    if (!rightEar && leftEar) {
                        stringBuilder.append(getString(R.string.details_freq_text) + frequency + getString(R.string.details_threshold_text)
                                + (mode) + "\n");
                        try{
                            listLeftEar.add(mode - 1);
                        }catch (Exception e) {
                            Log.d(TAG, "stop(), exception captured");
                        }
                    } else if (rightEar && !leftEar) {
                        stringBuilder.append(getString(R.string.details_freq_text) + frequency + getString(R.string.details_threshold_text)
                                + (mode) + "\n");
                        try{
                            listRightEar.add(mode - 1);
                        }catch (Exception e) {
                            Log.d(TAG, "stop(), exception captured");
                        }
                    }
                    mode = 11;
                    if (frequency == 8000 && rightEar) {
                        result = stringBuilder.toString();

                        AlertDialog.Builder builder = new AlertDialog
                                .Builder(context);
                        builder
                                .setTitle(getString(R.string.results_dialog_title_text))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ExaminationActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(getString(R.string.results_dialog_negative_button_text), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog
                                                .Builder(context);

                                        alertDialogBuilder
                                                .setTitle(getString(R.string.results_dialog_negative_button_text))
                                                .setMessage(result)
                                                .setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        ExaminationActivity.this.finish();
                                                    }
                                                });

                                        // create alert dialog
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });

                        for (int i = 0; i < 6; i++) {
                            if (
                                    (listLeftEar.get(i) - listRightEar.get(i)) > 2 ||
                                    (listLeftEar.get(i) - listRightEar.get(i)) < -2 ||
                                    (listMax(listLeftEar) - listMin(listLeftEar)) > 3 ||
                                    (listMax(listLeftEar) - listMin(listLeftEar)) < -3 ||
                                    (listMax(listRightEar) - listMin(listRightEar)) > 3 ||
                                    (listMax(listRightEar) - listMin(listRightEar)) < -3
                                    ) {
                                builder.setMessage(getString(
                                        R.string.result_dialog_no_hearing_loss_text));
                            } else {
                                builder.setMessage(getString(
                                        R.string.result_dialog_hearing_loss_text));
                            }
                        }

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    toneGen.stop();

                    textview7Counter += 1;
                    if (textview7Counter < 13) {
                        textview7.setText("Test " + textview7Counter + "/12");
                    }

                    clickHere.setClickable(false);
                    freqChecker = frequency;
                }
            }
        });

        textview2.setText("toneGen.volume: " + String.valueOf(0.1));
        textview3.setText("Amplitude: " + String.valueOf(amplitude));
        textview4.setText("StreamVolume: " + String.valueOf(audioManager.getStreamVolume(
                AudioManager.STREAM_MUSIC)));
        textview5.setText("mode: " + String.valueOf(mode));
        textview6.setText("frequency: " + String.valueOf(frequency));
        testInfoTextView.setText("left ear: " + String.valueOf(leftEar) + " right ear: "
                + String.valueOf(rightEar));

        clickHere.setClickable(false);

        VolumeUp();
    }

    private void VolumeUp(){
        // gets volumeChanger running every 'delay' seconds
        int delay = 1000 * 3;
        handler.postDelayed(volumeChanger, delay);
    }

    private final Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
            clickHere.setClickable(true);

            if (!stop){
                VolumeUp();

                if (!rightEar && !leftEar) {
                    leftEar = true;
                    frequency = 250;
                }

                if (mode == 10 || mode == 11) {
                    mode = -1;
                    if (frequency == 0){
                        frequency = 250;
                        base = 0.61;
                    }else if (frequency == 250) {
                        frequency = 500;
                        base = 0.609;
                        if (leftEar && !rightEar && listLeftEar.size()<1) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<1) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                    }else if (frequency == 500) {
                        frequency = 1000;
                        base = 0.609;
                        if (leftEar && !rightEar && listLeftEar.size()<2) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<2) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                    }else if (frequency == 1000) {
                        frequency = 2000;
                        base = 0.61;
                        if (leftEar && !rightEar && listLeftEar.size()<3) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<3) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                    }else if (frequency == 2000) {
                        frequency = 4000;
                        base = 0.619;
                        if (leftEar && !rightEar && listLeftEar.size()<4) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<4) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                    }else if (frequency == 4000) {
                        frequency = 8000;
                        base = 2.121;
                        if (leftEar && !rightEar && listLeftEar.size()<5) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<5) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                    }else if (frequency == 8000) {
                        if (leftEar && !rightEar && listLeftEar.size()<6) {
                            listLeftEar.add(11);
                            textview7Counter =+ 1;
                        } else if (!leftEar && rightEar && listRightEar.size()<6) {
                            listRightEar.add(11);
                            textview7Counter =+ 1;
                        }
                        if (!rightEar && leftEar) {
                            rightEar = true;
                            leftEar = false;
                            frequency = 250;
                            base = 0.61;
                            stringBuilder.append(getString(R.string.details_right_ear_text));
                        }else if (rightEar && !leftEar){
                            leftEar = true;
                        }
                        if (rightEar && leftEar) {
                            stop = true;
                        }
                    }
                }

                if (!stop){

                    mode++;

                    if (mode == 0) {
                        amplitude = 0.6;
                    }else if (mode == 1) {
                        amplitude = 0.7;
                    }else if (mode == 2) {
                        amplitude = 1.4;
                    }else if (mode == 3) {
                        amplitude = 3;
                    }else if (mode > 3 && mode < 9) {
                        amplitude = (Math.pow(2, mode-2));
                    }else if (mode == 9) {
                        amplitude = 64;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0);
                    }else if (mode == 10) {
                        amplitude = 64;
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);

                    }
                    amplitude = amplitude * base;
                    toneGen.stop();
                    toneGen = new ToneGen(frequency, amplitude);
                    toneGen.play();
                    if (rightEar) {
                        toneGen.volume(0.0f, 0.1f);
                    }else if (leftEar) {
                        toneGen.volume(0.1f, 0.0f);
                    }

                    textview2.setText("toneGen.volume: " + String.valueOf(0.1));
                    textview3.setText("Amplitude: " + String.valueOf(amplitude));
                    textview4.setText("StreamVolume: " + String.valueOf(audioManager
                            .getStreamVolume(AudioManager.STREAM_MUSIC)));
                    textview5.setText("mode: " + String.valueOf(mode));
                    textview6.setText("frequency: " + String.valueOf(frequency));
                    testInfoTextView.setText("left ear: " + String.valueOf(leftEar) + " right ear: "
                            + String.valueOf(rightEar));

                }else {
                    toneGen.stop();
                }
            }
        }
    };

    static public int listMin(List array) {
        // Takes a list of integers as an input, and returns the lowest value
        Integer minObj = (Integer)array.get(0);

        for (int i = 0; i < array.size(); i++) {
            Integer item = (Integer)array.get(i);
            if (item.compareTo(minObj) < 0) {
                minObj = item;
            }
        }
        return minObj;
    }

    static public int listMax(List array) {
        // Takes a list of integers as an input, and returns the highest value
        Integer maxObj = (Integer)array.get(0);

        for (int i = 0; i < array.size(); i++) {
            Integer item = (Integer)array.get(i);
            if (item.compareTo(maxObj) > 0) {
                maxObj = item;
            }
        }
        return maxObj;
    }

    @Override
    public void onPause(){
        super.onPause();
        toneGen.stop();
        stop = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        stop = false;
    }
}
