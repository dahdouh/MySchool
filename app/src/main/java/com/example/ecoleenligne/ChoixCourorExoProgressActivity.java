package com.example.ecoleenligne;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ChoixCourorExoProgressActivity extends AppCompatActivity {


int[] img ={R.drawable.pdf,R.drawable.pdf,R.drawable.pdf};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_type_layout);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");

        ImageView imgpdf = (ImageView)findViewById(R.id.imgPdf);
        imgpdf.setImageResource(R.drawable.cours);
        imgpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChoixCourorExoProgressActivity.this, CourseProgressActivity.class);
                intent1.putExtra("id",id);
                startActivity(intent1);
            }
        });

        ImageView imgvideo = (ImageView)findViewById(R.id.imgVideo);
        imgvideo.setImageResource(R.drawable.exercie);

        imgvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ChoixCourorExoProgressActivity.this, ExerciceProgressActivity.class);
                intent1.putExtra("id",id);
                startActivity(intent1);

            }
        });







    }



}
