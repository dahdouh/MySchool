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
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardParentActivity extends AppCompatActivity implements Response.Listener<JSONArray>, Response.ErrorListener, NavigationView.OnNavigationItemSelectedListener {

    /*------------------ Menu ----------------*/
    final Context context = this;
    SharedPreferences sharedpreferences;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView user_name, user_profile;

    ListView listview;

    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_parent);
        sqLiteHelper= new SQLiteHelper(this);

        user_name = findViewById(R.id.user_name);
        user_profile = findViewById(R.id.user_profile);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

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
        /*------------------- hide items from menu ---------------------*/
        if(user_profile_data.equals("ROLE_TUTOR")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_courses).setVisible(false);
        }




        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        LinearLayout child = (LinearLayout) findViewById(R.id.child);
        LinearLayout subscription = (LinearLayout) findViewById(R.id.subscription);
        LinearLayout lbhist = (LinearLayout) findViewById(R.id.rlhist);
        LinearLayout recommendation = (LinearLayout) findViewById(R.id.recommendation);

        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardParentActivity.this, ChildActivity.class);
                startActivity(intent);
            }
        });

        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra("ToSubscription", "1");
                    intent.putExtra("parent_id", ""+user_connected_id);
                    context.startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(context, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
            }
        });

        lbhist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent = new Intent(DashboardParentActivity.this, HistoricListActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(context, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
            }
        });

        recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardParentActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

















        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/
            String url = MainActivity.IP+"/api/user/"+user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("email");
                        if(user_exist.equals("not found")) {
                            Toast.makeText(context, "=====>  User not found", Toast.LENGTH_LONG).show();
                        } else {
                            String fullname_data = response.getString("firstName")+" "+response.getString("lastName");
                            String role = sharedpreferences.getString(MainActivity.Role, null);
                            user_name.setText(fullname_data);
                            user_profile.setText(role);
                        }

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
                public void onErrorResponse(VolleyError error) {
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(error.toString())
                            .show();
                }
            });
            queue.add(request);
        } else  {
            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(user_connected_id));
            String fullname_data = user.getFirstname()+" "+user.getLastname();
            int compte_id = user.getCompte_id();
            Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            int profile_id = compte.getProfile_id();
            Profile profile = sqLiteHelper.getProfileByIdFromDb(profile_id);
            String profile_role_data = profile.getLibelle();
            user_name.setText(fullname_data);
            user_profile.setText(profile_role_data);
        }

    }

    @Override
    public void onResponse(JSONArray response) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }




    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected = sharedpreferences.getString(MainActivity.Id, null);
        /*
        if(MainActivity.MODE.equals("ONLINE")) {
            String url = MainActivity.IP + "/students/" + user_connected;
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
            queue.add(microPostsRequest);
        }
        */
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
            case R.id.nav_myspace:
                Intent intent_myspace = new Intent(this, MySpaceActivity.class);
                startActivity(intent_myspace);
                break;
            case R.id.nav_dashboard:
                Intent intent_dashboard;
                if(user_profile.equals("ROLE_TUTOR")) {
                    intent_dashboard = new Intent(this, DashboardParentActivity.class);
                } else {
                    intent_dashboard = new Intent(this, DashboardActivity.class);
                }
                startActivity(intent_dashboard);
                break;
            case R.id.nav_courses:
                Intent intent_courses = new Intent(this, ListeCoursActivity.class);
                startActivity(intent_courses);
                break;
            case R.id.nav_subscriptions:
                //Intent intent_subscription = new Intent(this, SubscriptionListActivity.class);
                //startActivity(intent_subscription);
                break;
            case R.id.nav_forum:
                Intent intent_forum = new Intent(this, ForumActivity.class);
                startActivity(intent_forum);
                break;
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
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_share:
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg));
                    intentShare.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
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
