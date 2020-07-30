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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Kinship;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.KinshipListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChildActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    private KinshipListAdapter kinshipListAdapter;
    ListView listView;
    List<Kinship> kinships = new ArrayList<Kinship>();

    String email_data;

   // Popup dialog
    Button positive, negative;
    Dialog dialog, dialog_child_form;
    ImageView closePoppupPositiveImg, closePoppupNegativeImg;
    TextView positive_title, positive_content, negative_title, negative_content, formChildError;
    Button positive_button, negative_button;
    ImageButton child_add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

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

        this.kinshipListAdapter = new KinshipListAdapter(this, kinships);
        ListView listView = findViewById(R.id.list_students);
        listView.setAdapter(kinshipListAdapter);

        //get list of children from server
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String url = MainActivity.IP + "/api/tutor/child/list/"+user_connected_id;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        /*--------------- allow connection with https and ssl-------------*/
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String emailResponse = response.getString("email");
                    String tutor_id = response.getString("id");
                    String tutor_firstName = response.getString("firstName");
                    String tutor_lastName = response.getString("lastName");
                    User tutor = new User(Integer.parseInt(tutor_id), tutor_firstName, tutor_lastName);

                    kinships = new ArrayList<>(response.getJSONArray("kinshipStudents").length());
                    for (int j = 0; j < response.getJSONArray("kinshipStudents").length(); j++) {
                        JSONObject kindshipJsonObject = response.getJSONArray("kinshipStudents").getJSONObject(j);
                        String kinship_id = kindshipJsonObject.getString("id");

                        JSONObject studentJson = kindshipJsonObject.getJSONObject("student");
                        String student_id = studentJson.getString("id");
                        String student_firstName = studentJson.getString("firstName");
                        String student_lastName = studentJson.getString("lastName");
                        String student_email = studentJson.getString("email");
                        String student_image = studentJson.getString("image");

                        User student= new User();
                        if(response.getJSONArray("kinshipStudents").length()>0) {
                            JSONObject studentSubscription = studentJson.getJSONArray("subscriptions").getJSONObject(0);
                            JSONObject levelJson = studentSubscription.getJSONObject("level");
                            String student_level = levelJson.getString("name");
                            student = new User(Integer.parseInt(student_id), student_firstName, student_lastName, student_email, student_level, student_image);
                        } else {
                            student = new User(Integer.parseInt(student_id), student_firstName, student_lastName, student_email);
                        }

                        kinships.add(new Kinship(Integer.parseInt(kinship_id), tutor, student));
                    }

                    dialog = new Dialog(context);
                    if(kinships.isEmpty()){
                        actionNegative();
                        negative_title = dialog.findViewById(R.id.negative_title);
                        negative_content = dialog.findViewById(R.id.negative_content);
                        negative_button = dialog.findViewById(R.id.negative_button);
                        negative_button.setOnClickListener(v -> {
                            dialog.dismiss();
                        });
                        negative_title.setText(R.string.parent_children);
                        negative_content.setText(R.string.parent_child_not_found);
                        negative_button.setText(R.string.button_ok);
                    }
                    kinshipListAdapter.setKinships(kinships);
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
                        .setTitle("Error")
                        .setMessage(e.toString())
                        .show();
            }
        });
        queue.add(request);

        //Add child dialog popup
        dialog_child_form = new Dialog(context);
        dialog_child_form.setContentView(R.layout.popup_positive_child_add);
        Button child_add_btn = findViewById(R.id.child_add_btn);
        child_add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*dialog_child_form.findViewById(R.id.positive_close).setOnClickListener(vv -> dialog_child_form.dismiss());
                Objects.requireNonNull(dialog_child_form.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_child_form.show();
                 */
                Intent intent = new Intent(context, StudentAddActivity.class);
                context.startActivity(intent);
            }
        });

        //Add child dialog form
        formChildError = dialog_child_form.findViewById(R.id.formChildError);
        final Button child_add_form_btn = dialog_child_form.findViewById(R.id.add_form_button);
        child_add_form_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog_child_form.findViewById(R.id.positive_close).setOnClickListener(v2 -> dialog_child_form.dismiss());

                Button add_form_button = dialog_child_form.findViewById(R.id.add_form_button);
                add_form_button.setOnClickListener(v3 -> {
                    TextInputLayout emailInput = (TextInputLayout) dialog_child_form.findViewById(R.id.email);
                    String email = emailInput.getEditText().getText().toString();
                    if(!validateEmail()){
                        return;
                    } else {
                        addChild(email);
                    }
                });

                Objects.requireNonNull(dialog_child_form.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_child_form.show();
            }
        });

    }

    private Boolean validateEmail() {
        TextInputLayout emailInput = (TextInputLayout) dialog_child_form.findViewById(R.id.email);
        email_data = emailInput.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email_data.isEmpty()) {
            emailInput.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!email_data.matches(emailPattern)) {
            emailInput.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            emailInput.setError(null);
            emailInput.setErrorEnabled(false);
            return true;
        }
    }


    /*------------------ Authentification (RESTful) ----------------*/
    public void addChild(String email) {
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
            String url = MainActivity.IP + "/api/tutor/add/child/"+ email +"/"+user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        String emailResponse = response.getString("email");
                        if (emailResponse.equals("not found")) {
                            formChildError.setText("Le compte étudiant associé à cette adresse mail n'existe pas");
                        } else if(emailResponse.equals("already your child")){
                            formChildError.setText("Vous avez déjà associé ce compte étudiant");
                        } else if(emailResponse.equals("already associated")){
                            formChildError.setText("Ce compte étudiant a déjà été associé à un compte parent");
                        } else {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
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
                            .setTitle("Error")
                            .setMessage(e.toString())
                            .show();
                }
            });
            queue.add(request);
    }



    private void actionPositive() {
        dialog.setContentView(R.layout.popup_positive);
        closePoppupPositiveImg = dialog.findViewById(R.id.positive_close);
        closePoppupPositiveImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void actionNegative() {
        dialog.setContentView(R.layout.popup_negative);
        closePoppupNegativeImg = dialog.findViewById(R.id.negative_close);
        closePoppupNegativeImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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