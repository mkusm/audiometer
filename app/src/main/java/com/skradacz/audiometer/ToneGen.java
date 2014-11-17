package com.skradacz.audiometer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

class ToneGen {

    private final int sampleRate = 44100;
    @SuppressWarnings("FieldCanBeLocal")
    private final double RAD = 2.0 * Math.PI;
    private AudioTrack aTrack;
    @SuppressWarnings("CanBeFinal")
    private byte[] buffer;
    private static final String TAG = ToneGen.class.getSimpleName();

    /**
     * @param frequency The frequency of the tone in Hz
     * @param duration The duration of the tone in seconds
     */
    public ToneGen(double frequency, int duration, double amplitude){
        int seconds = duration * 2 * 2;

        buffer = new byte[sampleRate * seconds];
        for ( int i=0; i<buffer.length; i++ )
        {
            buffer[i] = (byte)( Math.sin( RAD * frequency / sampleRate * i ) * amplitude);
        }

    }

    public void play(){
        aTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffer.length, AudioTrack.MODE_STATIC);
        aTrack.write(buffer, 0, buffer.length);
        aTrack.play();
    }

    public void stop(){
        try{
            aTrack.stop();
            aTrack.release();
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
        aTrack.setStereoVolume(leftVolume, rightVolume);
    }
}