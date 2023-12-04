package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends AppCompatActivity {

    private ImageView studentImageView;
    private TextView userEmailTextView;
    private TextView studentIdTextView;
    private TextView fnameTextView;
    private TextView lnameTextView;
    private TextView sectionIdTextView;
    private TextView yearIdTextView;

    private TextView courseIdTextView;
    private TextView familyIdTextView;
    private TextView familyLnameTextView;

    private Button editProfileButton;

    private static final String PROFILE_ENDPOINT = "http://192.168.100.117:8000/api/profile";
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        studentImageView = findViewById(R.id.studentImageView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        studentIdTextView = findViewById(R.id.studentIdTextView);
        fnameTextView = findViewById(R.id.fnameTextView);
        lnameTextView = findViewById(R.id.lnameTextView);
        sectionIdTextView = findViewById(R.id.sectionIdTextView);
        yearIdTextView = findViewById(R.id.yearIdTextView);
        courseIdTextView = findViewById(R.id.courseIdTextView);
        familyIdTextView = findViewById(R.id.familyIdTextView);
        familyLnameTextView = findViewById(R.id.familyLnameTextView);

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
        Intent intent = new Intent(this, EditProfileActivity.class);
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
            String studentName = profileData.getString("fname");
            String studentLname = profileData.getString("lname");
            String studentEmail = profileData.getString("email");
            String studentId = profileData.getString("student_id");
            String studentImgUrl = profileData.getString("student_img");
            String sectionId = profileData.getString("sectioname");
            String yearId = profileData.getString("yearname");
            String courseId = profileData.getString("coursename");
            String familyId = profileData.getString("familyfname");
            String familyLname = profileData.getString("familylname");

            userEmailTextView.setText("User Email: " + studentEmail);
            studentIdTextView.setText("Student ID: " + studentId);
            fnameTextView.setText("First Name: " + studentName);
            lnameTextView.setText("Last Name: " + studentLname);
            sectionIdTextView.setText("Section: " + sectionId);
            yearIdTextView.setText("Year Level: " + yearId);
            courseIdTextView.setText("Course: " + courseId);
            familyIdTextView.setText("Family First Name: " + familyId);
            familyLnameTextView.setText("Family Last Name: " + familyLname);

            Glide.with(this)
                    .load(studentImgUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Error loading image: " + e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Image loaded successfully
                            return false;
                        }
                    })
                    .into(studentImageView);

            Log.d("ImageUrl", "Student Image URL: " + studentImgUrl);

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
