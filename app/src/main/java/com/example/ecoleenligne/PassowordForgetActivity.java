package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class PassowordForgetActivity extends AppCompatActivity {

    final Context context = this;
    String emailExist ="";
    TextInputLayout email;
    String email_data;
    String msg ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passoword_forget);

        email = findViewById(R.id.password_forget_email);

        final Button password_send_btn = findViewById(R.id.password_send_btn);
        password_send_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!validateEmail()){
                    return;
                } else {
                    //call rest webservice
                    PasswordForgetRest(email_data);
                    Intent intent = new Intent(PassowordForgetActivity.this, LoginActivity.class);
                    context.startActivity(intent);

                }

                }
        });
    }

    /*------------------ save data in server database ----------------*/
    public void PasswordForgetRest(String email) {
        String url = MainActivity.IP+"/api/password/reset/"+email;
        RequestQueue queue = Volley.newRequestQueue(context);
        /*--------------- allow connection with https and ssl-------------*/
        HttpsTrustManager.allowAllSSL();
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    emailExist = response.getString("email");
                    if (emailExist.equals("not found")) {
                        Toast.makeText(context, getString(R.string.login_forget_email_notfoud), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, getString(R.string.login_forget_send_ok), Toast.LENGTH_SHORT).show();
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
            }
        });

        queue.add(request);

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
