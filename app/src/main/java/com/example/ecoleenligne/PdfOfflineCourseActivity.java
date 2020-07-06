package com.example.ecoleenligne;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PdfOfflineCourseActivity extends AppCompatActivity{

    List<String> cours = new ArrayList<String>();
    List<String> liens = new ArrayList<>();

    final Context context = this;
    String datef="";
    SharedPreferences sharedpreferences;
    String id_user;
    int[] img ={R.drawable.pdf,R.drawable.pdf,R.drawable.pdf};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cours.add("cours1.pdf");

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");


        ListView listView = (ListView) findViewById(R.id.listview);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        id_user = sharedpreferences.getString(MainActivity.Id,null);





    }


    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cours.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_layout,null);
            ImageView image = (ImageView)convertView.findViewById(R.id.imageView);
            image.setImageResource(R.drawable.pdf);
            final TextView titre = (TextView)convertView.findViewById(R.id.titre);
            titre.setText(cours.get(position));
            RelativeLayout lb = (RelativeLayout)convertView.findViewById(R.id.rlc);

            lb.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PdfOfflineCourseActivity.this, ContentOfflineActivity.class);
                    startActivity(intent);


                }
            });
            return convertView;
        }
    }



}
