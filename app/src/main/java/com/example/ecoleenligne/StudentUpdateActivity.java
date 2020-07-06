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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

public class StudentUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final Context context = this;
    SharedPreferences sharedpreferences;
    TextInputLayout login_textLayout, password_textLayout, firstname_textLayout, lastname_textLayout;
    TextInputEditText login, password, firstname, lastname;
    TextView msg_error;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    Spinner student_level_spinner;
    String login_data, password_data, firstname_data, lastname_data, parent_data, level_data;
    String userAlreadyExist ="";

    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_update);

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

        /*----------- Edit input Text ---------*/
        login = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        radioGroup = findViewById(R.id.radioGroup);
        msg_error = findViewById(R.id.msg_error);

        /*------------ Spinner for student's level ------------*/
        student_level_spinner = (Spinner) findViewById(R.id.student_level_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_level_spinner.setAdapter(adapter);
        student_level_spinner.setOnItemSelectedListener(this);


        /*--------------get selected user from previous listView --------------*/
        Intent intent = getIntent();
        final String student_selected = intent.getStringExtra("user_id");

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------  get profile from webservice  -----------------*/
            String url = MainActivity.IP + "/profile/" + student_selected;
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
                            firstname_data = response.getString("firstname");
                            lastname_data = response.getString("lastname");
                            JSONObject compteJsonObject = response.getJSONObject("compte");
                            login_data = compteJsonObject.getString("login");

                            firstname.setText(firstname_data + "");
                            lastname.setText(lastname_data);
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
            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(student_selected));
            firstname_data = user.getFirstname();
            lastname_data = user.getLastname();
            int compte_id = user.getCompte_id();
            Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            login_data = compte.getLogin();
            firstname.setText(firstname_data + "");
            lastname.setText(lastname_data);
            login.setText(login_data);
        }


        final Button update_btn = findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if(selectedRadioButtonId == -1 || !validateLogin() || !validatePassword() || !validateFirstname() || !validateLastname()){
                    return;
                } else {
                    selectedRadioButton = findViewById(selectedRadioButtonId);
                    parent_data = selectedRadioButton.getText().toString();

                    /*--------------get user from session --------------*/
                    sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    String user_connected = sharedpreferences.getString(MainActivity.Id, null);
                    //call restful webservice
                    updateStudentRest(student_selected, user_connected, login_data, password_data, firstname_data, lastname_data, parent_data, level_data);
                    Intent intent=new Intent(StudentUpdateActivity.this, DashboardParentActivity.class);
                    context.startActivity(intent);


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        level_data = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), level_data, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /*------------------ update data through RESTful webservice ----------------*/
    public void updateStudentRest(String student_selected, String user_connected, String login, String password, String firstName, String lastName, String parent, String level) {
        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            String url = MainActivity.IP + "/studentUpdate/" + student_selected + "/" + user_connected + "/" + login + "/" + password + "/" + firstName + "/" + lastName + "/" + parent + "/" + level;
            RequestQueue queue = Volley.newRequestQueue(context);
            //Toast.makeText(context, "s###################."+login_data+"??????????"+password_data, Toast.LENGTH_LONG).show();
            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        userAlreadyExist = response.getString("firstname");
                        if (userAlreadyExist.equals("already exist")) {
                            msg_error.setText(getString(R.string.register_user_alreadyexist));
                            Toast.makeText(context, getString(R.string.register_user_alreadyexist), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(student_selected));
            int compte_id = user.getCompte_id();
            sqLiteHelper.updateStudent(sqLiteHelper.getDatabase(), student_selected, compte_id, login, password, firstName, lastName, parent, level);
        }
    }
}
