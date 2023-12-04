package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;

public class StudentHome extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private CalendarView calendarView;
    private TextView welcomeTextView;
    private String mJSONURLString = "http://192.168.100.117:8000/api/logout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        welcomeTextView = findViewById(R.id.textViewWelcome);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        Intent dashboardIntent = new Intent(StudentHome.this, DashboardActivity.class);
                        startActivity(dashboardIntent);
                        break;
                    case R.id.nav_profile:
                        Intent profileIntent = new Intent(StudentHome.this, StudentProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.nav_report_violation:
                        // Handle report violation
                        break;
                    case R.id.nav_request_goodmoral:
                        Intent goodmoralrequestIntent = new Intent(StudentHome.this, GoodMoralRequest.class);
                        startActivity(goodmoralrequestIntent);                        break;
                    case R.id.nav_schedule_coaching:
                        // Start CoachingScheduling activity when "Schedule Coaching" is selected
                        Intent coachingScheduleIntent = new Intent(StudentHome.this, CoachingScheduling.class);
                        startActivity(coachingScheduleIntent);
                        break;
                    case R.id.nav_logout:
                        // Logout when "Logout" is selected
                        logout();
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void logout() {
        String accessToken = retrieveAccessToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                mJSONURLString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        runOnUiThread(() -> {
                            Log.d("Logout", "Logout successful");

                            clearAccessToken();
                            startActivity(new Intent(StudentHome.this, LoginActivity.class));
                            finish();
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Logout", "Logout error: " + error.getMessage());
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

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }

    private void clearAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.ACCESS_TOKEN_KEY);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
