package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Historic;
import com.example.ecoleenligne.util.HistoricListAdapter;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentParentPayment extends Fragment {


    private Context context;
    TextInputLayout owner, card_number, crypto;
    Spinner month, year;
    String owner_data, cardnumber_data;

    int crypto_data,mont_data,year_data;
    int idUser;
    SharedPreferences sharedpreferences;

    public FragmentParentPayment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_payment, container, false);

        owner = view.findViewById(R.id.owner);
        card_number = view.findViewById(R.id.credit_card);
        crypto = view.findViewById(R.id.crypto);


        /*-------------------- Spinner for Month -----------------*/
        month = (Spinner) view.findViewById(R.id.month_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.mois, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        month.setAdapter(adapter);
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mont_data = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //mont_data = Integer.parseInt(month.getSelectedItem().toString());

        /*-------------------- Spinner for year -----------------*/
        year = (Spinner) view.findViewById(R.id.year_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> yadapter = ArrayAdapter.createFromResource(context, R.array.annee, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        year.setAdapter(yadapter);
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year_data = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        final Button save_btn = view.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if(!validateOwner() || !validateCardNumber() || !validateCrypto()){
                    return;
                } else {
                    //get user from session
                    sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    String user_connected = sharedpreferences.getString(MainActivity.Email, null);
                    String id = sharedpreferences.getString(MainActivity.Id,null);
                    //   idUser = Integer.parseInt(id);
                    String expriration_date = mont_data+"-"+year_data;
                    registerpayement(owner_data, cardnumber_data, expriration_date, crypto_data);
                    Intent intent=new Intent(context, DashboardParentActivity.class);
                    context.startActivity(intent);
                }
            }
        });


        return view;
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

}
