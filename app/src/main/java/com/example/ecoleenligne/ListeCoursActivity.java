package com.example.ecoleenligne;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.CourseListAdapter;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView;

public class ListeCoursActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    List<String> cours = new ArrayList<String>();
    List<String> liens = new ArrayList<>();
    List<String> id = new ArrayList<>();
    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedpreferences;

    private CourseListAdapter courseListAdapter;
    ListView listview;
    List<Course> courses = new ArrayList<Course>();

    int[] img ={R.drawable.pdf,R.drawable.pdf,R.drawable.pdf};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course);
        sqLiteHelper= new SQLiteHelper(this);


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
        if(user_profile_data.equals("ROLE_PARENT")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_courses).setVisible(false);
            menu.findItem(R.id.nav_recommendation).setVisible(false);
            menu.findItem(R.id.nav_exercices).setVisible(false);
        }


        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        /*------------------------ List of courses  ---------------------*/
        this.courseListAdapter = new CourseListAdapter(this, courses);
        ListView studentsListView = findViewById(R.id.list_students);
        studentsListView.setAdapter(courseListAdapter);

        /*--------------retrieve data from customer student listView --------------*/
       /*
        studentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // selected item
                String user_selected =((TextView)view.findViewById(R.id.user_id)).getText().toString();
                //Intent intent=new Intent(ListeCoursActivity.this, StudentDetailActivity.class);
                //intent.putExtra("user_id", user_selected);
                //context.startActivity(intent);
            }

        });
        */

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/

            /*--------------get user from session --------------*/
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            /*-------------- call restful webservices ----------*/
            String url =  MainActivity.IP+"/api/cours/"+ user_connected;
            /*-------------------- get courses from server RESTful ------------------*/
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() != 0) {
                                    courses = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String id = item.getString("id");
                                        JSONObject SubjectJsonObject = item.getJSONObject("subject");
                                        String name = SubjectJsonObject.getString("name");
                                        String description = item.getString("description");
                                        //String content = item.getString("content");
                                        courses.add(new Course(Integer.parseInt(id), name, description));
                                        //for(Course c: courses)
                                        //Toast.makeText(context, "ddddddd "+ c.getName(), Toast.LENGTH_SHORT).show();

                                    }
                                    courseListAdapter.setCourses(courses);
                                }

                            } catch (JSONException error) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.toString())
                                        .show();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage(error.getMessage())
                                    .show();
                        }
                    }
            );
            queueUserConnected.add(requestUserConnected);
               /* if(students.isEmpty()){
                    empty_students_msg.setText(R.string.empty_students_msg);
                }
                */

        } else  {

            User useConnected= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(user_connected_id));
            String fullname_data = useConnected.getFirstname()+" "+useConnected.getLastname();
            int compte_id = useConnected.getCompte_id();
            //Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            //int profile_id = compte.getProfile_id();
            //Profile profile = sqLiteHelper.getProfileByIdFromDb(profile_id);
            //String profile_role_data = profile.getLibelle();

            //user_name.setText(fullname_data);
            //user_profile.setText(profile_role_data);


            /*--------------get user from session --------------*/
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            /*
            List<User> list_users_sqlite= sqLiteHelper.getUserByParentFromDb(Integer.parseInt(user_connected));
            courses = new ArrayList<>(list_users_sqlite.size());
            for(User user : list_users_sqlite){
                int id = user.getId();
                String firstname = user.getFirstname();
                String lastname = user.getLastname();
                String level = user.getLevel();
                courses.add(new User(id, firstname, lastname, level));
            }
            courseListAdapter.setCourses(courses);
             */
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
                    Intent intent=new Intent(ListeCoursActivity.this, LoginActivity.class);
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
                        Intent intent=new Intent(ListeCoursActivity.this, LoginActivity.class);
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
                Intent intent=new Intent(ListeCoursActivity.this, LoginActivity.class);
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
            case R.id.nav_exercices:
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent_exercices = new Intent(this, ListeCoursExerciceActivity.class);
                    startActivity(intent_exercices);
                } else {
                    Toast toast = Toast.makeText(this, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
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
                if(MainActivity.MODE.equals("ONLINE")) {
                    Intent intent_payment = new Intent(this, PayementActivity.class);
                    startActivity(intent_payment);
                } else {
                    Toast toast = Toast.makeText(this, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.parseColor("#ff0040"));
                    toast.show();
                }
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