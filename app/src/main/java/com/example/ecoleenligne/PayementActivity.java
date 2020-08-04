package com.example.ecoleenligne;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.example.ecoleenligne.model.Compte;
//import com.example.ecoleenligne.model.User;

public class PayementActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    final Context context = this;
    TextInputLayout owner, card_number, crypto;
    Spinner month, year;
    String owner_data, cardnumber_data;

    int crypto_data,mont_data,year_data;
    int idUser;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);
        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.navigation_menu_payment);
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));
        //getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));
        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        owner = findViewById(R.id.owner);
        card_number = findViewById(R.id.credit_card);
        crypto = findViewById(R.id.crypto);


        /*-------------------- Spinner for Month -----------------*/
        month = (Spinner) findViewById(R.id.month_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mois, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        month.setAdapter(adapter);
        month.setOnItemSelectedListener(this);
        mont_data = Integer.parseInt(month.getSelectedItem().toString());

        /*-------------------- Spinner for year -----------------*/
        year = (Spinner) findViewById(R.id.year_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> yadapter = ArrayAdapter.createFromResource(this, R.array.annee, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        year.setAdapter(yadapter);
        year.setOnItemSelectedListener(this);



        final Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


               if(!validateOwner() || !validateCardNumber() || !validateCrypto()){
                    return;
                } else {
                   //get user from session
                   sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                   String user_connected = sharedpreferences.getString(MainActivity.Email, null);
                   String id = sharedpreferences.getString(MainActivity.Id,null);
                //   idUser = Integer.parseInt(id);
                   String expriration_date = mont_data+"-"+year_data;
                   registerpayement(owner_data, cardnumber_data, expriration_date, crypto_data);
                   Intent intent=new Intent(PayementActivity.this, DashboardParentActivity.class);
                   context.startActivity(intent);


                }
            }
        });



    }


    //  save data in server database
    public void registerpayement(String owner, String card_number, String expriration_date, int  crypto) {

        String id = sharedpreferences.getString(MainActivity.Id,null);

        if(id==null){
            id="1";
        }

        int id_user = Integer.parseInt(id);
        String url = MainActivity.IP+"/api/payment/"+ id_user +"/"+owner+"/"+ card_number+"/"+ expriration_date +"/"+ crypto;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(context, "vos innformations bancaires sont bien enregistrées.", Toast.LENGTH_LONG).show();

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

    private Boolean validateOwner() {
        owner_data = owner.getEditText().getText().toString();
        if (owner_data.isEmpty()) {
            owner.setError(getString(R.string.credit_card_owner_msg_error));
            return false;
        } else {
            owner.setError(null);
            owner.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateCardNumber() {
        cardnumber_data = card_number.getEditText().getText().toString();
        if (cardnumber_data.isEmpty()) {
            card_number.setError(getString(R.string.card_number_msg_error));
            return false;
        } else{
            return true;
        }
    }


    private Boolean validateCrypto() {
        crypto_data = Integer.parseInt(crypto.getEditText().getText().toString());
        if (crypto_data == 0) {
            crypto.setError(getString(R.string.crypto_msg_error));
            return false;
        } else {
            crypto.setError(null);
            crypto.setErrorEnabled(false);
            return true;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        year_data = Integer.parseInt(parent.getItemAtPosition(position).toString());
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
