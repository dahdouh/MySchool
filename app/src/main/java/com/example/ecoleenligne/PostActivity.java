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
import com.example.ecoleenligne.model.Post;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.PostListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    final Context context = this;
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


        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.navigation_menu_forum);
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.primary)));


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
            final Button post_add_btn = findViewById(R.id.post_add_btn);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra("ToForum", "1");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}