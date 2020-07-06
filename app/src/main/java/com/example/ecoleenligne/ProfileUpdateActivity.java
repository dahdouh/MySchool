package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileUpdateActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferences sharedpreferences;

    TextInputLayout login_textLayout, password_textLayout, firstname_textLayout, lastname_textLayout, email_textLayout, tel_textLayout, ville_textLayout;
    TextInputEditText login, password, firstname, lastname, email, tel, ville;
    String login_data, password_data, firstname_data, lastname_data, email_data, tel_data, ville_data;
    String userAlreadyExist ="";
    TextView msg_error;
    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        sqLiteHelper= new SQLiteHelper(this);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        /*----------- Edit input Layout ---------*/
        login_textLayout = findViewById(R.id.usernameTextLayout);
        password_textLayout = findViewById(R.id.passwordTextLayout);
        firstname_textLayout = findViewById(R.id.firstnameTextLayout);
        lastname_textLayout = findViewById(R.id.lastnameTextLayout);
        email_textLayout = findViewById(R.id.emailTextLayout);
        tel_textLayout = findViewById(R.id.telTextLayout);
        ville_textLayout = findViewById(R.id.villeTextLayout);

        /*----------- Edit input Text ---------*/
        login = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        tel = findViewById(R.id.tel);
        ville = findViewById(R.id.ville);
        msg_error = findViewById(R.id.msg_error);


        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        final String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        /*--------------get selected user from previous listView --------------*/
        Intent intent = getIntent();

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------  get profile from webservice  -----------------*/
            String url = MainActivity.IP + "/profile/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("firstname");


                        if (user_exist.equals("user not exist")) {
                            Toast.makeText(context, "=====>  User not exist", Toast.LENGTH_LONG).show();
                        } else {
                            firstname_data = response.getString("firstname");
                            lastname_data = response.getString("lastname");
                            email_data = response.getString("email");
                            tel_data = response.getString("tel");
                            ville_data = response.getString("ville");
                            JSONObject compteJsonObject = response.getJSONObject("compte");
                            login_data = compteJsonObject.getString("login");

                            firstname.setText(firstname_data);
                            lastname.setText(lastname_data);
                            email.setText(email_data);
                            tel.setText(tel_data);
                            ville.setText(ville_data);
                            login.setText(login_data);
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
            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(user_connected_id));
            firstname_data = user.getFirstname();
            lastname_data = user.getLastname();
            email_data = user.getEmail();
            tel_data = user.getTel();
            ville_data = user.getVille();
            int compte_id = user.getCompte_id();
            Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            login_data = compte.getLogin();
            firstname.setText(firstname_data);
            lastname.setText(lastname_data);
            email.setText(email_data);
            tel.setText(tel_data);
            ville.setText(ville_data);
            login.setText(login_data);
        }

        final Button update_btn = findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!validateFirstname() || !validateLastname() || !validateEmail() || !validateTel() || !validateVille() ||
                        !validateLogin() || !validatePassword()){
                    return;
                } else {
                        Compte compte = new Compte(login_data, password_data);
                        User user = new User(firstname_data, lastname_data, email_data, tel_data, ville_data);
                        user.setId(Integer.parseInt(user_connected_id));
                        if(MainActivity.MODE.equals("ONLINE")) {
                            //call rest webservice
                            updateProfileRest(user, compte);
                        } else {
                            SQLiteHelper.updateUserParent(sqLiteHelper.getDatabase(), user, compte);
                            Intent intent = new Intent(ProfileUpdateActivity.this, ProfileActivity.class);
                            context.startActivity(intent);
                        }
                }
            }
        });
    }


    /*------------------ validate data --------------*/
    private Boolean validateLogin() {
        login_data = login_textLayout.getEditText().getText().toString();
        if (login_data.isEmpty()) {
            login.setError(getString(R.string.register_login_validat_empty));
            return false;
        } else {
            login_textLayout.setError(null);
            login_textLayout.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        password_data = password_textLayout.getEditText().getText().toString();
        if (password_data.isEmpty()) {
            password_textLayout.setError(getString(R.string.register_password_validat_empty));
            return false;
        } else {

            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password_data);
            if(!matcher.matches() && password_data.length()<4) {
                password_textLayout.setError(getString(R.string.register_password_validat_regex));
                return false;
            }else {
                return true;
            }
        }
    }

    private Boolean validateFirstname() {
        firstname_data = firstname_textLayout.getEditText().getText().toString();
        if (firstname_data.isEmpty()) {
            firstname_textLayout.setError(getString(R.string.register_firstname_validat_empty));
            return false;
        } else {
            firstname_textLayout.setError(null);
            firstname_textLayout.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateLastname() {
        lastname_data = lastname_textLayout.getEditText().getText().toString();
        if (lastname_data.isEmpty()) {
            lastname_textLayout.setError(getString(R.string.register_lastname_validat_empty));
            return false;
        } else {
            lastname_textLayout.setError(null);
            lastname_textLayout.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateEmail() {
        email_data = email_textLayout.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email_data.isEmpty()) {
            email_textLayout.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!email_data.matches(emailPattern)) {
            email_textLayout.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            email_textLayout.setError(null);
            email_textLayout.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateTel() {
        tel_data = tel_textLayout.getEditText().getText().toString();
        if (tel_data.isEmpty()) {
            tel_textLayout.setError(getString(R.string.register_tel_validat_empty));
            return false;
        } else {
            tel_textLayout.setError(null);
            tel_textLayout.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateVille() {
        ville_data = ville_textLayout.getEditText().getText().toString();
        if (ville_data.isEmpty()) {
            ville_textLayout.setError(getString(R.string.register_ville_validat_empty));
            return false;
        } else {
            ville_textLayout.setError(null);
            ville_textLayout.setErrorEnabled(false);
            return true;
        }
    }


    /*------------------ update user profile RESTful ----------------*/
    public void updateProfileRest(User user, Compte compte) {
        String url = MainActivity.IP+"/updateProfile/"+user.getId()+"/"+compte.getLogin()+"/"+compte.getPassword()+"/"+user.getFirstname()+"/"+user.getLastname()+"/"+user.getEmail()+"/"+user.getTel()+"/"+user.getVille();
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    userAlreadyExist = response.getString("firstname");
                    if(userAlreadyExist.equals("already exist")) {
                        msg_error.setText(getString(R.string.register_user_alreadyexist));
                        Toast.makeText(context, getString(R.string.register_user_alreadyexist), Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(ProfileUpdateActivity.this, ProfileActivity.class);
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
