package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.LogSession;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    SharedPreferences sharedpreferences;
    TextView fullname, role, level, dateLogin, dateLogout;
    String user_selected;
    /*---- Menu ---*/
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        sqLiteHelper= new SQLiteHelper(this);

        /*------------------------ Menu ---------------------*/
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        navigationView.bringToFront();
        toolbar=findViewById(R.id.toolbar);
        toolbar.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_darawer_open, R.string.navigation_darawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);
        /*------------------- hide items from menu ---------------------*/
        if(user_profile_data.equals("ROLE_PARENT")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_courses).setVisible(false);
            menu.findItem(R.id.nav_recommendation).setVisible(false);
        }


        fullname = findViewById(R.id.fullname);
        role = findViewById(R.id.role);
        level = findViewById(R.id.level);
        dateLogin = findViewById(R.id.dateLogin);
        dateLogout = findViewById(R.id.dateLogout);

        /*--------------get selected user from previous listView --------------*/
        Intent intent = getIntent();
        user_selected = intent.getStringExtra("user_id");

        /*--------------------- profile update listener -----------------*/
        ImageView img = (ImageView) findViewById(R.id.edit);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(StudentDetailActivity.this, StudentUpdateActivity.class);
                intent.putExtra("user_id", user_selected);
                context.startActivity(intent);
            }
        });

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------  get profile from server  -----------------*/
            String url = MainActivity.IP + "/profile/" + user_selected;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("firstname");
                        if (user_exist.equals("user not exist")) {
                            Toast.makeText(context, "User not exist", Toast.LENGTH_LONG).show();
                        } else {
                            String fullname_data = response.getString("firstname") + " " + response.getString("lastname");

                            JSONObject compteJsonObject = response.getJSONObject("compte");
                            JSONObject profileJsonObject = compteJsonObject.getJSONObject("profile");
                            String profile_role_data = profileJsonObject.getString("libelle");
                            String level_data = response.getString("level");

                            try {
                                List<LogSession> listLogSession = new ArrayList<>(response.getJSONArray("logSession").length());
                                //for (int i = 0; i < response.getJSONArray("logSession").length(); i++) {
                                /*------------- check if user was never connected -------------*/
                                if (response.getJSONArray("logSession").length() == 0) {
                                    dateLogin.setText(R.string.login_never_connected);
                                    dateLogout.setText("");
                                } else {
                                    JSONObject item = response.getJSONArray("logSession").getJSONObject(response.getJSONArray("logSession").length() - 1);
                                    String id = item.getString("id");
                                    String dateConnexion = item.getString("dateConnexion");
                                    String dateDeconnexion = item.getString("dateDeconnexion");

                                    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss");
                                    //String dateString = sdf.format(dateConnexion);

                                    String[] parts = dateConnexion.split("-");
                                    String yy = parts[0];
                                    String mm = parts[1];
                                    String[] partsTime = parts[2].split(":");
                                    String dd = partsTime[0];
                                    String hh = partsTime[1];
                                    //String mn = partsTime[2];
                                    //String ss = partsTime[3];
                                    dateLogin.setText(yy + "-" + mm + "-" + dd + ":" + hh);
                                    if (dateDeconnexion != null) {
                                        String[] partsDateDeconx = dateDeconnexion.split("-");
                                        yy = partsDateDeconx[0];
                                        mm = partsDateDeconx[1];
                                        String[] partsTimeDateDeconx = partsDateDeconx[2].split(":");
                                        dd = partsTimeDateDeconx[0];
                                        hh = partsTimeDateDeconx[1];
                                        dateLogout.setText(yy + "-" + mm + "-" + dd + ":" + hh);
                                    }


                                    //Toast.makeText(context, "yyyyyyyy " + dateDeconnexion, Toast.LENGTH_SHORT).show();
                                    //String level = item.getString("level");
                                    //listLogSession.add(new LogSession(Integer.parseInt(id), firstname,  level));
                                }
                            } catch (JSONException error) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.toString())
                                        .show();
                            }


                            fullname.setText(fullname_data);
                            role.setText(profile_role_data);
                            level.setText(level_data);
                            //tel.setText(first_login+"");
                            //ville.setText(last_logout+"");
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
                            .setMessage(R.string.server_restful_error)
                            .show();
                }
            });

            queue.add(request);
        } else {

            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(user_selected));

            String fullname_data = user.getFirstname()+" "+user.getLastname();
            int compte_id = user.getCompte_id();
            Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            int profile_id = compte.getProfile_id();
            Profile profile = sqLiteHelper.getProfileByIdFromDb(profile_id);
            String profile_role_data = profile.getLibelle();

            /*
            String[] parts = compte.get.split("-");
            String yy = parts[0];
            String mm = parts[1];
            String[] partsTime = parts[2].split(":");
            String dd = partsTime[0];
            String hh = partsTime[1];
            //String mn = partsTime[2];
            //String ss = partsTime[3];
            dateLogin.setText(yy + "-" + mm + "-" + dd + ":" + hh);
            if (dateDeconnexion != null) {
                String[] partsDateDeconx = dateDeconnexion.split("-");
                yy = partsDateDeconx[0];
                mm = partsDateDeconx[1];
                String[] partsTimeDateDeconx = partsDateDeconx[2].split(":");
                dd = partsTimeDateDeconx[0];
                hh = partsTimeDateDeconx[1];
                dateLogout.setText(yy + "-" + mm + "-" + dd + ":" + hh);
            }
            */

            fullname.setText(fullname_data);
            role.setText(profile_role_data);
            level.setText(user.getLevel());

        }

    }

    /*---------------------- Unsubscribe out function --------------------------*/
    public void confirmUnsubscribe(){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.presence_busy)
                .setTitle("Confirmation")
                .setMessage(R.string.unsubscribe_confirm)
                .setPositiveButton(R.string.unsubscribe_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unsubscribe();
                    }
                })
                .setNegativeButton(R.string.unsubscribe_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public  void unsubscribe(){
        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String user_connected_login = sharedpreferences.getString(MainActivity.Login, null);

        if(user_connected_id == null && user_connected_login == null) {
            Toast.makeText(context, "You must fisrt connect!", Toast.LENGTH_LONG).show();
        } else {
            /*-------------- user unsubscribe ----------*/
            String url = MainActivity.IP+"/unsubscribe/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(context, R.string.unsubscribe_success, Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(StudentDetailActivity.this, LoginActivity.class);
                    context.startActivity(intent);
                    logout();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(R.string.server_restful_error)
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
        } else {
            /*-------------- check if there is connection--------------*/
            if(MainActivity.MODE.equals("ONLINE")) {
                /*-------------- user logout ----------*/
                String url = MainActivity.IP+"/logout/" + user_connected_id;
                RequestQueue queue = Volley.newRequestQueue(context);
                JSONObject jsonObject = new JSONObject();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent=new Intent(StudentDetailActivity.this, LoginActivity.class);
                        context.startActivity(intent);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(R.string.server_restful_error)
                                .show();
                    }
                });
                queue.add(request);
            } else {
                Intent intent=new Intent(StudentDetailActivity.this, LoginActivity.class);
                context.startActivity(intent);
            }

            /*---------------clear session ------*/
            SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Toast.makeText(context, getString(R.string.logout_success), Toast.LENGTH_LONG).show();
        }

    }

    /*---------------------- Menu actions ---------------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile = sharedpreferences.getString(MainActivity.Role, null);

        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Intent intent_profile = new Intent(this, ProfileActivity.class);
                startActivity(intent_profile);
                break;
            case R.id.nav_dashboard:
                Intent intent_dashboard;
                if(user_profile.equals("ROLE_PARENT")) {
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
            case R.id.nav_recommendation:
                Intent intent_recommendation = new Intent(this, RecommendationActivity.class);
                startActivity(intent_recommendation);
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
            case R.id.nav_payment:
                Intent intent_payment = new Intent(this, PayementActivity.class);
                startActivity(intent_payment);
                break;
            case R.id.nav_synchronisation:
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent_synchronisation = new Intent(this, SynchronisationActivity.class);
                    startActivity(intent_synchronisation);
                } else {
                    Toast toast = Toast.makeText(this, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
                break;
            case R.id.nav_unsubscribe:
                if(MainActivity.MODE.equals("ONLINE")) {
                    confirmUnsubscribe();
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
