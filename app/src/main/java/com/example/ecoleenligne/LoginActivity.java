package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Offre;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    final Context context = this;
    Animation topAnimation, bottomAnimation;
    TextView msg_error;
    //TextInputLayout firstname, lastname, email, facebook, tweeter;
    TextInputLayout username, password;
    String username_data, password_data;

    SharedPreferences sharedpreferences;
    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sqLiteHelper= new SQLiteHelper(this);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        /*------------------  ajouter abbomation -----------------*/
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        msg_error = findViewById(R.id.login_msg_error);



       /* firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        facebook = findViewById(R.id.facebook);
        tweeter = findViewById(R.id.tweeter);
        */

        final Button signin_btn = findViewById(R.id.signin_btn);
        signin_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username_data = username.getEditText().getText().toString();
                password_data = password.getEditText().getText().toString();
                if(!validateUsername() || !validatePassword()){
                    return;
                } else {
                    authentification(username_data, password_data);
                }
            }
        });

        /********************* Registration *******************/
        final Button signup_btn = findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ProfileChooseActivity.class);
                context.startActivity(intent);


                /*
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);

                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                }
                 */
            }
        });

        /********************* Forget Password *******************/
        final Button password_btn = findViewById(R.id.password_btn);
        password_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, PassowordForgetActivity.class);
                context.startActivity(intent);
            }
        });
    }

    /*------------------------ Security configuration ---------------------*/
   /*
    public void onBackPressed() {
        //Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String user_connected_login = sharedpreferences.getString(MainActivity.Login, null);

        Intent intent;
        if(user_connected_id == null && user_connected_login == null) {
            intent=new Intent(LoginActivity.this, LoginActivity.class);
        }

    }
    */


    /*------------------ Authentification (RESTful) ----------------*/
    public void authentification(final String username, String password) {

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/

            String url = MainActivity.IP + "/api/login/" + username + "/" + password;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String connected = response.getString("email");
                        if (!connected.equals("not found")) {

                            //String login = compteJsonObject.getString("login");
                            //String passwod = compteJsonObject.getString("password");
                            //String email = response.getString("email");
                            //Toast.makeText(context, "xxxxxxxxxxxxxxxxxx " + login + "????????????????"+passwod + ", ggggg "+email, Toast.LENGTH_LONG).show();
                            Toast.makeText(context, getString(R.string.login_success), Toast.LENGTH_LONG).show();

                            /********************* Profile of user connected *******************/

                            //String profile_role = profileJsonObject.getString("0");

                            String id = response.getString("id");
                            String email = response.getString("email");
                            String firstname = response.getString("firstName");
                            String lastname = response.getString("lastName");

                            JSONArray userRolesArray = response.getJSONArray("roles");
                            String role = userRolesArray.getString(0);

                            //Toast.makeText(context, "xxxxxxxxxxxxxxxxxx " + firstname + "????????????????"+lastname + ", ggggg "+email, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, "###### "+role, Toast.LENGTH_SHORT).show();


                            /*---------------save user connected in session ------*/
                            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(MainActivity.Login, username_data);
                            editor.putString(MainActivity.Password, password_data);
                            editor.putString(MainActivity.Email, email);
                            editor.putString(MainActivity.Id, id);
                            editor.putString(MainActivity.Role, role);
                            editor.commit();

                            if (role.equals("ROLE_ADMIN")) {
                                Toast.makeText(context, "#admiiiin not yet configured", Toast.LENGTH_LONG).show();
                            } else if(role.equals("ROLE_TUTOR")) {
                                Intent intent = new Intent(LoginActivity.this, DashboardParentActivity.class);
                                context.startActivity(intent);
                            } else if (role.equals("ROLE_STUDENT")) {
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                context.startActivity(intent);
                            }

                        } else {
                            Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            msg_error.setText(getString(R.string.login_failed));
                            /*
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                            dlgAlert.setMessage(getString(R.string.login_failed));
                            dlgAlert.setTitle("warning");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                             */
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
        } else {

            Compte compte= sqLiteHelper.getCompteByLoginPassFromDb(username, password);
           if(compte.getLogin() !=null) {
                User user= sqLiteHelper.getUserByIdCompte(compte.getId());
                /*---------------save user connected in session ------*/
                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(MainActivity.Login, username_data);
                editor.putString(MainActivity.Password, password_data);
                editor.putString(MainActivity.Email, user.getEmail());
                editor.putString(MainActivity.Id, user.getId()+"");
                int profile_id = compte.getProfile_id();
                Profile profile = sqLiteHelper.getProfileByIdFromDb(profile_id);
                String profile_role = profile.getRole();
                editor.putString(MainActivity.Role, profile_role);
                editor.commit();

                if (profile_role.equals("ROLE_PARENT")) {
                    Intent intent = new Intent(LoginActivity.this, DashboardParentActivity.class);
                    context.startActivity(intent);
                } else if (profile_role.equals("ROLE_ELEVE")) {
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    context.startActivity(intent);
                }
            } else {
                Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                msg_error.setText(getString(R.string.login_failed));
                /*
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                dlgAlert.setMessage(getString(R.string.login_failed));
                dlgAlert.setTitle("warning");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                 */

            }

        }
    }



    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            username.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!val.matches(emailPattern)) {
            username.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }


    }
    private Boolean validatePassword() {
        password_data = password.getEditText().getText().toString();
        if (password_data.isEmpty()) {
            password.setError(getString(R.string.register_password_validat_empty));
            return false;
        } else {

            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password_data);
            if(!matcher.matches() && password_data.length()<4) {
                password.setError(getString(R.string.register_password_validat_regex));
                return false;
            }else {
                return true;
            }
        }
    }


}
