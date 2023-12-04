package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class TeacherProfileActivity extends AppCompatActivity {

    private TextView userEmailTextView;
    private TextView fnameTextView;
    private TextView lnameTextView;
    private Button editProfileButton;

    private static final String PROFILE_ENDPOINT = "http://192.168.100.117:8000/api/profileteacher";
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        userEmailTextView = findViewById(R.id.userEmailTextView);
        fnameTextView = findViewById(R.id.fnameTextView);
        lnameTextView = findViewById(R.id.lnameTextView);

        editProfileButton = findViewById(R.id.editProfileButton);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileActivity();
            }
        });

        fetchUserProfile();
    }

    private void openEditProfileActivity() {
        Intent intent = new Intent(this, EditTeacherProfileActivity.class);
        startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fetchUserProfile();
            }
        }
    }

    private void fetchUserProfile() {
        String accessToken = retrieveAccessToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                PROFILE_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FetchProfile", "Profile data received: " + response.toString());
                        showUserProfile(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FetchProfile", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void showUserProfile(JSONObject profileData) {
        try {
            String userEmail = profileData.getString("email");
            String parentName = profileData.getString("fname");
            String parentLname = profileData.getString("lname");

            userEmailTextView.setText("User Email: " + userEmail);
            fnameTextView.setText("First Name: " + parentName);
            lnameTextView.setText("Last Name: " + parentLname);

        } catch (JSONException e) {
            Log.e("ShowProfile", "Error displaying profile data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }
}
