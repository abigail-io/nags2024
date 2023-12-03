package com.example.donna_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GoodMoralRequest extends AppCompatActivity {

    private TextView descriptionTextView;
    private TextView statusTextView;
    private TextView studentIdTextView;
    private TextView scheduleDateTextView;
    private TextView goodMoralIdTextView;
    private Button buttonAddNewRequest;
    private Button buttonRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodmoral_request);

        descriptionTextView = findViewById(R.id.descriptionTextView);
        statusTextView = findViewById(R.id.statusTextView);
        studentIdTextView = findViewById(R.id.studentIdTextView);
        scheduleDateTextView = findViewById(R.id.scheduleDateTextView);
        goodMoralIdTextView = findViewById(R.id.goodMoralIdTextView);
        buttonAddNewRequest = findViewById(R.id.buttonAddNewRequest);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        buttonAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRequest();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExistingRequest();

            }
        });
        getDataFromApi();
    }

    private void getDataFromApi() {
        String apiUrl = "http://192.168.55.119:8000/api/goodmorals";
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
                                JSONObject goodMoralObject = dataArray.getJSONObject(i);

                                int goodMoralId = goodMoralObject.getInt("id");
                                String description = goodMoralObject.getString("description");
                                String status = goodMoralObject.getString("status");
                                int studentId = goodMoralObject.getInt("student_id");
                                String scheduleDate = goodMoralObject.getString("schedule_date");

                                goodMoralIdTextView.setText(String.valueOf(goodMoralId));
                                descriptionTextView.setText(description);
                                statusTextView.setText(status);
                                studentIdTextView.setText(String.valueOf(studentId));
                                scheduleDateTextView.setText(scheduleDate);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("API Request", "Failed to parse JSON response.");
                            descriptionTextView.setText("Failed to parse JSON response.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        descriptionTextView.setText("Failed to retrieve data. Error: " + error.getMessage());
                        Log.e("API Request", "Failed to retrieve data. Error: " + error.getMessage());

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            handleErrorResponse(error);
                        } else {
                            Log.e("API Request", "Empty or null network response");
                        }
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

    private void handleErrorResponse(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            Log.e("API Request", "Error response: " + responseBody);

            try {
                JSONObject errorObject = new JSONObject(responseBody);
                JSONArray errorsArray = errorObject.getJSONArray("errors");

                for (int i = 0; i < errorsArray.length(); i++) {
                    JSONObject errorItem = errorsArray.getJSONObject(i);
                    String fieldName = errorItem.getString("field");
                    String errorMessage = errorItem.getString("message");
                    Log.e("API Request", "Validation error - Field: " + fieldName + ", Message: " + errorMessage);
                }
            } catch (JSONException e) {
                Log.e("API Request", "Error parsing JSON response: " + e.getMessage());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("API Request", "Unsupported Encoding: " + e.getMessage());
        }
    }

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }

    private void addNewRequest() {
        String apiUrl = "http://192.168.100.117:8000/api/goodmorals/create";
        String accessToken = retrieveAccessToken();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("description", "Good Moral");
            requestData.put("status", "YourNewStatus");
            requestData.put("schedule_date", "YourNewScheduleDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apiUrl,
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                getDataFromApi();
                            } else {
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GoodMoralRequest.this, "Failed to add new request", Toast.LENGTH_SHORT).show();
                        Log.e("API Request", "Failed to add new request. Error: " + error.getMessage());

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            handleErrorResponse(error);
                        } else {
                            Log.e("API Request", "Empty or null network response");
                        }
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
    private void deleteExistingRequest() {
        String apiUrl = "http://192.168.100.117:8000/api/goodmorals/delete";

        String accessToken = retrieveAccessToken();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                clearDisplay();
                            } else {
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GoodMoralRequest.this, "Failed to delete record", Toast.LENGTH_SHORT).show();
                        Log.e("API Request", "Failed to delete record. Error: " + error.getMessage());

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            handleErrorResponse(error);
                        } else {
                            Log.e("API Request", "Empty or null network response");
                        }
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

    private void clearDisplay() {
        goodMoralIdTextView.setText("");
        descriptionTextView.setText("");
        statusTextView.setText("");
        studentIdTextView.setText("");
        scheduleDateTextView.setText("");
    }

}
