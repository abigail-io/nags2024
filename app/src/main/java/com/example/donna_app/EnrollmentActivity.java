package com.example.donna_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnrollmentActivity extends AppCompatActivity {

    private EditText fnameEditText, lnameEditText, mnameEditText, enameEditText,
            schoolYearEditText, birthDateEditText, birthPlaceEditText, lrnEditText, genderEditText,
            ageEditText, addressEditText, lastGradeLevelEditText, lastSchoolAttendedEditText,
            lastSchoolYearEditText, mothersNameEditText, mothersNumEditText, fathersNameEditText,
            fathersNumEditText;

    private Button saveButton;
    private RadioButton yesRadioButton, noRadioButton;
    private LinearLayout additionalFieldsLayout; // Container for additional fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        fnameEditText = findViewById(R.id.fnameEditText);
        lnameEditText = findViewById(R.id.lnameEditText);
        mnameEditText = findViewById(R.id.mnameEditText);
        enameEditText = findViewById(R.id.enameEditText);
        schoolYearEditText = findViewById(R.id.schoolYearEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        birthPlaceEditText = findViewById(R.id.birthPlaceEditText);
        lrnEditText = findViewById(R.id.lrnEditText);
        genderEditText = findViewById(R.id.genderEditText);
        ageEditText = findViewById(R.id.ageEditText);
        addressEditText = findViewById(R.id.addressEditText);
        lastGradeLevelEditText = findViewById(R.id.lastGradeLevelEditText);
        lastSchoolAttendedEditText = findViewById(R.id.lastSchoolAttendedEditText);
        lastSchoolYearEditText = findViewById(R.id.lastSchoolYearEditText);
        mothersNameEditText = findViewById(R.id.mothersNameEditText);
        mothersNumEditText = findViewById(R.id.mothersNumEditText);
        fathersNameEditText = findViewById(R.id.fathersNameEditText);
        fathersNumEditText = findViewById(R.id.fathersNumEditText);

        saveButton = findViewById(R.id.saveButton);
        yesRadioButton = findViewById(R.id.yesRadioButton);
        noRadioButton = findViewById(R.id.noRadioButton);
        additionalFieldsLayout = findViewById(R.id.additionalFieldsLayout);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.yesRadioButton) {
                    additionalFieldsLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.noRadioButton) {
                    additionalFieldsLayout.setVisibility(View.GONE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStudentDetails();
            }
        });
    }

    private void saveStudentDetails() {
        String url = "http://192.168.117.61:8000/api/savenrollment";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fname", fnameEditText.getText().toString());
            jsonObject.put("lname", lnameEditText.getText().toString());
            jsonObject.put("mname", mnameEditText.getText().toString());
            jsonObject.put("ename", enameEditText.getText().toString());
            jsonObject.put("school_year", schoolYearEditText.getText().toString());
            jsonObject.put("birth_date", birthDateEditText.getText().toString());
            jsonObject.put("birth_place", birthPlaceEditText.getText().toString());
            jsonObject.put("lrn", lrnEditText.getText().toString());
            jsonObject.put("gender", genderEditText.getText().toString());
            jsonObject.put("age", ageEditText.getText().toString());
            jsonObject.put("address", addressEditText.getText().toString());

            // Check if additional fields should be included
            if (additionalFieldsLayout.getVisibility() == View.VISIBLE) {
                jsonObject.put("last_grade_level", lastGradeLevelEditText.getText().toString());
                jsonObject.put("last_school_attended", lastSchoolAttendedEditText.getText().toString());
                jsonObject.put("last_school_year", lastSchoolYearEditText.getText().toString());
            }

            jsonObject.put("mothers_name", mothersNameEditText.getText().toString());
            jsonObject.put("mothers_num", mothersNumEditText.getText().toString());
            jsonObject.put("fathers_name", fathersNameEditText.getText().toString());
            jsonObject.put("fathers_num", fathersNumEditText.getText().toString());

            // Add the balikaral field based on radio button selection
            jsonObject.put("balikaral", yesRadioButton.isChecked() ? "Yes" : "No");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        Log.d("EnrollmentActivity", "Response: " + response.toString());

                        try {
                            // Check if the response indicates success
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                // Display a toast for successful save
                                Toast.makeText(EnrollmentActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                // Display a toast for unsuccessful save
                                Toast.makeText(EnrollmentActivity.this, "Save failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("EnrollmentActivity", "Error: " + error.toString());
                        // Display a toast for generic error
                        Toast.makeText(EnrollmentActivity.this, "Save failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
