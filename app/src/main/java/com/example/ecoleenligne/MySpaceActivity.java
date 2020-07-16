package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.CourseContent;
import com.example.ecoleenligne.util.CourseContentListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySpaceActivity extends AppCompatActivity {

    final Context context = this;
    private SharedPreferences sharedpreferences;
    private CourseContentListAdapter courseContentListAdapter;
    ListView listview;
    List<CourseContent> myDocuements = new ArrayList<CourseContent>();

    Dialog dialog;
    ImageView closePoppupNegativeImg;
    TextView negative_title, negative_content;
    Button negative_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_space);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        this.courseContentListAdapter = new CourseContentListAdapter(this, myDocuements);
        ListView studentsListView = findViewById(R.id.list_students);
        studentsListView.setAdapter(courseContentListAdapter);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------  get profile from server  -----------------*/
            String url = MainActivity.IP + "/api/profile/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);

            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("email");
                        if (user_exist.equals("not found")) {
                            Toast.makeText(context, "User not exist", Toast.LENGTH_LONG).show();
                        } else {
                            //String fullname_data = response.getString("firstName")+" "+response.getString("lastName");
                            //String birthday_data = response.getString("date_birth");

                            myDocuements = new ArrayList<>(response.getJSONArray("desktopDocuments").length());
                            for (int i = 0; i < response.getJSONArray("desktopDocuments").length(); i++) {
                                JSONObject item = response.getJSONArray("desktopDocuments").getJSONObject(i);
                                // String id = item.getString("_id");
                                String id = item.getString("id");
                                String title = item.getString("name");
                                String path = MainActivity.IP_myspace +"/TER.git/public/"+ item.getString("file");
                                String type = ""+1;

                                myDocuements.add(new CourseContent(Integer.parseInt(id), title, path, type));
                            }
                            courseContentListAdapter.setcourseContents(myDocuements);
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

        }

    }

    public void backStep(View view) {
        finish();
    }

    private void actionNegative() {
        dialog.setContentView(R.layout.popup_negative);
        closePoppupNegativeImg = dialog.findViewById(R.id.negative_close);
        closePoppupNegativeImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}