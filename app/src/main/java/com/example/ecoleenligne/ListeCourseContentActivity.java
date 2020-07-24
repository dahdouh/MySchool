package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ListeCourseContentActivity extends AppCompatActivity {


    final Context context = this;

    private String course_id ="";
    private String course_title ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_course_content);

        Intent intent = getIntent();
        course_id = intent.getStringExtra("course_id");
        course_title = intent.getStringExtra("course_title");

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(course_title);

        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));
        //Transparent statusbar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    public void backStep(View view) {
        finish();
    }

    public void gotoNextInterface(View view) {
        String layout = getResources().getResourceEntryName(view.getId());
        Intent intent = null;
        switch (layout) {
            case "video" :
                intent = new Intent(this, ListVideoActivity.class);
                intent.putExtra("course_id", ""+course_id);
                intent.putExtra("course_title", ""+ course_title);
                break;
            case "courses" :
                intent = new Intent(this, ListPdfActivity.class);
                intent.putExtra("course_id", ""+course_id);
                intent.putExtra("course_title", ""+ course_title);
                break;
            case "quiz" :
                intent = new Intent(this, QuizActivity.class);
                intent.putExtra("course_id", ""+course_id);
                context.startActivity(intent);
                break;
            case "exercice" :
                intent = new Intent(context, ListeExercicesActivity.class);
                intent.putExtra("id",course_id);
                context.startActivity(intent);
                break;
        }

        startActivity(intent);
    }
}

