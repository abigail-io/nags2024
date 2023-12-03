package com.example.donna_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private ListView listView;
    private List<StudentRecord> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_student);

        // Initialize views
        listView = findViewById(R.id.listView);

        // Initialize data list
        dataList = new ArrayList<>();

        // Execute the method to perform the initial API call
        getDataFromApi();
    }

    private void getDataFromApi() {
        String apiUrl = "http://192.168.55.119:8000/api/dashboard";

        String accessToken = retrieveAccessToken();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API Request", "Success");

                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject object = dataArray.getJSONObject(i);
                                String dateRecorded = object.getString("date_recorded");
                                String remarks = object.getString("remarks");
                                String status = object.getString("status");
//                                int studentId = object.getInt("student_id");
//                                int violationId = object.getInt("violation_id");
//                                int guidanceId = object.getInt("guidance_id");
                                String violationName = object.getJSONObject("violations").getString("name");
                                String guidanceName = object.getJSONObject("guidances").getString("fname");
                                String studentName = object.getJSONObject("students").getString("fname");

                                StudentRecord record = new StudentRecord(dateRecorded, remarks, status, violationName , guidanceName, studentName);
                                dataList.add(record);
                            }

                            StudentRecordAdapter adapter = new StudentRecordAdapter(DashboardActivity.this, dataList);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("API Request", "Failed to parse JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("API Request", "Failed to retrieve data. Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }
}
