package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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
    TextInputLayout password, firstname, lastname, email;
    String password_data, firstname_data, lastname_data, email_data, date_data;
    TextView msg_error;
    DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.navigation_menu_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));
        //getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));
        //Transparent statusbar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        password = findViewById(R.id.login_password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        picker=(DatePicker)findViewById(R.id.datePicker);
        msg_error = findViewById(R.id.msg_error);

        final Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!validateFirstname() || !validateLastname() || !validatePassword() || !validateEmail()){
                    return;
                } else {
                    date_data = picker.getYear()+"-"+ (picker.getMonth() + 1)+"-"+picker.getDayOfMonth();
                    updateStudentRest(email_data, password_data, firstname_data, lastname_data, date_data);
                }
            }
        });

        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        final String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        //online mode
        if(MainActivity.MODE.equals("ONLINE")) {
            /*String url = MainActivity.IP + "/api/profile/" + user_connected_id;
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
                            //tel_data = response.getString("tel");
                            //ville_data = response.getString("ville");
                            //JSONObject compteJsonObject = response.getJSONObject("compte");
                            //login_data = compteJsonObject.getString("login");

                            firstname.getEditText().setText(firstname_data);
                            lastname.getEditText().setText(lastname_data);
                            email.getEditText().setText(email_data);
                            //tel.setText(tel_data);
                            //ville.setText(ville_data);
                            //login.setText(login_data);
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
             */

            String url = MainActivity.IP + "/api/profile/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);

            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                try {
                    String user_exist = response.getString("email");


                    if (user_exist.equals("not found")) {
                        Toast.makeText(context, "=====>  User not exist", Toast.LENGTH_LONG).show();
                    } else {
                        firstname.getEditText().setText(response.getString("firstName"));
                        lastname.getEditText().setText(response.getString("lastName"));
                        email.getEditText().setText(response.getString("email"));
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
                    .setMessage(R.string.server_restful_error)
                    .show());

            queue.add(request);

        } else {
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.putExtra("ToProfile", "1");
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void backStep(View view) {

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra("ToProfile", "1");
        context.startActivity(intent);
    }

    /*------------------ call restful service ----------------*/
    public void updateStudentRest(String email, String password, String firstName, String lastName, String date) {
        String url = MainActivity.IP+"/api/profile/update/"+email+"/"+password+"/"+firstName+"/"+lastName+"/"+date;
        Toast.makeText(context, "eee "+url, Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        // allow connection with https and ssl
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                  Intent intent=new Intent(context, ProfileActivity.class);
                  context.startActivity(intent);

        }, e -> new AlertDialog.Builder(context)
                .setTitle("#Error")
                .setMessage(e.toString())
                .show());

        queue.add(request);

    }

    /*------------------ validate data --------------*/
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
    private Boolean validateFirstname() {
        firstname_data = firstname.getEditText().getText().toString();
        if (firstname_data.isEmpty()) {
            firstname.setError(getString(R.string.register_firstname_validat_empty));
            return false;
        } else {
            firstname.setError(null);
            firstname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateLastname() {
        lastname_data = lastname.getEditText().getText().toString();
        if (lastname_data.isEmpty()) {
            lastname.setError(getString(R.string.register_lastname_validat_empty));
            return false;
        } else {
            lastname.setError(null);
            lastname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateEmail() {
        email_data = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email_data.isEmpty()) {
            email.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!email_data.matches(emailPattern)) {
            email.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

}
