package com.example.ecoleenligne;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class ResultatQuizActivity extends AppCompatActivity {

    final Context context = this;

    private LinearLayout navBar;
    private LinearLayout entrainementLayout;
    private LinearLayout correctionLayout;
    private TextView titleLeconText;
    private TextView gTitre;
    private TextView pTitre;
    private TextView score_quiz;
    private TextView recommencer;
    private String course_id = "";

    private ImageView arrowEntrainement;

    private GifImageView gifImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_quiz);

        Intent intent = getIntent();
        course_id = intent.getStringExtra("course_id");

        Bundle bundle = getIntent().getExtras();

        navBar = findViewById(R.id.navBar);

        titleLeconText = findViewById(R.id.titleLecon);
        gTitre = findViewById(R.id.gTitre);
        pTitre = findViewById(R.id.pTitre);
        score_quiz = findViewById(R.id.score_quiz);
        recommencer = findViewById(R.id.recommencer);

        gifImage = findViewById(R.id.gifImage);
        final Integer score = (Integer) bundle.get("score");
        final Integer nbrQuestion = (Integer) bundle.get("nbrQuestion");

        final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        float result = (score/nbrQuestion)*100;
        if (result >= 70.00) {
            gifImage.setImageResource(getApplicationContext().getResources().getIdentifier("giflove", "drawable", getApplicationContext().getPackageName()));
            gTitre.setText(R.string.msg_result_cool_title);
            pTitre.setText(R.string.msg_result_cool_content);
        } else if (result >= 50.00) {
            gifImage.setImageResource(getApplicationContext().getResources().getIdentifier("gifgood", "drawable", getApplicationContext().getPackageName()));
            gTitre.setText(R.string.msg_result_good_title);
            pTitre.setText(R.string.msg_result_good_content);
        } else if (result >= 30.00) {
            gifImage.setImageResource(getApplicationContext().getResources().getIdentifier("gifsad", "drawable", getApplicationContext().getPackageName()));
            gTitre.setText(R.string.msg_result_ok_title);
            pTitre.setText(R.string.msg_result_ok_content);
        } else if (result >= 0.00) {
            gifImage.setImageResource(getApplicationContext().getResources().getIdentifier("gifverysad", "drawable", getApplicationContext().getPackageName()));
            gTitre.setText(R.string.msg_result_no_title);
            pTitre.setText(R.string.msg_result_no_content);
        }
        /*
        titleLeconText.setText(titleLecon);
        entrainementLayout.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorMatiere)));
        arrowEntrainement.setImageResource(getApplicationContext().getResources().getIdentifier(colorEntrainement, "drawable", getApplicationContext().getPackageName()));
        */
        score_quiz.setText(score +" / "+ nbrQuestion);
        /*
        navBar.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorDarkMatiere)));
        correctionLayout.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorQuiz)));
        recommencer.setTextColor(getResources().getColor(Integer.parseInt(colorDarkMatiere)));
         */

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void backStep(View view) {
        Intent intent = new Intent(context, ListeCoursActivity.class);
        context.startActivity(intent);
        //finish();
    }
    public void recommence(View view) {
        Intent intent = new Intent(context, QuizActivity.class);
        intent.putExtra("course_id", ""+course_id);
        context.startActivity(intent);
    }
}
