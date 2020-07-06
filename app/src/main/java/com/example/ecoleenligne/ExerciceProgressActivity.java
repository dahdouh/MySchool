package com.example.ecoleenligne;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExerciceProgressActivity extends AppCompatActivity implements Response.Listener<JSONArray>, Response.ErrorListener{
    ListView listview;
    String id;
    List<String> state = new ArrayList<String>();
    List<String> description = new ArrayList<String>();
    int totalEx=0;
    int totalDone=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_description_layout);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        /*------------------------ Webservices  ---------------------*/
        ArrayList<String> offres = new ArrayList<String>();



        // issue the request
        //String url = "https://onlineschool.cfapps.io/api/cours/"+id;
         String url = MainActivity.IP+"/api/cours/exercice/progress/"+id;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
        queue.add(microPostsRequest);


    }

    @Override
    public void onResponse(JSONArray response) {
        try {
            state = new ArrayList<>(response.length());
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = response.getJSONObject(i);
                // String id = item.getString("_id");
                String message = item.getString("state");
                if (message.equals("done")){
                    totalDone+=1;
                }
                totalEx+=1;

                float progress=totalDone*100/totalEx;

                TextView nbrdone = (TextView)findViewById(R.id.nbrdone);
                TextView nbrtot = (TextView)findViewById(R.id.nbrtot);
                nbrdone.setText("Terminé :"+totalDone);
                int rest = totalEx-totalDone;
                nbrtot.setText("Restant :"+rest);

                ProgressBar pb = (ProgressBar)findViewById(R.id.progbar);
                ProgressBar pb2 = (ProgressBar)findViewById(R.id.progressBar);
                TextView taux = (TextView)findViewById(R.id.pourcentage);
                taux.setText(progress+"%");
                pb2.setProgress(totalDone);
                pb2.setMax(totalEx);
                pb.setMax(totalEx);
                pb.setProgress(totalDone);
                pb.getProgress();



            }
        } catch (JSONException error) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error.toString())
                    .show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getMessage())
                .show();
    }




}
