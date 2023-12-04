package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    private static final String API_URL_PROFILE_UPDATE = "http://192.168.100.117:8000/api/update";
    private ImageView imgGallery;
    private final int GALLERY_REQ_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgGallery = findViewById(R.id.profileImageView);
        Button btnGallery = findViewById(R.id.pickImageButton);
        Button btnSaveProfile = findViewById(R.id.updateProfileButton);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGalleryPicker();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void launchGalleryPicker() {
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(iGallery, GALLERY_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                imgGallery.setImageURI(data.getData());
            }
        }
    }

    private void updateProfile() {
        String studentFName = getFStudentName();
        String studentLName = getLStudentName();

        if (studentFName.isEmpty() || studentLName.isEmpty()) {
            Toast.makeText(this, "Please enter both first and last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imgGallery.getDrawable() == null) {
            updateProfileInfo(studentFName, studentLName, null);
        } else {
            String base64Image = getImageBase64();
            updateProfileInfo(studentFName, studentLName, base64Image);
        }
    }

    private void updateProfileInfo(String fname, String lname, @Nullable String base64Image) {
        JSONObject profileUpdate = new JSONObject();
        try {
            profileUpdate.put("fname", fname);
            profileUpdate.put("lname", lname);

            if (base64Image != null) {
                profileUpdate.put("uploads", base64Image);
            }
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
                            } catch (UnsupportedEncodingException e) {
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private String getFStudentName() {
        EditText studentFNameEditText = findViewById(R.id.fnameEditText);
        return studentFNameEditText.getText().toString();
    }

    private String getLStudentName() {
        EditText studentLNameEditText = findViewById(R.id.lnameEditText);
        return studentLNameEditText.getText().toString();
    }

    private String getImageBase64() {
        BitmapDrawable drawable = (BitmapDrawable) imgGallery.getDrawable();

        if (drawable == null) {
            Log.e(TAG, "Drawable is null. No image selected.");
            return "";
        }

        Bitmap originalBitmap = drawable.getBitmap();

        int targetWidth = 500;
        int targetHeight = (int) (originalBitmap.getHeight() * (targetWidth / (float) originalBitmap.getWidth()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
        Log.d("AccessToken", "Retrieved Token: " + accessToken);
        return accessToken;
    }
}
