package com.example.ecoleenligne;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EvaluationActivity extends AppCompatActivity implements Response.Listener<JSONArray>, Response.ErrorListener{

    List<String> libelle = new ArrayList<String>();
    List<String> codes = new ArrayList<>();
    String r;
    String id;
int[] img ={R.drawable.pdf,R.drawable.pdf,R.drawable.pdf};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_layout);
        TextView question = (TextView)findViewById(R.id.question);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String q = intent.getStringExtra("question");
        r = intent.getStringExtra("reponse");
        question.setText(q);



        //***************** Appel API**************************
        String url = MainActivity.IP+"/api/cours/exercice/listechoices/"+id;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
        queue.add(microPostsRequest);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getMessage())
                .show();
    }

    @Override
    public void onResponse(JSONArray response) {

        try {

            if(response.length()==0){
                Toast.makeText(EvaluationActivity.this,"Aucun Exercice trouvé",Toast.LENGTH_SHORT);
                Toast.makeText(EvaluationActivity.this,"Aucun Exercice trouvé",Toast.LENGTH_SHORT);

            }

            for (int i = 0; i < response.length(); i++) {
                JSONObject item = response.getJSONObject(i);
                // String id = item.getString("_id");
                String message = item.getString("libelle");
                String code = item.getString("code");
                libelle.add(message);
                codes.add(code);


            }


            final Button rep1 = (Button) findViewById(R.id.rep1);
            rep1.setText(codes.get(0)+"-)     "+libelle.get(0));
            rep1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(codes.get(0).equals(r)){
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Bravo c'est la bonne reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                    }else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Mauvaise reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                    }
                }
            });
            Button rep2 = (Button) findViewById(R.id.rep2);
            rep2.setText(codes.get(1)+"-)     "+libelle.get(1));
            rep2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(codes.get(1).equals(r)){
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Bravo c'est la bonne reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                        alert.show();
                        Intent intent = new Intent(EvaluationActivity.this,ListeQuestionsActivity.class);
                        intent.putExtra("valide","valide");
                        ListeQuestionsActivity.etat="valide";
                        intent.putExtra("id",id);
                        startActivity(intent);

                    }else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Mauvaise reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                    }
                }
            });
            Button rep3 = (Button) findViewById(R.id.rep3);
            rep3.setText(codes.get(2)+"-)     "+libelle.get(2));
            rep3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(codes.get(2).equals(r)){
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Bravo c'est la bonne reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Mauvaise reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();
                    }
                }
            });
            Button rep4 = (Button) findViewById(R.id.rep4);
            rep4.setText(codes.get(3)+"-)     "+libelle.get(3));
            rep4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(codes.get(3).equals(r)){
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Bravo c'est la bonne reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();

                    }else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(EvaluationActivity.this);
                        builder.setMessage("Mauvaise reponse !");
                        AlertDialog alert=builder.create();
                        alert.show();

                    }
                }
            });

        } catch (JSONException error) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error.toString())
                    .show();
        }

    }
}
