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
import java.util.Arrays;
import java.util.List;


/**
 * This is an Activity which will be used throughout the whole examination.
 */
public class ExaminationActivity extends Activity {

    final Context context = this;
    private static final String TAG = ExaminationActivity.class.getSimpleName();

    private TextView currentToneGenVolumeTextView, currentAmplitudeTextView,
            currentStreamVolumeTextView, currentModeTextView, currentFrequencyTextView,
            currentTestNumberTextView, currentEarExaminedTextView;
    private TextView clickHereWhenYouHearTheSoundTextView;

    private AudioManager audioManager;
    private ToneGen toneGen;
    private Handler handler = new Handler();

    // This builder creates a String which will be used as a detailed result available after
    // the examination.
    private StringBuilder examinationResultDetailsBuilder = new StringBuilder();
    private String examinationResultDetails;

    private List<Integer> leftEarHearingList = new ArrayList<>();
    private List<Integer> rightEarHearingList = new ArrayList<>();

    private final double[][] MODE_VOLUMES = {
            {0.6, 0.7, 1.4, 3, 4, 8, 16, 32, 64, 64, 64},
            {5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 9}
    };
    private final Integer[] FREQUENCIES = {250, 500, 1000, 2000, 4000, 8000};
    private final double[] MULTIPLIERS = {0.61, 0.609, 0.609, 0.61, 0.619, 2.121};

    private int currentFrequency = 8000;                                  // toneGen frequency
    private double currentAmplitudeWithoutMultiplier = 0;                 // toneGen amplitude
    @SuppressWarnings("FieldCanBeLocal")
    private double amplitudeMultiplier = 0;
    private int currentEar = 1;
    private int currentMode = 11;
    private int currentTestNumber = 1;
    private boolean examinationStopped = false;
    private boolean examinationStarted = false;
    private boolean examinationFinished  = false;
    private boolean hearingLossOutOfRange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        clickHereWhenYouHearTheSoundTextView = (TextView) findViewById(R.id.hear_sound_text_view);
        currentToneGenVolumeTextView = (TextView) findViewById(R.id.textView2);
        currentAmplitudeTextView = (TextView) findViewById(R.id.textView3);
        currentStreamVolumeTextView = (TextView) findViewById(R.id.textView4);
        currentModeTextView = (TextView) findViewById(R.id.textView5);
        currentFrequencyTextView = (TextView) findViewById(R.id.textView6);
        currentTestNumberTextView = (TextView) findViewById(R.id.textView7);
        currentEarExaminedTextView = (TextView) findViewById(R.id.testInfoTextView);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        toneGen = new ToneGen(currentFrequency, currentAmplitudeWithoutMultiplier);
        setNextVolumeLevelAfterThreeSeconds();
        examinationResultDetailsBuilder.append(getString(R.string.details_left_ear_text));

        // For Debug Mode
        Global global = ((Global)getApplicationContext());
        if (global.getTestChecked()) {
            setDebugTextViewsVisibility(View.VISIBLE);
            updateDebugTextViewsText();
        } else {
            setDebugTextViewsVisibility(View.INVISIBLE);
        }

        // This button is the only button available during the examination.
        clickHereWhenYouHearTheSoundTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User is not supposed to click the button for first three seconds of the new test,
                // because there is no sound yet. Button is set back to clickable later in the code,
                // once the volume has been increased for the first time.
                clickHereWhenYouHearTheSoundTextView.setClickable(false);

                // save results of current test
                saveCurrentFrequencyAndCurrentMode();

                // inform volumeChanger that test for current frequency is done
                currentMode = 11;
                if (currentFrequency == 8000 && currentEar == 2) {
                    finishExamination();
                }
                toneGen.stop();

                // inform user about new test start
                currentTestNumber += 1;
                if (currentTestNumber < 13) {
                    currentTestNumberTextView.setText(String.format("Test %d/12", currentTestNumber));
                }
            }
        });

        clickHereWhenYouHearTheSoundTextView.setClickable(false);
    }

    private void finishExamination() {
        toneGen.stop();
        examinationStopped = true;
        showExaminationResultAlertDialog();
    }

    private void appendFrequencyAndModeToResultDetailsBuilder() {
        examinationResultDetailsBuilder
                .append(getString(R.string.details_freq_text))
                .append(currentFrequency)
                .append(getString(R.string.details_threshold_text))
                .append(currentMode)
                .append("\n");
    }

    /**
     * Creates and shows AlertDialog with simple result of the examination. It only says if there
     * could be a potential hearing loss or not. Also, provides a button to check detailed result.
     */
    private void showExaminationResultAlertDialog() {
        examinationResultDetails = examinationResultDetailsBuilder.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    showExaminationResultDetailsAlertDialog();
                }
            });

        if (isHearingLost()) {
            builder.setMessage(getString(R.string.result_dialog_hearing_loss_found_text));
        } else {
            builder.setMessage(getString(R.string.result_dialog_no_hearing_loss_found_text));
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * To be used after the examination is finished. Determines whether the user could have a
     * potential hearing loss.
     * Algorithm used assumes, that if there is a difference bigger than 2 Mode levels between
     * left and right ear, there might be a hearing loss for one of them. Also, if difference in
     * hearing on different frequencies is bigger than 3.
     * @return boolean
     */
    private boolean isHearingLost() {
        if (hearingLossOutOfRange) {
            return true;
        }
        for (int i = 0; i < 6; i++) {
            if (Math.abs(leftEarHearingList.get(i) - rightEarHearingList.get(i)) > 2) {
                return true;
            }
        }
        return Math.abs(listMax(leftEarHearingList) - listMin(leftEarHearingList)) > 3 ||
                Math.abs(listMax(rightEarHearingList) - listMin(rightEarHearingList)) > 3;
    }

    /**
     * Creates and shows AlertDialog with detailed info on hearing levels for given frequency and
     * given ear.
     */
    private void showExaminationResultDetailsAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
            .setTitle(getString(R.string.results_dialog_negative_button_text))
            .setMessage(examinationResultDetails)
            .setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ExaminationActivity.this.finish();
                }
            });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * @param visible View.VISIBLE to show debug info, View.INVISIBLE to hide
     */
    private void setDebugTextViewsVisibility(int visible) {
        currentToneGenVolumeTextView.setVisibility(visible);
        currentAmplitudeTextView.setVisibility(visible);
        currentStreamVolumeTextView.setVisibility(visible);
        currentModeTextView.setVisibility(visible);
        currentFrequencyTextView.setVisibility(visible);
        currentEarExaminedTextView.setVisibility(visible);
    }

    /** calls valumeChanger function with 3 seconds delay on seperate thread */
    private void setNextVolumeLevelAfterThreeSeconds(){
        int delay = 1000 * 3;
        handler.postDelayed(volumeChanger, delay);
    }

    /** gives debug mode text views current values of different volume settings */
    private void updateDebugTextViewsText() {
        currentToneGenVolumeTextView.setText(
            String.format("toneGen.setVolume: %s", String.valueOf(0.1))
        );
        currentAmplitudeTextView.setText(
            String.format("Amplitude: %s", String.valueOf(currentAmplitudeWithoutMultiplier))
        );
        currentStreamVolumeTextView.setText(
            String.format("StreamVolume: %s",
                          String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)))
        );
        currentModeTextView.setText(
            String.format("currentMode: %s", String.valueOf(currentMode))
        );
        currentFrequencyTextView.setText(
            String.format("currentFrequency: %s", String.valueOf(currentFrequency))
        );
        currentEarExaminedTextView.setText(
            String.format("currentEar: %s", String.valueOf(currentEar))
        );
    }

    /**
     * changes the volume based on currentMode and currentFrequency,
     * then runs itself with 3 seconds delay if examination is not finished
     */
    private final Runnable volumeChanger = new Runnable() {
        @Override
        public void run() {

            // examinationStopped is true if:
            //  - user paused or left activity
            //  - the examination ended (frequency 8000 and (button clicked or mode > 10))
            if (examinationStopped) {
                return;
            }

            // this is a recursion call, which effectively works like a loop with 3 second interval
            setNextVolumeLevelAfterThreeSeconds();

            // this block is executed when user didn't click button through all modes in one frequency
            if (currentMode == 10) {
                hearingLossOutOfRange = true;
                currentMode++;
                saveCurrentFrequencyAndCurrentMode();
            }

            if (currentMode == 11) {
                // change frequency after clicking button or after all modes passed
                changeFrequency();
                if (examinationFinished) {
                    return;
                }
                currentMode = 0;
            } else {
                currentMode++;
            }

            // prepare volume variables
            currentAmplitudeWithoutMultiplier = MODE_VOLUMES[0][currentMode];
            int currentFrequencyIndex = Arrays.asList(FREQUENCIES).indexOf(currentFrequency);
            amplitudeMultiplier = MULTIPLIERS[currentFrequencyIndex];
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, (int) MODE_VOLUMES[1][currentMode], 0);
            double currentAmplitudeWithMultiplier =
                    currentAmplitudeWithoutMultiplier * amplitudeMultiplier;

            // stop previous sound and start new one with new volume settings
            toneGen.stop();
            toneGen = new ToneGen((double)currentFrequency, currentAmplitudeWithMultiplier);
            toneGen.play();
            if (currentEar == 1) {
                toneGen.setVolume(0.1f, 0.0f);
            } else if (currentEar == 2) {
                toneGen.setVolume(0.0f, 0.1f);
            }

            updateDebugTextViewsText();

            // after the first volume in frequency, user is allowed to click the button
            clickHereWhenYouHearTheSoundTextView.setClickable(true);
        }

        private void changeFrequency() {
            if (currentFrequency == FREQUENCIES[FREQUENCIES.length - 1]) {
                // after the last frequency we either move to next ear or finish examination
                currentFrequency = FREQUENCIES[0];
                if (examinationStarted) {
                    appendResultToHearingList(6);
                    if (currentEar == 1) {
                        // move to right ear
                        currentEar++;
                        examinationResultDetailsBuilder.append(getString(R.string.details_right_ear_text));
                    } else {
                        finishExamination();
                        examinationFinished = true;
                    }
                } else {
                    examinationStarted = true;
                }
            } else {
                int currentFrequencyIndex = Arrays.asList(FREQUENCIES).indexOf(currentFrequency);
                appendResultToHearingList(currentFrequencyIndex + 1);
                currentFrequency = FREQUENCIES[currentFrequencyIndex + 1];
            }
        }


        private void appendResultToHearingList(int maxHearingListSize) {
            if (currentEar == 1 && leftEarHearingList.size() < maxHearingListSize) {
                leftEarHearingList.add(11);
                currentTestNumber =+ 1;
            } else if (currentEar == 2 && rightEarHearingList.size() < maxHearingListSize) {
                rightEarHearingList.add(11);
                currentTestNumber =+ 1;
            }
        }
    };

    private void addCurrentModeToEarList(List<Integer> list) {
        try {
            list.add(currentMode - 1);
        } catch (Exception e) {
            Log.d(TAG, "exception captured when adding to list");
        }
    }

    private void saveCurrentFrequencyAndCurrentMode() {
        if (currentEar == 1) {
            appendFrequencyAndModeToResultDetailsBuilder();
            addCurrentModeToEarList(leftEarHearingList);
        } else if (currentEar == 2) {
            appendFrequencyAndModeToResultDetailsBuilder();
            addCurrentModeToEarList(rightEarHearingList);
        }
    }

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
        examinationStopped = true;
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
