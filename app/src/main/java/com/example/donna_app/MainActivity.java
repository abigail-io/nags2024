package com.example.donna_app;

import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
//import android.graphics.Bitmap;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import androidx.core.view.WindowCompat;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
import com.example.donna_app.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity  {
    Note mTempNote = new Note();
    private Context mContext;
    private final String mJSONURLString = "http://192.168.52.174:8000/api/item/";
    private final String imgUrl = "http://192.168.52.174:8000/";
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 111;
    private ImageView imageView;
    private String imagePath;

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                if (data != null) {
                    Uri filePath = data.getData();
                    Log.i("file", "file://" + filePath.toString());
                    Log.i("content", filePath.getPath().toString());
                    Log.i("pic", new File(filePath.getPath()).toString());

                    Glide.with(mContext).load(data.getData()).into(imageView);

                }
            }
        });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        Button btnSearch = findViewById(R.id.search);

        EditText desc = findViewById(R.id.description);
        EditText cost = findViewById(R.id.cost);
        EditText sell = findViewById(R.id.sell);
        EditText itemId = findViewById(R.id.item_no);
        EditText imgName =  findViewById(R.id.imageName);

        ImageView imageView  =  findViewById(R.id.imageView);
        Button delete =  findViewById(R.id.btnDelete);
        Button save = findViewById(R.id.save);
        Button buttonChoose = findViewById(R.id.buttonChoose);

        btnSearch.setOnClickListener(view -> {

            String urlString = mJSONURLString+itemId.getText();
            Log.i("url","url"+ urlString);

            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlString,
                null,
                response -> {
                    Log.i("json",String.valueOf(response));
                    try {
                        String description = response.getString("description");
                        String item_cost = response.getString("cost_price");
                        String item_sell = response.getString("sell_price");
                        String image_url = imgUrl + response.getString("img_path");

                        // Display the formatted json data in text view
                        desc.setText(description);
                        cost.setText(item_cost);
                        sell.setText(item_sell);
                        Picasso.get()
                            .load(image_url)
                            .into(imageView);
                        //  }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Do something when error occurred
                    Log.e("error :",error.getMessage());
                });

            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);
        });
        delete.setOnClickListener(view -> {
            // Empty the TextView
            // mTextView.setText("");
            // item_id = itemId.getText().toString();
            //mJSONURLString +=item_id;
            // mJSONURLString = "delete/" + itemId.getText();
            String urlString = mJSONURLString+itemId.getText();
            Log.i("url",urlString);

            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                urlString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with response
                        // Process the JSON
                        try{
                            String status = response.getString("status");
                            Toast.makeText(getApplicationContext(),"Item Deleted", Toast.LENGTH_LONG).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Log.e("error :",error.getMessage());
                    }
                }
            ) ;
            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("url","url"+ mJSONURLString);
                JSONObject jsonItem = new JSONObject();
                try {
                    jsonItem.put("description", desc.getText());
                    jsonItem.put("sell_price", sell.getText());
                    jsonItem.put("cost_price", cost.getText());
                    jsonItem.put("img_path",imgName.getText().toString().trim());
                    jsonItem.put("uploads", getStringImage(bitmap));
                    //Log.i("url","url"+ jsonItem.toString());
                    Log.d("tag", jsonItem.toString(4));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                // Initialize a new RequestQueue instance
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);

                // Initialize a new JsonObjectRequest instance
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    mJSONURLString,
                    jsonItem,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                String status = response.getString("status");
                                Toast.makeText(getApplicationContext(),"Item saved", Toast.LENGTH_LONG).show();
                                String description = response.getString("description");
                                String item_cost = response.getString("cost_price");
                                String item_sell = response.getString("sell_price");
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e("error :","not saved");
                        }
                    });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Add JsonObjectRequest to the RequestQueue
                requestQueue.add(jsonObjectRequest);
            }
        });
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra("return-data", true);

                startActivityIntent.launch(Intent.createChooser(intent, "Select Picture"));
//                Intent galleryIntent= new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityIntent.launch(galleryIntent);
            }

        });


    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void showFileChooser() {
        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent galleryIntent= new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityIntent.launch(galleryIntent);
//        startActivityIntent.launch(Intent.createChooser(intent, "Select Picture"));
    }

    public void createNewNote(Note n){
        // Temporary code
        mTempNote = n;
    }

}




//    private AppBarConfiguration appBarConfiguration;
//    private ActivityMainBinding binding;
//
//    private int value = 0;
//    // A bunch of Buttons and a TextView
//    private Button btnAdd;
//    private Button btnTake;
//    private TextView txtValue;
//    private Button btnGrow;
//    private Button btnShrink;
//    private Button btnReset;
//    private Button btnHide;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_main);
//        setContentView(R.layout.exploration_layout);
//        // Get a reference to all our widgets
//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        RadioButton rbBeijing = (RadioButton) findViewById(R.id.radioButtonBeijing);
//        RadioButton rbLondon = (RadioButton) findViewById
//            (R.id.radioButtonLondon);
//        RadioButton rbnewYork = (RadioButton) findViewById
//            (R.id.radioButtonNewYork);
//        final EditText editText = (EditText) findViewById(R.id.editText);
//        final Button button = (Button) findViewById(R.id.button);
//        final TextClock tClock = (TextClock) findViewById(R.id.textClock);
//        final CheckBox cbTransparency = (CheckBox) findViewById
//            (R.id.checkBoxTransparency);
//        final CheckBox cbTint = (CheckBox) findViewById(R.id.checkBoxTint);
//        final CheckBox cbReSize = (CheckBox) findViewById
//            (R.id.checkBoxReSize);
//        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        Switch switch1 = (Switch) findViewById(R.id.switch1);
//        final WebView webView = (WebView) findViewById(R.id.webView);
//
//        cbTransparency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (cbTransparency.isChecked()) {
//                    // Set some transparency
//                    imageView.setAlpha(.1f);
//                } else {
//                    imageView.setAlpha(1f);
//                }
//            }
//        });
//        cbTint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (cbTint.isChecked()) {
//                    // Checked so set some tint
//                    imageView.setColorFilter(Color.argb(150, 255, 0, 0));
//                } else {
//                    // No tint required
//                    imageView.setColorFilter(Color.argb(0, 0, 0, 0));
//                }
//
//            }
//        });
//
//        cbReSize.setOnCheckedChangeListener
//            (new CompoundButton.OnCheckedChangeListener() {
//                public void onCheckedChanged(CompoundButton buttonView,
//                                             boolean isChecked) {
//                    if (cbReSize.isChecked()) {
//                        // It's checked so make bigger
//                        imageView.setScaleX(2);
//                        imageView.setScaleY(2);
//                    } else {
//                        // It's not checked make regular size
//                        imageView.setScaleX(1);
//                        imageView.setScaleY(1);
//                    }
//                }
//            });
//        // Now for the radio buttons
//        // Uncheck all buttons
//        radioGroup.clearCheck();
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rb = (RadioButton) group.findViewById
//                    (checkedId);
//                switch (rb.getId()) {
//                    case R.id.radioButtonLondon:
//                        tClock.setTimeZone("Europe/London");
//                        break;
//                    case R.id.radioButtonBeijing:
//                        tClock.setTimeZone("CST6CDT");
//                        break;
//                    case R.id.radioButtonNewYork:
//                        tClock.setTimeZone("America/New_York");
//                        break;
//                }// End switch block
//            }
//        });
//        /*
//        Let's listen for clicks on our regular Button.
//        We can do this with an anonymous class as well.
//        */
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // We only handle one button
//                // So no switching required
//                button.setText(editText.getText());
//            }
//        });
//        // Make the webview display a page
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("https://google.com");
//        webView.setVisibility(View.INVISIBLE);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    webView.setVisibility(View.VISIBLE);
//                } else {
//                    webView.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }
//
//    public void createNewNote(Note newNote) {
//    }


    /////////////////////////////////////////////////////////////////////////////////////
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btnAdd = (Button) findViewById(R.id.btnAdd);
//        btnTake = (Button) findViewById(R.id.btnTake);
//        txtValue = (TextView) findViewById(R.id.txtValue);
//        btnGrow = (Button) findViewById(R.id.btnGrow);
//        btnShrink = (Button) findViewById(R.id.btnShrink);
//        btnReset = (Button) findViewById(R.id.btnReset);
//        btnHide = (Button) findViewById(R.id.btnHide);
//
//        btnAdd.setOnClickListener(this);
//        btnTake.setOnClickListener(this);
//        txtValue.setOnClickListener(this);
//        btnGrow.setOnClickListener(this);
//        btnShrink.setOnClickListener(this);
//        btnReset.setOnClickListener(this);
//        btnHide.setOnClickListener(this);
//
//        Log.i("info", "Done creating the app");
//    }
//
//    @Override
//    public void onClick(View view) {
//        float size;
//        switch(view.getId()){
//            case R.id.btnAdd:
//                value ++;
//                txtValue.setText(""+ value);
//                break;
//            case R.id.btnTake:
//                value--;
//                txtValue.setText(""+ value);
//                break;
//            case R.id.btnReset:
//                value = 0;
//                txtValue.setText(""+ value);
//                break;
//            case R.id.btnGrow:
//                size = txtValue.getTextScaleX();
//                txtValue.setTextScaleX(size + 1);
//                break;
//            case R.id.btnShrink:
//                size = txtValue.getTextScaleX();
//                txtValue.setTextScaleX(size - 1);
//                break;
//            case R.id.btnHide:
//                if(txtValue.getVisibility() == View.VISIBLE)
//                {
//                    // Currently visible so hide it
//                    txtValue.setVisibility(View.INVISIBLE);
//                    // Change text on the button
//                    btnHide.setText("SHOW");
//                }else{
//                    // Currently hidden so show it
//                    txtValue.setVisibility(View.VISIBLE);
//                    // Change text on the button
//                    btnHide.setText("HIDE");
//                }
//                break;
//
//        }
//
//    }
//


    ////////////////////////////////////////////////////////////////////////////////////

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//}
