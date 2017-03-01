package com.udacity.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

    @BindView(R.id.toolbar_activity_line_graph)
    Toolbar toolbar;

    @BindView(R.id.ll_activity_line_graph)
    LinearLayout linearLayout;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.marketcap)
    TextView marketcap;

    @BindView(R.id.day_low)
    TextView day_low;

    @BindView(R.id.day_high)
    TextView day_high;

    @BindView(R.id.year_low)
    TextView year_low;

    @BindView(R.id.year_high)
    TextView year_high;

    @BindView(R.id.quarterly_estimate)
    TextView quarterly_estimate;

    @BindView(R.id.yearly_estimate)
    TextView yearly_estimate;

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_click);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if(!networkUp())
        {
            Toast.makeText(this, getString(R.string.no_network_on_click), Toast.LENGTH_LONG).show();
        }
        name.setText(getIntent().getStringExtra("name"));
        marketcap.setText(getIntent().getStringExtra("marketcap"));
        day_low.setText(getIntent().getStringExtra("days_low"));
        day_high.setText(getIntent().getStringExtra("days_high"));
        year_low.setText(getIntent().getStringExtra("years_low"));
        year_high.setText(getIntent().getStringExtra("years_high"));
        quarterly_estimate.setText(getIntent().getStringExtra("quarterly_estimate"));
        yearly_estimate.setText(getIntent().getStringExtra("yearly_estimate"));
        closeString = getIntent().getStringExtra("symbol_name");
        String[] splits= closeString.split(",");

        ArrayList<String> closeArray=new ArrayList<String>(Arrays.asList(splits[0].split("%")));
        ArrayList<String> dateArray=new ArrayList<String>(Arrays.asList(splits[1].split("%")));

        Collections.reverse(closeArray);
        Collections.reverse(dateArray);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();

       for (int i = 0; i < closeArray.size(); i++) {
           xvalues.add((dateArray.get(i)));
           entries.add(new Entry( Float.valueOf(closeArray.get(i)), i));
       }
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelsToSkip(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
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
        lineChart.setDescription(getString(R.string.graph_desc));

        String name= getString(R.string.graph_legend);

        LineDataSet dataSet = new LineDataSet(entries, name);
        LineData lineData = new LineData(xvalues, dataSet);

        lineChart.animateX(2500);
        lineChart.setData(lineData);
    }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

}
