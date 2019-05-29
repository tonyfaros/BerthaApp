package com.example.prueba1.FuelControl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.prueba1.Pattern.Singleton;
import com.example.prueba1.Profile.My_Cars;
import com.example.prueba1.R;
import com.example.prueba1.StartDrive.Main4Activity;
import com.example.prueba1.Utils.BottomNavigationViewHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FuelControlActivity extends AppCompatActivity {

    private static final String TAG = "FuelControlActivity";

    private Context mContext = FuelControlActivity.this;

    private static final int ACTIVITY_NUM = 1;

    private final String[] values_firstGridstr = {"AV L/100 km", "LAST L/100 km","BEST L/100 km","Total km Tracked","Fuel logs","Total Liters"};

    private final String[] values_firstGridint = {"6.7","7.5","6.4","1887","5","171"};

    private final String[] values_secondGridstr = {"Avg. Price/Liter", "Avg. Fuel-Up Cost","Avg. Price/km"};

    private final String[] values_secondGridint = {"$677","$23.16","$45.42"};

    private GridView gridView_stats,gridView_prices;

    private FloatingActionButton floatingActionButton_addLog;

    private BarChart barChart;
    private CombinedChart combinedChart;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String id_user = "id_user";
    public static final String id_car = "id_car";

    private String id_userLogged;
    private String id_carDef;

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "June"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuelcontrol);

        setupBottomNavigationView();

        gridView_stats = findViewById(R.id.gridviewHorizontal);
        gridView_prices = findViewById(R.id.gridview_prices);

        floatingActionButton_addLog = findViewById(R.id.fab_button_addFuelLog);

        //Get user id and car id

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        id_userLogged = sharedPreferences.getString(id_user,"");
        id_carDef = sharedPreferences.getString(id_car,"");

        getFuelLogs();

        //BarChart
        //PieChart
        //CombinedChart
        barChart = findViewById(R.id.barChart);

        combinedChart = findViewById(R.id.combinedChart);

        Items_adapter items_adapter = new Items_adapter(this,values_firstGridint,values_firstGridstr);
        gridView_stats.setAdapter(items_adapter);

        Items_adapter items_adapterPrices = new Items_adapter(this,values_secondGridint,values_secondGridstr);
        gridView_prices.setAdapter(items_adapterPrices);


        floatingActionButton_addLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FuelControlActivity.this,Fuel_log.class));
            }
        });


        /// BAR CHART

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1,40f));
        barEntries.add(new BarEntry(2,44f));
        barEntries.add(new BarEntry(3,30f));
        barEntries.add(new BarEntry(4,36f));

        BarDataSet barDataSet = new BarDataSet(barEntries,"Data set1");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);

        /// COMBINED CHART

        combinedChart.getDescription().setText("Descripcion");
        combinedChart.setDrawGridBackground(true);

        combinedChart.setDrawBarShadow(true);
        combinedChart.setHighlightFullBarEnabled(true);

        // draw bars behind lines
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return mMonths[(int) value % mMonths.length];
            }
        });

        CombinedData dataCombined = new CombinedData();

        dataCombined.setData( generateLineData());
        dataCombined.setData(generateBarData());

        xAxis.setAxisMaximum(dataCombined.getXMax() + 0.25f);
        combinedChart.setData(dataCombined);
        combinedChart.invalidate();
    }

    private void setupBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Main4Activity.class);
        startActivity(intent);
        finish();
    }

    private ArrayList<Entry> getLineEntriesData(ArrayList<Entry> entries){
        entries.add(new Entry(1, 20));
        entries.add(new Entry(2, 10));
        entries.add(new Entry(3, 8));
        entries.add(new Entry(4, 40));
        entries.add(new Entry(5, 37));

        return entries;
    }

    private ArrayList<BarEntry> getBarEnteries(ArrayList<BarEntry> entries){
        entries.add(new BarEntry(1, 25));
        entries.add(new BarEntry(2, 30));
        entries.add(new BarEntry(3, 38));
        entries.add(new BarEntry(4, 10));
        entries.add(new BarEntry(5, 15));
        return  entries;
    }

    private LineData generateLineData() {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries = getLineEntriesData(entries);

        LineDataSet set = new LineDataSet(entries, "Line");
        //set.setColor(Color.rgb(240, 238, 70));
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        entries = getBarEnteries(entries);

        BarDataSet set1 = new BarDataSet(entries, "Bar");
        //set1.setColor(Color.rgb(60, 220, 78));
        set1.setColors(ColorTemplate.COLORFUL_COLORS);
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }

    //Download data from BD
    public void getFuelLogs(){
        String url_get_fuelLogs = "https://evening-oasis-22037.herokuapp.com/fuelLog/getLogsxUser/" + id_userLogged+
                                                                                                        "/"+id_carDef;
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url_get_fuelLogs, null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG,response.toString());
                        try{
                            int len = response.length();
                            for(int i = 0 ;i<len; i++){
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String date = jsonObject.getString("date");
                                String time = jsonObject.getString("time");
                                int odometer_current = jsonObject.getInt("odometer_current_value");
                                int liters_qtty = jsonObject.getInt("liters_quantity");
                                double total_price = jsonObject.getDouble("total_price");
                                double price_perLiter = jsonObject.getDouble("price_per_liter");
                                String fuel_type = jsonObject.getString("fuel_type");

                                Singleton.getInstance(getApplicationContext()).addList_fuelLogs(new Log_object(id,date,time,odometer_current,liters_qtty,total_price,price_perLiter,fuel_type));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();

            }
        });

        Singleton.getInstance(FuelControlActivity.this).addToRequestQueue(getRequest);
    }
}
