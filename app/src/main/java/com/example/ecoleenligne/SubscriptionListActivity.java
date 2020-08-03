package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Kinship;
import com.example.ecoleenligne.model.Level;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Subscription;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.CourseListAdapter;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.example.ecoleenligne.util.SubscriptionListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedpreferences;

    private SubscriptionListAdapter subscriptionListAdapter;
    ListView listview;
    List<Subscription> subscriptions = new ArrayList<Subscription>();
    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

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

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);
        /*------------------------ Button add subscription ---------------------*/
        final ImageButton student_add_imageButton = findViewById(R.id.student_add_imageButton);
        student_add_imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent=new Intent(SubscriptionListActivity.this, SubscriptionActivity.class);
                context.startActivity(intent);
            }
        });


        /*------------------------ List of subscriptions  ---------------------*/
        this.subscriptionListAdapter = new SubscriptionListAdapter(this, subscriptions);
        ListView subscriptionsListView = findViewById(R.id.list_students);
        subscriptionsListView.setAdapter(subscriptionListAdapter);

        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected = sharedpreferences.getString(MainActivity.Id, null);

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            Intent intent = getIntent();
            final String parent_id = intent.getStringExtra("parent_id");
            // If parent list all his students susbscriptions, otherwise show only student's connected subscriptions
            if(parent_id != null) {
                String url = MainActivity.IP + "/api/tutor/child/list/"+parent_id;
                RequestQueue queue = Volley.newRequestQueue(context);
                JSONObject jsonObject = new JSONObject();
                /*--------------- allow connection with https and ssl-------------*/
                HttpsTrustManager.allowAllSSL();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                    try{

                        List<String> students_ids = new ArrayList<String>();
                        for (int j = 0; j < response.getJSONArray("kinshipStudents").length(); j++) {
                            JSONObject studentJson = response.getJSONArray("kinshipStudents").getJSONObject(j).getJSONObject("student");
                            String student_id = studentJson.getString("id");
                            students_ids.add(student_id);
                        }

                        if(students_ids.size()>0){
                            for (String student_id: students_ids) {
                                //Toast.makeText(context, "########## "+ id, Toast.LENGTH_LONG).show();

                                String url2 = MainActivity.IP + "/api/subscription/list/" + student_id;
                                //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
                                /*-------------------- get subscriptions from server RESTful ------------------*/
                                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                                JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url2, null,
                                        res -> {
                                            try {
                                                if (res.length() != 0) {
                                                    for (int i = 0; i < res.length(); i++) {
                                                        JSONObject item = res.getJSONObject(i);
                                                        String subscription_id = item.getString("id");
                                                        String subscription_price = item.getString("price");

                                                        JSONObject levelJsonObject = item.getJSONObject("level");
                                                        String level_id = levelJsonObject.getString("id");
                                                        String level_name = levelJsonObject.getString("name");
                                                        for (int j = 0; j < item.getJSONArray("subjects").length(); j++) {
                                                            JSONObject subjectJsonObject = item.getJSONArray("subjects").getJSONObject(j);
                                                            String subject_id = subjectJsonObject.getString("id");
                                                            String subject_name = subjectJsonObject.getString("name");
                                                            Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);
                                                            Level level = new Level(Integer.parseInt(level_id), level_name);
                                                            subscriptions.add(new Subscription(Integer.parseInt(subscription_id), subject, subscription_price, level));
                                                        }
                                                    }
                                                    subscriptionListAdapter.setSubscriptions(subscriptions);
                                                }

                                            } catch (JSONException error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(error.toString())
                                                        .show();
                                            }
                                        },
                                        e -> new AlertDialog.Builder(context)
                                                .setTitle("Error")
                                                .setMessage(e.getMessage())
                                                .show()
                                );
                                queueUserConnected.add(requestUserConnected);

                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.toString())
                                .show();
                    }
                }, error -> new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage(error.toString())
                        .show());
                queue.add(request);











            } else {
                /*--------------  If student  ---------------*/
                String url = MainActivity.IP + "/api/subscription/list/" + user_connected;
                //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
                /*-------------------- get subscriptions from server RESTful ------------------*/
                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                if (response.length() != 0) {
                                    subscriptions = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String subscription_id = item.getString("id");
                                        String subscription_price = item.getString("price");

                                        JSONObject levelJsonObject = item.getJSONObject("level");
                                        String level_id = levelJsonObject.getString("id");
                                        String level_name = levelJsonObject.getString("name");
                                        for (int j = 0; j < item.getJSONArray("subjects").length(); j++) {
                                            JSONObject subjectJsonObject = item.getJSONArray("subjects").getJSONObject(j);
                                            String subject_id = subjectJsonObject.getString("id");
                                            String subject_name = subjectJsonObject.getString("name");
                                            Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);
                                            Level level = new Level(Integer.parseInt(level_id), level_name);
                                            subscriptions.add(new Subscription(Integer.parseInt(subscription_id), subject, subscription_price, level));
                                        }
                                    }
                                    subscriptionListAdapter.setSubscriptions(subscriptions);
                                }

                            } catch (JSONException error) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.toString())
                                        .show();
                            }
                        },
                        error -> new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(error.getMessage())
                                .show()
                );
                queueUserConnected.add(requestUserConnected);
            }

        } else  {

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