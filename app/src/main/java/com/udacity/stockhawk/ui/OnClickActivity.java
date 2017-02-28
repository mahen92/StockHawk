package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


import butterknife.BindView;
import butterknife.ButterKnife;


public class OnClickActivity extends AppCompatActivity {
    String closeString="";

    @BindView(R.id.lineChart_activity_line_graph)
    LineChart lineChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click);

        ButterKnife.bind(this);
        closeString = getIntent().getStringExtra("symbol_name");
        Log.v("Checkers",closeString);
        String[] splits= closeString.split(",");
        Log.v("Sizealis Revelio",""+splits[0]);
        Log.v("Sizealis Revelio",""+splits[1]);
        ArrayList<String> closeArray=new ArrayList<String>(Arrays.asList(splits[0].split("%")));
        for(String i:closeArray)
        {
            Log.v("Forealis Revelio",""+i);
        }
        ArrayList<String> dateArray=new ArrayList<String>(Arrays.asList(splits[1].split("%")));
        for(String i:dateArray)
        {
            Log.v("Forealis Revelio",""+i);
        }
        Collections.reverse(closeArray);
        Collections.reverse(dateArray);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();

       for (int i = 0; i < closeArray.size(); i++) {
           // Log.v("Checkers1","Checkers1");
           xvalues.add((dateArray.get(i)));

           entries.add(new Entry( Float.valueOf(closeArray.get(i)), i));

        }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelsToSkip(3);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.rgb(182,182,182));

        YAxis left = lineChart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(10, true);
        left.setTextColor(Color.rgb(182,182,182));

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextSize(16f);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.rgb(25,118,210));
        lineChart.setDescriptionColor(Color.WHITE);
        lineChart.setDescription("Last 12 Months Stock Comparison");

        String name= "Stock";

        LineDataSet dataSet = new LineDataSet(entries, name);
        LineData lineData = new LineData(xvalues, dataSet);

        lineChart.animateX(2500);
        lineChart.setData(lineData);
    }

}
