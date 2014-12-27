package com.skradacz.audiometer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

class ToneGen2 {

    private final int sampleRate = 20000;
    private AudioTrack audioTrack;
    private static final String TAG = ToneGen2.class.getSimpleName();
    private final int duration = 6; // seconds
    private final int numSamples = duration * sampleRate;
    @SuppressWarnings("FieldCanBeLocal")
    private final double sample[] = new double[numSamples];
    private final byte generatedSnd[] = new byte[2 * numSamples];

    public ToneGen2(double frequency, double amplitude){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequency));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * amplitude * 500));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

    }

    public void play(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

    public void stop(){
        try{
            audioTrack.stop();
            audioTrack.release();
        }catch(Exception e){
            Log.d(TAG, "stop(), exception captured");
        }
    }

    public void volume(float leftVolume, float rightVolume){

       /**
        * @param leftVolume Left volume 0.0f - silent, 1.0f full volume
        * @param rightVolume Right volume 0.0f - silent, 1.0f full volume
        */

        //noinspection deprecation
        audioTrack.setStereoVolume(leftVolume, rightVolume);
    }
}