package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadMoreActivity extends AppCompatActivity {

    private static final String TAG = ReadMoreActivity.class.getSimpleName();
    private String announcementId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more);

        TextView announcementContentTextView = findViewById(R.id.announcementContentTextView);
        TextView commentsTextView = findViewById(R.id.commentsTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("announcementContent")) {
            String announcementContent = intent.getStringExtra("announcementContent");
            Log.d(TAG, "Received announcement content: " + announcementContent);
            announcementContentTextView.setText(announcementContent);

            if (intent.hasExtra("announcementId")) {
                announcementId = intent.getStringExtra("announcementId");
                Log.d(TAG, "Announcement ID: " + announcementId);
                fetchComments(commentsTextView);
            } else {
                Log.e(TAG, "No ID found in the intent");
                commentsTextView.setText("Error: No ID found for comments");
            }
        } else {
            Log.e(TAG, "No announcement content found in the intent");
        }

        // Add Comment Section
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextComment = findViewById(R.id.editTextComment);
        Button btnSaveComment = findViewById(R.id.btnSaveComment);

        btnSaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve values from the EditText fields
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String comment = editTextComment.getText().toString();

                // Implement your logic to save the comment (e.g., send a request to your server)
                saveComment(name, email, comment);
            }
        });
    }

    private void fetchComments(final TextView commentsTextView) {
        String url = "http://192.168.100.117:8000/api/comments/" + announcementId;

        // Retrieve access token from shared preferences
        String token = retrieveAccessToken();

        if (token != null) {
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                // Assuming your comments are directly in the JSON array
                                List<String> comments = new ArrayList<>();

                                // Parse the JSON array and extract comment information
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject commentObj = response.getJSONObject(i);
                                    String commentContent = commentObj.getString("comment");
                                    String commenterName = commentObj.getString("name");
                                    comments.add(commenterName + ": " + commentContent);
                                }

                                // Display comments in the TextView
                                commentsTextView.setText("Comments:\n" + String.join("\n", comments));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error parsing JSON response");
                                commentsTextView.setText("Error: Unable to parse comments");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error fetching comments: " + error.toString());
                            commentsTextView.setText("Error: Unable to fetch comments");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(request);
        } else {
            // Handle the case where the token is not available
            Log.e(TAG, "Access token not available");
            commentsTextView.setText("Error: Access token not available");
        }
    }

    private void saveComment(String name, String email, String comment) {
        String url = "http://192.168.100.117:8000/api/announcements/" + announcementId + "/comments";

        // Retrieve access token from shared preferences
        String token = retrieveAccessToken();

        if (token != null) {
            // Create a JSONObject with the comment data
            JSONObject commentData = new JSONObject();
            try {
                commentData.put("name", name);
                commentData.put("email", email);
                commentData.put("comment", comment);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Create a JsonObjectRequest to send the comment data to the server
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    commentData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle the response from the server
                            // You can update the UI or take any other actions
                            Log.d(TAG, "Comment saved successfully");
                            // Clear the text fields
                            EditText editTextName = findViewById(R.id.editTextName);
                            EditText editTextEmail = findViewById(R.id.editTextEmail);
                            EditText editTextComment = findViewById(R.id.editTextComment);

                            editTextName.setText("");
                            editTextEmail.setText("");
                            editTextComment.setText("");
                            // Refresh the comments after saving
                            fetchComments((TextView) findViewById(R.id.commentsTextView));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error saving comment: " + error.toString());
                            // Handle the error (e.g., show an error message)
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(request);
        } else {
            // Handle the case where the token is not available
            Log.e(TAG, "Access token not available");
            // Show an error message or take appropriate action
        }
    }

    // Retrieve access token from shared preferences
    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }
}
