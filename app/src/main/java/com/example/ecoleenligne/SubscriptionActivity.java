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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Subscription;
import com.example.ecoleenligne.util.SubjectListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubscriptionActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferences sharedpreferences;

    TextView user_name, user_profile;
    //student avatar
    ImageView image;
    private SubjectListAdapter subjectListAdapter;
    ListView listview;
    List<Subject> subjects = new ArrayList<Subject>();

    //dialog
    Dialog dialog, dialog_success;
    ImageView closePoppupNegativeImg, closePoppupPositiveImg;
    TextView negative_title, negative_content, negative_title_big, positive_title, positive_content, positive_title_big;
    Button negative_button, positive_button;

    /*
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Spinner student_level_spinner, student_matiere_spinner, student_duration_spinner;
    String level_data, matiere_data, duration_data;
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.subscribe_title);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);
        if(user_profile_data.equals("ROLE_TUTOR"))
            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.linkedin)));
        else
            getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.primary)));

        //Transparent statusbar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        user_name = findViewById(R.id.user_name);
        user_profile = findViewById(R.id.user_profile);
        //image upload
        image = (ImageView)findViewById(R.id.image);

        SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String parent_student_selected = sharedpreferences.getString("parent_student_selected", null);
        String parent_student_level = sharedpreferences.getString("parent_student_level", null);
        String parent_student_subscription_type = sharedpreferences.getString("parent_student_subscription_type", null);


        this.subjectListAdapter = new SubjectListAdapter(context, subjects);
        ListView subjectsListView = findViewById(R.id.list_subjects);
        subjectsListView.setAdapter(subjectListAdapter);

        //get list of courses for each selected subject
        subjectsListView.setOnItemClickListener((parent, view1, position, id) -> {
            String subject_id = ((TextView) view1.findViewById(R.id.subject_id)).getText().toString();
            String subject_name = ((TextView) view1.findViewById(R.id.name)).getText().toString();

            // check if there is connection-
            if(MainActivity.MODE.equals("ONLINE")) {
                // get courses from server RESTful
                //String url =  MainActivity.IP+"/api/cours/"+ user_connected;
                String url =  MainActivity.IP+"/api/subscription/student/check/"+ parent_student_selected +"/"+ subject_id;
                Toast.makeText(SubscriptionActivity.this,"student= "+ url+ " subject= "+subject_id,Toast.LENGTH_SHORT);
                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                        response -> {
                                if (response.length() != 0) {
                                    //  dialog student already subscribed
                                    dialog = new Dialog(context);
                                    actionNegative();
                                    negative_title = dialog.findViewById(R.id.negative_title);
                                    negative_content = dialog.findViewById(R.id.negative_content);
                                    negative_title_big = dialog.findViewById(R.id.negative_title_big);
                                    negative_button = dialog.findViewById(R.id.negative_button);
                                    negative_button.setOnClickListener(v -> {
                                        String url1 = MainActivity.IP + "/api/subscription/parent/student/unsubscribe/" + parent_student_selected + "/" + subject_id;
                                        // delete subscriptions from server through RESTful webservice
                                        RequestQueue queueUnsub = Volley.newRequestQueue(context);
                                        HttpsTrustManager.allowAllSSL();
                                        JsonArrayRequest requestUnsub = new JsonArrayRequest(Request.Method.GET, url1, null,
                                                response12 -> {
                                                    Toast.makeText(context, " You are successfully unscribed", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                },
                                                error -> new AlertDialog.Builder(context).setTitle("Error").setMessage(error.getMessage()).show()
                                        );
                                        queueUnsub.add(requestUnsub);

                                    });
                                    negative_title.setText(subject_name);
                                    negative_content.setText(R.string.subscribtion_already);
                                    negative_title_big.setText(R.string.subscribtion_unsubscribe);
                                    negative_button.setText(R.string.parent_child_unsubscribe);
                                } else {
                                    //confirm subscrition dialog
                                    dialog_success = new Dialog(context);
                                    actionPositive();
                                    positive_title = dialog_success.findViewById(R.id.positive_title);
                                    positive_content = dialog_success.findViewById(R.id.positive_content);
                                    positive_title_big = dialog_success.findViewById(R.id.positive_title_big);
                                    positive_button = dialog_success.findViewById(R.id.positive_button);
                                    final int[] done = {0};
                                    positive_button.setOnClickListener(v -> {
                                        if(done[0] == 0) {
                                            done[0] = 1;
                                            String url1 = MainActivity.IP + "/api/subscription/parent/student/done/" + parent_student_selected + "/" + parent_student_level + "/" + parent_student_subscription_type + "/" + subject_id;
                                            RequestQueue queue = Volley.newRequestQueue(context);
                                            JSONObject jsonObject = new JSONObject();
                                            // allow connection with https and ssl
                                            HttpsTrustManager.allowAllSSL();
                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, jsonObject, response1 -> {
                                                positive_title.setText(subject_name);
                                                positive_content.setText(R.string.subscribe_done);
                                                positive_title_big.setText("");
                                                positive_button.setText(R.string.button_ok);
                                            }, e -> new AlertDialog.Builder(context).setTitle("Error").setMessage(e.toString()).show());

                                            queue.add(request);
                                        } else {
                                            dialog_success.cancel();
                                        }

                                    });
                                    positive_title.setText(subject_name);
                                    positive_content.setText(R.string.subscribtion_confirm);

                                    int months = 0;
                                    double priceReduction = 0;
                                    switch (parent_student_subscription_type) {
                                        case "Trimestre":
                                            months = 3;
                                            priceReduction = 1;
                                            break;
                                        case "Semestre":
                                            months = 6;
                                            priceReduction = 0.9;
                                            break;
                                        case "Année":
                                            months = 12;
                                            priceReduction = 0.8;
                                            break;
                                    }

                                    double totalPrice = priceReduction * months * 7;

                                    DecimalFormat df = new DecimalFormat("#.####");

                                    positive_title_big.setText(getString(R.string.subscribtion_price)+" " + parent_student_subscription_type +" = "+ df.format(totalPrice));
                                    positive_button.setText(R.string.button_confirm);
                                }
                        },
                        error -> new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(error.getMessage())
                                .show()
                );
                queueUserConnected.add(requestUserConnected);

            } else  {}

        });




        //check if there is connection
        if(MainActivity.MODE.equals("ONLINE")) {
            // get user from session
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            // call restful webservices
            String url =  MainActivity.IP+"/api/subject/list";
            //get list of subject
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            // allow connection with https and ssl
            HttpsTrustManager.allowAllSSL();
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() != 0) {
                                    subjects = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String id = item.getString("id");
                                        String name = item.getString("name");
                                        String icon = item.getString("icon");
                                        //String content = item.getString("content");
                                        subjects.add(new Subject(Integer.parseInt(id), name, icon));
                                    }
                                    subjectListAdapter.setSubjects(subjects);
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


        /*
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
        */


        /*
        // subscription level
        student_level_spinner = (Spinner) findViewById(R.id.student_level_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_level_spinner.setAdapter(adapter);
        student_level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level_data = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), level_data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // subscription material
        student_matiere_spinner = (Spinner) findViewById(R.id.student_matiere_spinner);
        ArrayAdapter<CharSequence> adapterMatier = ArrayAdapter.createFromResource(this, R.array.matiere_array, android.R.layout.simple_spinner_item);
        adapterMatier.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_matiere_spinner.setAdapter(adapterMatier);
        student_matiere_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                matiere_data = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), matiere_data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // subscription duration
        student_duration_spinner = (Spinner) findViewById(R.id.student_duration_spinner);
        ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(this, R.array.duration_array, android.R.layout.simple_spinner_item);
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_duration_spinner.setAdapter(adapterDuration);
        student_duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                duration_data = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), duration_data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // get user from session
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        final String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        //final String url = MainActivity.IP+"/api/subscription/new/"+ user_connected_id+"/"+ level_data +"/"+ matiere_data +"/"+ duration_data;


        final Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = MainActivity.IP+"/api/subscription/new/"+ user_connected_id+"/"+ level_data +"/"+ matiere_data +"/"+ duration_data;
                //Toast.makeText(context, url, Toast.LENGTH_LONG).show();
                RequestQueue queue = Volley.newRequestQueue(context);
                JSONObject jsonObject = new JSONObject();
                HttpsTrustManager.allowAllSSL();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int subscription_id = response.getInt("id");
                            if(subscription_id == 0) {
                                Toast.makeText(context, getString(R.string.register_user_alreadyexist), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, getString(R.string.subscribe_done), Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(SubscriptionActivity.this, DashboardActivity.class);
                                context.startActivity(intent);
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
                    public void onErrorResponse(VolleyError e) {
                        new AlertDialog.Builder(context)
                                .setTitle("###Error")
                                .setMessage(e.toString())
                                .show();
                    }
                });

                queue.add(request);

            }
        });
         */

    }

    private void actionNegative() {
        dialog.setContentView(R.layout.popup_negative);
        closePoppupNegativeImg = dialog.findViewById(R.id.negative_close);
        closePoppupNegativeImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void actionPositive() {
        dialog_success.setContentView(R.layout.popup_positive);
        closePoppupPositiveImg = dialog_success.findViewById(R.id.positive_close);
        closePoppupPositiveImg.setOnClickListener(v -> dialog_success.dismiss());
        Objects.requireNonNull(dialog_success.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_success.show();
    }

    /*
    // Log out function
    public  void logout(){
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String user_connected_login = sharedpreferences.getString(MainActivity.Login, null);

        if(user_connected_id == null && user_connected_login == null) {
            Toast.makeText(context, "You are already disconnected!", Toast.LENGTH_LONG).show();
        }

        // clear session
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(context, getString(R.string.logout_success), Toast.LENGTH_LONG).show();

        Intent intent=new Intent(this, LoginActivity.class);
        context.startActivity(intent);

    }

    // Menu actions
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
    */
}



