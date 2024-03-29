package com.example.donna_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Context mContext;
    private LoginActivity mActivity;

    private Button mLogin;
    private EditText mEmail;
    private EditText mPassword;
    private Button mGoToEnrollment;
    private Button mGoToAnnouncement; // Added button for navigating to AnnouncementActivity
    private String mJSONURLString = "http://192.168.117.61:8000/api/login";
    private JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        mActivity = LoginActivity.this;
        mLogin = findViewById(R.id.btnLogin);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mGoToEnrollment = findViewById(R.id.btnGoToEnrollment);
        mGoToAnnouncement = findViewById(R.id.btnGoToAnnouncement); // Initialize the button

        mLogin.setOnClickListener(view -> {
            JSONObject jsonItem = new JSONObject();
            try {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                jsonItem.put("email", email);
                jsonItem.put("password", password);

                Log.i("host", mJSONURLString);
                Log.i("LoginActivity", "JSON Data: " + jsonItem.toString());
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);

                jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        mJSONURLString,
                        jsonItem,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String accessToken = response.getString("access_token");
                                    String userRole = response.getString("role");
                                    saveAccessToken(accessToken);

                                    if ("student".equals(userRole)) {
                                        Intent intent = new Intent(LoginActivity.this, StudentHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else if ("parent".equals(userRole)) {
                                        Intent intent = new Intent(LoginActivity.this, ParentHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else if ("teacher".equals(userRole)) {
                                        Intent intent = new Intent(LoginActivity.this, TeacherHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else if ("guidance".equals(userRole)) {
                                        Intent intent = new Intent(LoginActivity.this, GuidanceHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Handle other user roles or show a message
                                        Toast.makeText(mContext, "Login successful!", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle login failure
                                Log.i("laravel", error.getMessage() != null ? error.getMessage() : "Unknown error");
                                Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
                            }
                        }
                );

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mGoToEnrollment.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, EnrollmentActivity.class);
            startActivity(intent);
        });

        // Add a click listener for the button to go to AnnouncementActivity
        mGoToAnnouncement.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, AnnouncementActivity.class);
            startActivity(intent);
        });
    }

    private void saveAccessToken(String accessToken) {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
    }
}
