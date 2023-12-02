package com.example.donna_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CoachingScheduling extends AppCompatActivity {
    private TextView dateText;
    private TextView startTimeText;
    private TextView endTimeText;
    private Button button;
    private Spinner guidanceSpinner;

    private String mGuidanceUrl = "http://192.168.100.17:8000/api/createcoaching"; // Replace with your actual guidance URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching_student);

        dateText = findViewById(R.id.dateText);
        startTimeText = findViewById(R.id.startTimeText);
        endTimeText = findViewById(R.id.endTimeText);
        button = findViewById(R.id.dateButton);
        guidanceSpinner = findViewById(R.id.guidanceSpinner);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTimePicker();
                openDatePicker();
            }
        });

        new LoadGuidancesTask().execute();
    }

    private void openDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateText.setText(String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day));
                dateText.setTextColor(getResources().getColor(android.R.color.black));
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                startTimeText.setText(String.valueOf(hour) + ":" + String.valueOf(minute));

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.add(Calendar.HOUR_OF_DAY, 1);

                int endHour = calendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = calendar.get(Calendar.MINUTE);

                endTimeText.setText(String.valueOf(endHour) + ":" + String.valueOf(endMinute));
            }
        }, currentHour, currentMinute, false);

        timePickerDialog.show();
    }

//    private class LoadGuidancesTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... voids) {
//            String result = "";
//
//            try {
//                URL url = new URL(mGuidanceUrl);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//                try {
//                    urlConnection.setRequestMethod("GET");
//                    InputStream in = urlConnection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//                    StringBuilder stringBuilder = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        stringBuilder.append(line);
//                    }
//
//                    result = stringBuilder.toString();
//
//                } finally {
//                    urlConnection.disconnect();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//                Log.d("JSON Response", result);
//
//                JSONObject responseJson = new JSONObject(result);
//
//                if (responseJson.getBoolean("success")) {
//                    JSONArray guidanceArray = responseJson.getJSONArray("guidances");
//                    List<String> guidanceNames = new ArrayList<>();
//
//                    for (int i = 0; i < guidanceArray.length(); i++) {
//                        JSONObject guidanceObject = guidanceArray.getJSONObject(i);
//                        String fname = guidanceObject.getString("fname");
//                        String lname = guidanceObject.getString("lname");
//                        String fullName = fname + " " + lname;
//
//                        guidanceNames.add(fullName);
//                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachingScheduling.this, android.R.layout.simple_spinner_item, guidanceNames);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    guidanceSpinner.setAdapter(adapter);
//
//                } else {
//                    Toast.makeText(CoachingScheduling.this, "Failed to load guidance data", Toast.LENGTH_SHORT).show();
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Toast.makeText(CoachingScheduling.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
private class LoadGuidancesTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        String url = mGuidanceUrl;

        // Initialize a RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(CoachingScheduling.this);

        // Initialize a StringRequest
        StringRequest stringRequest = new StringRequest(
            Request.Method.GET,
            url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("JSON Response", response);

                        JSONObject responseJson = new JSONObject(response);

                        if (responseJson.getBoolean("success")) {
                            JSONArray guidanceArray = responseJson.getJSONArray("guidances");
                            List<String> guidanceNames = new ArrayList<>();

                            for (int i = 0; i < guidanceArray.length(); i++) {
                                JSONObject guidanceObject = guidanceArray.getJSONObject(i);
                                String fname = guidanceObject.getString("fname");
                                String lname = guidanceObject.getString("lname");
                                String fullName = fname + " " + lname;

                                guidanceNames.add(fullName);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachingScheduling.this, android.R.layout.simple_spinner_item, guidanceNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            guidanceSpinner.setAdapter(adapter);

                        } else {
                            Toast.makeText(CoachingScheduling.this, "Failed to load guidance data", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachingScheduling.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", "Error loading guidance data: " + error.getMessage());
                    Toast.makeText(CoachingScheduling.this, "Error loading guidance data", Toast.LENGTH_SHORT).show();
                }
            }
        );

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);

        return null;
    }

    // No need for onPostExecute as Volley handles the response on a separate thread
}

}

