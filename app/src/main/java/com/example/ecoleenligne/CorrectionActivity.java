package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class CorrectionActivity extends AppCompatActivity {

    final Context context = this;
    TextView solution, exercice;
    ImageView solution_image;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.exercice_correction);
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.linkedin)));;

        solution_image = findViewById(R.id.solution_image);
        solution = findViewById(R.id.solution);
        exercice = findViewById(R.id.exercice);



        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/

            /*--------------get user from session --------------*/
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            Intent intent = getIntent();
            int exercice_id = intent.getIntExtra("exercice_id", -1);
            final int course_id = intent.getIntExtra("course_id", -1);


            final Button compris_btn = findViewById(R.id.compris_btn);
            compris_btn.setVisibility(View.GONE);
            compris_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListeCoursActivity.class);
                    //intent.putExtra("id", course_id);
                    context.startActivity(intent);
                }
            });

            solution_image.setVisibility(View.GONE);
            solution.setVisibility(View.GONE);
            final Button correction_btn = findViewById(R.id.correction_btn);
            correction_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    solution_image.setVisibility((solution_image.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                    solution.setVisibility((solution.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
                    compris_btn.setVisibility(View.VISIBLE);
                    correction_btn.setVisibility(View.GONE);
                }
            });




            String url = MainActivity.IP + "/api/exercice/correction/" + exercice_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
            HttpsTrustManager.allowAllSSL();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String exercice_data = response.getString("content");
                        String correction = response.getString("correction");
                        exercice.setText(exercice_data);
                        solution.setText(correction);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.toString())
                                .show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    new AlertDialog.Builder(context)
                            .setTitle("#Error")
                            .setMessage(e.toString())
                            .show();
                }
            });

            queue.add(request);
        }


    }

}