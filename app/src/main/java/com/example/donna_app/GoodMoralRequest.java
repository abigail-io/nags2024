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
    private TextView goodMoralIdTextView; // Added TextView for GoodMoral ID
    private Button buttonAddNewRequest;
    private Button buttonRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodmoral_request);

        // Initialize views
        descriptionTextView = findViewById(R.id.descriptionTextView);
        statusTextView = findViewById(R.id.statusTextView);
        studentIdTextView = findViewById(R.id.studentIdTextView);
        scheduleDateTextView = findViewById(R.id.scheduleDateTextView);
        goodMoralIdTextView = findViewById(R.id.goodMoralIdTextView); // Initialize GoodMoral ID TextView
        buttonAddNewRequest = findViewById(R.id.buttonAddNewRequest);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        // Set click listeners for buttons
        buttonAddNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Add New Request" button click
                // Add your logic to perform actions when the button is clicked
                // For example, you can send a request to your Laravel backend to create a new record

                // Call the method to send the request to create a new record
                addNewRequest();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Refresh" button click
                // Add your logic to refresh the data or perform any other actions
                // For example, you can call getDataFromApi() again to fetch updated data
                deleteExistingRequest();

            }
        });

        // Execute the method to perform the initial API call
        getDataFromApi();
    }

    private void getDataFromApi() {
        // Replace with your Laravel backend URL and endpoint
        String apiUrl = "http://192.168.100.117:8000/api/goodmorals";

        // Retrieve access token from SharedPreferences
        String accessToken = retrieveAccessToken();

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log success message
                        Log.d("API Request", "Success");

                        try {
                            // Get the array from the "data" key
                            JSONArray dataArray = response.getJSONArray("data");

                            // Iterate through the array and get each Good Moral object
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject goodMoralObject = dataArray.getJSONObject(i);

                                int goodMoralId = goodMoralObject.getInt("id");
                                String description = goodMoralObject.getString("description");
                                String status = goodMoralObject.getString("status");
                                int studentId = goodMoralObject.getInt("student_id");
                                String scheduleDate = goodMoralObject.getString("schedule_date");

                                // Display the data in your TextViews
                                goodMoralIdTextView.setText(String.valueOf(goodMoralId)); // Set GoodMoral ID
                                descriptionTextView.setText(description);
                                statusTextView.setText(status);
                                studentIdTextView.setText(String.valueOf(studentId));
                                scheduleDateTextView.setText(scheduleDate);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Log error message
                            Log.e("API Request", "Failed to parse JSON response.");
                            descriptionTextView.setText("Failed to parse JSON response.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        descriptionTextView.setText("Failed to retrieve data. Error: " + error.getMessage());
                        // Log error message
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
                // Add the access token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the queue
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
                    // Handle each error as needed
                }
            } catch (JSONException e) {
                // If parsing as JSON fails, handle the error response as a plain string
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
// Update the addNewRequest method in your Android activity or utility class

    private void addNewRequest() {
        // Replace with your Laravel backend URL and create endpoint
        String apiUrl = "http://192.168.100.117:8000/api/goodmorals/create";

        // Retrieve access token from SharedPreferences
        String accessToken = retrieveAccessToken();

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON object for the new request data
        JSONObject requestData = new JSONObject();
        try {
            // Add the necessary fields for creating a new record
            requestData.put("description", "Good Moral");
            requestData.put("status", "YourNewStatus");
            requestData.put("schedule_date", "YourNewScheduleDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JSON object request for the POST method
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
                                // Log success message or perform any other actions
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                // Optionally, you can refresh the data after adding a new request
                                getDataFromApi();
                            } else {
                                // Handle error response
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
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
                // Add the access token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }
    // Add this method to your Android activity or utility class
    private void deleteExistingRequest() {
        // Replace with your Laravel backend URL and delete endpoint
        String apiUrl = "http://192.168.100.117:8000/api/goodmorals/delete";

        // Retrieve access token from SharedPreferences
        String accessToken = retrieveAccessToken();

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON object request for the DELETE method
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
                                // Log success message or perform any other actions
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                // Clear the display
                                clearDisplay();
                            } else {
                                // Handle error response
                                Toast.makeText(GoodMoralRequest.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
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
                // Add the access token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    // Add this method to your Android activity or utility class
    private void clearDisplay() {
        // Reset TextViews to empty values
        goodMoralIdTextView.setText("");
        descriptionTextView.setText("");
        statusTextView.setText("");
        studentIdTextView.setText("");
        scheduleDateTextView.setText("");
    }

}
