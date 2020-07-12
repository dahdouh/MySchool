package com.example.ecoleenligne;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.CourseContent;
import com.example.ecoleenligne.util.CourseContentListAdapter;
import com.example.ecoleenligne.util.CourseListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListVideoActivity extends AppCompatActivity {

    final Context context = this;
    private SharedPreferences sharedpreferences;
    private String course_id ="";
    private String course_title ="";
    private String lien ="";

    private CourseContentListAdapter courseContentListAdapter;
    ListView listview;
    List<CourseContent> courseContents = new ArrayList<CourseContent>();

    Dialog dialog;
    ImageView closePoppupNegativeImg;
    TextView negative_title, negative_content;
    Button negative_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);

        Intent intent = getIntent();
        course_id = intent.getStringExtra("course_id");
        course_title = intent.getStringExtra("course_title");
        //put dyamic course's title
        TextView titleCourse = findViewById(R.id.titleCourse);
        titleCourse.setText(""+course_title);
        //Toast.makeText(context, "cooooooooourse = "+ course_id + " "+ course_title, Toast.LENGTH_SHORT).show();

        this.courseContentListAdapter = new CourseContentListAdapter(this, courseContents);
        ListView studentsListView = findViewById(R.id.list_students);
        studentsListView.setAdapter(courseContentListAdapter);

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*--------------get user from session --------------*/
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            /*-------------- call restful webservices ----------*/
            String url =  MainActivity.IP+"/api/cours/content/video/"+ course_id;
            /*-------------------- get courses from server RESTful ------------------*/
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.length() != 0) {
                                courseContents = new ArrayList<>(response.length());
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject item = response.getJSONObject(i);
                                    String id = item.getString("id");
                                    String path = item.getString("path");
                                    String title = item.getString("title");
                                    String type = item.getString("type");
                                    courseContents.add(new CourseContent(Integer.parseInt(id), title, path, type));
                                    Toast.makeText(context, "iiiiiiii "+ title + " " +path, Toast.LENGTH_SHORT).show();
                                }

                                courseContentListAdapter.setcourseContents(courseContents);
                            }

                            dialog = new Dialog(context);
                            if(courseContents.isEmpty()){
                                actionNegative();
                                negative_title = dialog.findViewById(R.id.negative_title);
                                negative_content = dialog.findViewById(R.id.negative_content);
                                negative_button = dialog.findViewById(R.id.negative_button);
                                negative_button.setOnClickListener(v -> {
                                    finish();
                                });
                                negative_title.setText(R.string.course_video_title);
                                negative_content.setText(R.string.course_video_not_found);
                                negative_button.setText(R.string.button_ok);
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
            queue.add(request);

        } else  {

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
