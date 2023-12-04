package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditTeacherProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditTeacherProfile";
    private static final String API_URL_PROFILE_UPDATE = "http://192.168.100.117:8000/api/updateteacher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher_profile);

        Button btnSaveProfile = findViewById(R.id.updateTeacherProfileButton);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        String parentFName = getFParentName();
        String parentLName = getLParentName();

        if (parentFName.isEmpty() || parentLName.isEmpty()) {
            Toast.makeText(this, "Please enter all required information", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProfileInfo(parentFName, parentLName);
    }

    private void updateProfileInfo(String fname, String lname) {
        JSONObject profileUpdate = new JSONObject();
        try {
            profileUpdate.put("fname", fname);
            profileUpdate.put("lname", lname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                API_URL_PROFILE_UPDATE,
                profileUpdate,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            if (status.equals("success")) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error: " + error.toString());

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, "UTF-8");
                                Log.e(TAG, "Error Response: " + errorResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + retrieveAccessToken());
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private String getFParentName() {
        EditText parentFNameEditText = findViewById(R.id.fnameEditText);
        return parentFNameEditText.getText().toString();
    }

    private String getLParentName() {
        EditText parentLNameEditText = findViewById(R.id.lnameEditText);
        return parentLNameEditText.getText().toString();
    }


    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
        Log.d("AccessToken", "Retrieved Token: " + accessToken);
        return accessToken;
    }
}
