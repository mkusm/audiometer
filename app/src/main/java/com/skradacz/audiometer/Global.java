package com.skradacz.audiometer;

import android.app.Application;

public class Global extends Application {
    private boolean isTestChecked = false;

    public boolean getTestChecked(){
        return isTestChecked;
    }
    public void setTestChecked(boolean b){
        isTestChecked = b;
    }
}
