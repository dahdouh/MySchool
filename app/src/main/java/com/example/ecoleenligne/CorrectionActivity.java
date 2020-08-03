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

public class CorrectionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView solution, exercice;
    ImageView solution_image;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        /*------------------------ Menu ---------------------*/
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        navigationView.bringToFront();
        toolbar=findViewById(R.id.toolbar);
        toolbar.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_darawer_open, R.string.navigation_darawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#FF4500"));
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);

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
                    Intent intent = new Intent(context, ListeExercicesActivity.class);
                    intent.putExtra("id", course_id);
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

    /*---------------------- Log out function --------------------------*/
    public  void logout(){
        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String user_connected_login = sharedpreferences.getString(MainActivity.Login, null);

        if(user_connected_id == null && user_connected_login == null) {
            Toast.makeText(context, "You are already disconnected!", Toast.LENGTH_LONG).show();
        }

        /*---------------clear session ------*/
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(context, getString(R.string.logout_success), Toast.LENGTH_LONG).show();

        Intent intent=new Intent(this, LoginActivity.class);
        context.startActivity(intent);

    }

    /*---------------------- Menu actions ---------------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile = sharedpreferences.getString(MainActivity.Role, null);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);

        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Intent intent_profile = new Intent(this, ProfileActivity.class);
                startActivity(intent_profile);
                break;
            case R.id.nav_dashboard:
                Intent intent_dashboard;
                if(user_profile.equals("ROLE_TUTOR")) {
                    intent_dashboard = new Intent(this, DashboardParentActivity.class);
                } else {
                    intent_dashboard = new Intent(this, DashboardActivityCopy.class);
                }
                startActivity(intent_dashboard);
                break;
            case R.id.nav_subscriptions:
                //Intent intent_subscription = new Intent(this, SubscriptionListActivity.class);
                //startActivity(intent_subscription);
                break;
            /*
            case R.id.nav_chat:
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent_chat = new Intent(this, ChatActivity.class);
                    startActivity(intent_chat);
                } else {
                    Toast toast = Toast.makeText(this, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
                break;
                */
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_share:
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg));
                    intentShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    startActivity(Intent.createChooser(intentShare, ""+R.string.share_title));
                } else {
                    Toast toast = Toast.makeText(this, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }

}