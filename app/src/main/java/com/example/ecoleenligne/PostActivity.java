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
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Level;
import com.example.ecoleenligne.model.Post;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Subscription;
import com.example.ecoleenligne.model.Topic;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.ForumListAdapter;
import com.example.ecoleenligne.util.PostListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
    /*------------------ Menu ----------------*/
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView textView;
    GridLayout mainGrid;
    TextView user_name, user_profile;
    SharedPreferences sharedpreferences;

    private PostListAdapter postListAdapter;
    ListView listview;
    List<Post> posts = new ArrayList<Post>();
    private EditText editText; //to add new post
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

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
        if(user_profile_data.equals("ROLE_PARENT")) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_courses).setVisible(false);
            menu.findItem(R.id.nav_recommendation).setVisible(false);
            menu.findItem(R.id.nav_exercices).setVisible(false);
        }

        this.postListAdapter = new PostListAdapter(this, posts);
        ListView forumListView = findViewById(R.id.list_students);
        forumListView.setAdapter(postListAdapter);


        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/

            /*--------------get user from session --------------*/
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            final String user_connected = sharedpreferences.getString(MainActivity.Id, null);



            final LinearLayout post_add_layout = (LinearLayout) findViewById(R.id.post_add);
            post_add_layout.setVisibility(View.GONE);

            //Toast.makeText(context, "##### "+ topic_id, Toast.LENGTH_LONG).show();
            final ImageButton post_add_btn = findViewById(R.id.post_add_btn);
            post_add_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    post_add_layout.setVisibility(View.VISIBLE);
                    //Intent intent = new Intent(context, ForumActivity.class);
                    //intent.putExtra("id", topic_id);
                    //context.startActivity(intent);
                    post_add_btn.setVisibility(View.GONE);
                }
            });

            Intent intent = getIntent();
            final int topic_id = intent.getIntExtra("id", -1);
            final String topic_subject = intent.getStringExtra("subject");
            user_name = findViewById(R.id.user_name);
            user_name.setText(topic_subject+"");

            editText = (EditText) findViewById(R.id.editText);
            final ImageButton post_add_done_btn = findViewById(R.id.post_add_done_btn);
            post_add_done_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                   if(!validatePost()) {
                       return;
                   } else{


                           post_add_layout.setVisibility(View.GONE);
                           post_add_btn.setVisibility(View.VISIBLE);
                           String content = editText.getText().toString();
                           String url = MainActivity.IP + "/api/forum/" + topic_id + "/posts/add/" + user_connected + "/" + content;
                           Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
                           RequestQueue queue = Volley.newRequestQueue(context);
                           JSONObject jsonObject = new JSONObject();
                           /*--------------- allow connection with https and ssl-------------*/
                           HttpsTrustManager.allowAllSSL();
                           JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                               @Override
                               public void onResponse(JSONObject response) {
                                   finish();
                                   startActivity(getIntent());
                               }
                           }, new Response.ErrorListener() {
                               @Override
                               public void onErrorResponse(VolleyError e) {
                                   new AlertDialog.Builder(context)
                                           .setTitle("Error")
                                           .setMessage(e.toString())
                                           .show();
                               }
                           });

                           queue.add(request);
                           editText.getText().clear();
                       }

                }
            });

            /*-------------- call restful webservices ----------*/
            String url =  MainActivity.IP+"/api/forum/"+topic_id+"/posts";
            //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
            /*-------------------- get forums from server RESTful ------------------*/
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() != 0) {
                                    posts = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String post_id = item.getString("id");
                                        String post_content = item.getString("content");
                                        String post_date = item.getString("date");

                                        /*JSONObject subjectJsonObject = item.getJSONObject("subject");
                                        String subject_id = subjectJsonObject.getString("id");
                                        String subject_name = subjectJsonObject.getString("name");
                                        Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);

                                         */

                                        JSONObject authorJsonObject = item.getJSONObject("author");
                                        String author_id = authorJsonObject.getString("id");
                                        String author_firstName = authorJsonObject.getString("firstName");
                                        String author_lastName = authorJsonObject.getString("lastName");
                                        User author = new User(Integer.parseInt(author_id), author_firstName, author_lastName);
                                        posts.add(new Post(Integer.parseInt(post_id), post_content, post_date, author));

                                    }
                                    postListAdapter.setPosts(posts);
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

        } else  {

        }


    }

    private Boolean validatePost() {
        String val = editText.getText().toString();
        if (val.isEmpty()) {
            editText.setError(getString(R.string.forum_post_add));
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    /*---------------------- confirm unsubscribe --------------------------*/
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

    /*---------------------- Log out function --------------------------*/
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
                    Intent intent=new Intent(PostActivity.this, LoginActivity.class);
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
        }
        /*---------------clear session ------*/
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(context, getString(R.string.logout_success), Toast.LENGTH_LONG).show();

        Intent intent=new Intent(PostActivity.this, LoginActivity.class);
        context.startActivity(intent);

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
            case R.id.nav_subscriptions:
                Intent intent_subscription = new Intent(this, SubscriptionListActivity.class);
                startActivity(intent_subscription);
                break;
            case R.id.nav_forum:
                Intent intent_forum = new Intent(this, ForumActivity.class);
                startActivity(intent_forum);
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