package com.example.donna_app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class ChartActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        barChart = findViewById(R.id.barChart);

        // Make a Volley request to fetch chart data from the Laravel backend
        fetchDataFromBackend();
    }

    private void fetchDataFromBackend() {
        String url = "http://192.168.117.61:8000/api/chart-data"; // Replace with your Laravel backend URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and update the chart
                        parseJsonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("ChartActivity", "Volley Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Add your authorization header if needed
                headers.put("Authorization", "Bearer " + retrieveAccessToken());
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private String retrieveAccessToken() {
        // Implement your logic to retrieve the access token from SharedPreferences
        // Example: Assume the access token is stored with the key "access_token"
        // Modify this according to how you store your access token
        return getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE)
                .getString(Constants.ACCESS_TOKEN_KEY, null);
    }

    private void parseJsonResponse(JSONObject jsonResponse) {
        Map<Integer, Integer> yearCountMap = new HashMap<>();

        try {
            // Iterate through the keys (violations_ids)
            Iterator<String> keys = jsonResponse.keys();

            while (keys.hasNext()) {
                String violationsId = keys.next();
                JSONObject dateRecords = jsonResponse.getJSONObject(violationsId);

                // Iterate through the date records for each violations_id
                Iterator<String> dateKeys = dateRecords.keys();
                while (dateKeys.hasNext()) {
                    String dateRecord = dateKeys.next();
                    int count = dateRecords.getInt(dateRecord);

                    // Extract the year from the date
                    int year = Integer.parseInt(dateRecord.substring(0, 4));

                    // Update the count for the corresponding year
                    if (yearCountMap.containsKey(year)) {
                        int currentCount = yearCountMap.get(year);
                        yearCountMap.put(year, currentCount + count);
                    } else {
                        yearCountMap.put(year, count);
                    }
                }
            }

            // Create the chart with the parsed data
            createChart(yearCountMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Create the chart with the parsed data
    private void createChart(Map<Integer, Integer> yearCountMap) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> years = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Integer, Integer> entry : yearCountMap.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            years.add(String.valueOf(entry.getKey())); // Add the year as a label
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Violations Chart");
        dataSet.setColor(Color.BLUE);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Customize the appearance of the chart
        Description description = new Description();
        description.setText("Violations Chart");
        barChart.setDescription(description);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(years)); // Set the custom YearValueFormatter
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Fixed position and avoid skipping labels

        YAxis leftYAxis = barChart.getAxisLeft();
        YAxis rightYAxis = barChart.getAxisRight();
        leftYAxis.setAxisMinimum(0);
        rightYAxis.setAxisMinimum(0);

        barChart.invalidate(); // Refresh the chart
    }
}