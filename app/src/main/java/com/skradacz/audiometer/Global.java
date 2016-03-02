package com.skradacz.audiometer;

import android.app.Application;
import android.graphics.Color;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;


public class Global extends Application {
    private boolean isTestChecked = false;
    public boolean getTestChecked(){
        return isTestChecked;
    }
    public void setTestChecked(boolean b){
        isTestChecked = b;
    }

    GraphViewSeries leftEarSeries = new GraphViewSeries("Right Ear", new GraphViewSeries
            .GraphViewSeriesStyle(Color.rgb(200, 50, 0), 1), new GraphView.GraphViewData[]{
            new GraphView.GraphViewData(500, 2),
            new GraphView.GraphViewData(1000, 3),
            new GraphView.GraphViewData(2000, 1),
            new GraphView.GraphViewData(4000, 4)
    });

    public void setLeftEarSeries(GraphViewSeries leftEarSeries) {
        this.leftEarSeries = leftEarSeries;
    }

    public GraphViewSeries getLeftEarSeries() {
        return leftEarSeries;
    }

    GraphViewSeries rightEarSeries = new GraphViewSeries("Left Ear", new GraphViewSeries
            .GraphViewSeriesStyle(Color.rgb(90, 250, 0), 1), new GraphView.GraphViewData[]{
            new GraphView.GraphViewData(500, 2),
            new GraphView.GraphViewData(1000, 2),
            new GraphView.GraphViewData(2000, 2),
            new GraphView.GraphViewData(4000, 1)});

    public void setRightEarSeries(GraphViewSeries rightEarSeries) {
        this.rightEarSeries = rightEarSeries;
    }

    public GraphViewSeries getRightEarSeries() {
        return rightEarSeries;
    }
}
