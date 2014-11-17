package com.skradacz.audiometer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //todo: graph

        Global global = ((Global)getApplicationContext());
        GraphViewSeries rightEarSeries = global.rightEarSeries;
        GraphViewSeries leftEarSeries = global.leftEarSeries;

        GraphView graphView = new LineGraphView(this, "");
        graphView.addSeries(rightEarSeries);
        graphView.addSeries(leftEarSeries);
        graphView.setShowLegend(true);
//        graphView.setHorizontalLabels(new String[] {"250", "500", "1000", "2000", "4000", "8000"});
//        graphView.setVerticalLabels(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
//                "10"});
//        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if (isValueX) {
//                    return String.valueOf(value);
//                }else{
//                    return String.valueOf(value);
//                }
//            }
//        });

        // set colors
        graphView.getGraphViewStyle().setGridColor(Color.WHITE);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        layout.addView(graphView);
    }
}

