package com.example.donna_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementActivity extends AppCompatActivity {

    private static final String TAG = AnnouncementActivity.class.getSimpleName();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private RequestQueue requestQueue;
    private List<String> announcementsList;
    private List<String> announcementIds;  // Added list to store announcement IDs

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, R.layout.custom_list_item, R.id.announcementItemTextView, new ArrayList<String>());
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        fetchAnnouncementData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openReadMoreActivity(position);
            }
        });
    }

    private void fetchAnnouncementData() {
        String url = "http://192.168.100.117:8000/api/announcements";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        announcementsList = parseAnnouncementData(response);
                        adapter.clear();
                        adapter.addAll(announcementsList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + retrieveAccessToken());
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    private List<String> parseAnnouncementData(JSONArray jsonArray) {
        List<String> announcementsList = new ArrayList<>();
        announcementIds = new ArrayList<>();  // Initialize the list

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("content") && jsonObject.has("id")) {
                    String announcementContent = jsonObject.getString("content");
                    String announcementId = jsonObject.getString("id");

                    announcementsList.add(announcementContent);
                    announcementIds.add(announcementId);  // Add ID to the list
                } else {
                    Log.w(TAG, "Key 'content' or 'id' not found in JSON object at index " + i);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
            e.printStackTrace();
        }

        return announcementsList;
    }

    private void openReadMoreActivity(int selectedAnnouncementIndex) {
        if (selectedAnnouncementIndex >= 0 && selectedAnnouncementIndex < announcementsList.size()) {
            String selectedAnnouncementContent = announcementsList.get(selectedAnnouncementIndex);
            String selectedAnnouncementId = announcementIds.get(selectedAnnouncementIndex);  // Use the stored ID

            Log.d(TAG, "Opening ReadMoreActivity with content: " + selectedAnnouncementContent);
            Intent intent = new Intent(this, ReadMoreActivity.class);
            intent.putExtra("announcementContent", selectedAnnouncementContent);
            intent.putExtra("announcementId", selectedAnnouncementId);
            startActivity(intent);
        } else {
            Log.e(TAG, "Invalid selected announcement index: " + selectedAnnouncementIndex);
        }
    }

    private String retrieveAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, null);
    }
}
