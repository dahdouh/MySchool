package com.example.ecoleenligne;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.CourseListAdapter;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListeCoursActivity extends AppCompatActivity {

    final Context context = this;
    List<String> cours = new ArrayList<String>();
    List<String> liens = new ArrayList<>();
    List<String> id = new ArrayList<>();
    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;

    SharedPreferences sharedpreferences;

    // list adapter
    private CourseListAdapter courseListAdapter;
    ListView listview;
    List<Course> courses = new ArrayList<Course>();

    //dialog
    Dialog dialog;
    ImageView closePoppupNegativeImg;
    TextView negative_title, negative_content;
    Button negative_button;

    int[] img ={R.drawable.pdf,R.drawable.pdf,R.drawable.pdf};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_course);
        sqLiteHelper= new SQLiteHelper(this);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        final String subject_name = intent.getStringExtra("subject_name");
        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(sharedpreferences.getString("subject_name", null));
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));

        final String subject_id = sharedpreferences.getString("subject_id", null);
        // List of courses adapter
        this.courseListAdapter = new CourseListAdapter(this, courses);
        ListView studentsListView = findViewById(R.id.list_students);
        studentsListView.setAdapter(courseListAdapter);

        // check if there is connection-
        if(MainActivity.MODE.equals("ONLINE")) {
            // get user from session
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);
            // get courses from server RESTful
            //String url =  MainActivity.IP+"/api/cours/"+ user_connected;
            String url =  MainActivity.IP+"/api/subject/cours/"+ user_connected +"/"+ sharedpreferences.getString("subject_id", null);
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.length() != 0) {
                                courses = new ArrayList<>(response.length());
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject item = response.getJSONObject(i);
                                    String id = item.getString("id");
                                    String image = item.getString("image");
                                    JSONObject SubjectJsonObject = item.getJSONObject("subject");
                                    String name = SubjectJsonObject.getString("name");
                                    String description = item.getString("description");
                                    courses.add(new Course(Integer.parseInt(id), name, description, image));
                                }
                                courseListAdapter.setCourses(courses);
                            } else {
                                if(courses.isEmpty()){
                                    dialog = new Dialog(context);
                                    actionNegative();
                                    negative_title = dialog.findViewById(R.id.negative_title);
                                    negative_content = dialog.findViewById(R.id.negative_content);
                                    negative_button = dialog.findViewById(R.id.negative_button);
                                    negative_button.setOnClickListener(v -> {
                                        finish();
                                    });
                                    negative_title.setText(R.string.subscribe_title);
                                    negative_content.setText(R.string.subscribtion_empty);
                                    negative_button.setText(R.string.button_ok);
                                }
                            }

                        } catch (JSONException error) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage(error.toString())
                                    .show();
                        }
                    },
                    error -> new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(error.getMessage())
                            .show()
            );
            queueUserConnected.add(requestUserConnected);

        } else  {}

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