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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.example.ecoleenligne.model.Compte;
//import com.example.ecoleenligne.model.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    final Context context = this;
    TextInputLayout login, password, firstname, lastname;
    RadioGroup radioGroup, radioGroupStudentSex;
    RadioButton selectedRadioButton, selectedRadioButtonStudentSex;
    Spinner student_level_spinner;
    String login_data, password_data, firstname_data, lastname_data, parent_data, level_data, sex_data;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroupStudentSex = findViewById(R.id.radioGroupStudentSex);

        /*-------------------- Spinner for student's level -----------------*/
        student_level_spinner = (Spinner) findViewById(R.id.student_level_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        student_level_spinner.setAdapter(adapter);
        student_level_spinner.setOnItemSelectedListener(this);

        final Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
               int selectedRadioButtonStudentSexId = radioGroupStudentSex.getCheckedRadioButtonId();
               if(selectedRadioButtonId == -1 || !validateLogin() || !validatePassword() || !validateFirstname() || !validateLastname()){
                    return;
                } else {
                   selectedRadioButton = findViewById(selectedRadioButtonId);
                   parent_data = selectedRadioButton.getText().toString();
                   selectedRadioButtonStudentSex = findViewById(selectedRadioButtonStudentSexId);
                   sex_data = selectedRadioButtonStudentSex.getText().toString();

                   /*--------------get user from session --------------*/
                   sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                   String user_connected = sharedpreferences.getString(MainActivity.Email, null);

                   registeStudentRest(user_connected, login_data, password_data, firstname_data, lastname_data, parent_data, level_data, sex_data);
                   //Toast.makeText(StudentAddActivity.this, parent_data + " is Selected", Toast.LENGTH_SHORT).show();
                   Intent intent=new Intent(StudentAddActivity.this, DashboardParentActivity.class);
                   context.startActivity(intent);


                }
            }
        });



    }


    /*------------------ save data in server database ----------------*/
    public void registeStudentRest(String user_connected, String login, String password, String firstName, String lastName, String parent, String level, String sex) {
        String url = MainActivity.IP+"/student/"+user_connected+"/"+ login+"/"+ password +"/"+ firstName +"/"+ lastName +"/"+ parent +"/"+ level+"/"+sex;
        RequestQueue queue = Volley.newRequestQueue(context);
        //Toast.makeText(context, "s###################."+login_data+"??????????"+password_data, Toast.LENGTH_LONG).show();
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(context, "student hase been successfully registred.", Toast.LENGTH_LONG).show();

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




    private Boolean validateLogin() {
        login_data = login.getEditText().getText().toString();
        if (login_data.isEmpty()) {
            login.setError(getString(R.string.register_login_validat_empty));
            return false;
        } else {
            login.setError(null);
            login.setErrorEnabled(false);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        level_data = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), level_data, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
