package com.example.ecoleenligne;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Post;
import com.example.ecoleenligne.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    //private FirebaseFirestore firebaseFirestore;
    //private FirebaseAuth firebaseAuth;

    final Context context = this;

    private LinearLayout navBar;
    private TextView titleLeconText;
    private TextView number_question;
    private TextView text_question;
    private TextView text_rep1;
    private TextView text_rep2;
    private TextView text_rep3;
    private TextView text_rep4;

    private ProgressBar progressBar;

    private ArrayList<ArrayList<String>> quiz = new ArrayList<ArrayList<String>>();

    private int score = 0;
    private int nbrQuestion = 0;
    private int currentQuestion = 0;
    private String course_id = "";

    private String titleLecon;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        /*------------------  make  Status Bar transparent ----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Intent intent = getIntent();
        course_id = intent.getStringExtra("course_id");

        Bundle bundle = getIntent().getExtras();


        navBar = findViewById(R.id.navBar);
       /* layoutReponse1 = findViewById(R.id.layoutReponse1);
        layoutReponse2 = findViewById(R.id.layoutReponse2);
        layoutReponse3 = findViewById(R.id.layoutReponse3);
        layoutReponse4 = findViewById(R.id.layoutReponse4);
        */

        titleLeconText = findViewById(R.id.titleLecon);
        number_question = findViewById(R.id.number_question);
        text_question = findViewById(R.id.text_question);
        text_rep1 = findViewById(R.id.text_rep1);
        text_rep2 = findViewById(R.id.text_rep2);
        text_rep3 = findViewById(R.id.text_rep3);
        text_rep4 = findViewById(R.id.text_rep4);

        progressBar = findViewById(R.id.progressBar);

        /*
        matiere = (String) bundle.get("matiere");
        titleLecon = (String) bundle.get("titleLecon");
        colorMatiere = (String) bundle.get("colorMatiere");
        colorDarkMatiere = (String) bundle.get("colorDarkMatiere");
        colorEntrainement = (String) bundle.get("colorEntrainement");
        colorQuiz = (String) bundle.get("colorQuiz");
        */


        titleLeconText.setText(titleLecon);
        //navBar.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorDarkMatiere)));
        /*
        layoutReponse1.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorQuiz)));
        layoutReponse2.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorQuiz)));
        layoutReponse3.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorQuiz)));
        layoutReponse4.setBackground(getApplicationContext().getDrawable(Integer.parseInt(colorQuiz)));

         */

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //window.setStatusBarColor(getResources().getColor(Integer.parseInt(colorDarkMatiere)));
            //window.setNavigationBarColor(getResources().getColor(Integer.parseInt(colorDarkMatiere)));
        }

        String url =  MainActivity.IP+"/api/quiz/"+course_id;
        /*-------------------- get forums from server RESTful ------------------*/
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() != 0) {
                            quiz = new ArrayList<>(response.length());
                            for (int i = 0; i < response.length(); i++) {
                                ArrayList<String> arrayQ = new ArrayList<String>();
                                JSONObject item = response.getJSONObject(i);
                                String Q = item.getString("content");
                                String R1 = item.getString("response1");
                                String R2 = item.getString("response2");
                                String R3 = item.getString("response3");
                                String R4 = item.getString("response4");
                                String TR = item.getString("response5");
                                arrayQ.add(Q);
                                arrayQ.add(R1);
                                arrayQ.add(R2);
                                arrayQ.add(R3);
                                arrayQ.add(R4);
                                arrayQ.add(TR);
                                quiz.add(arrayQ);
                            }
                            nbrQuestion = quiz.size();
                            updateQuestion(currentQuestion);
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(0);

            }
        }, 500);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void nextQuestion(View view) {

        // Verification de la réponse
        String reponseId = getResources().getResourceEntryName(view.getId());
        TextView reponseTextView = null;

        if (reponseId.equals("cardViewReponse1")) {
            reponseTextView = findViewById(R.id.text_rep1);
        } else if (reponseId.equals("cardViewReponse2")) {
            reponseTextView = findViewById(R.id.text_rep2);
        } else if (reponseId.equals("cardViewReponse3")) {
            reponseTextView = findViewById(R.id.text_rep3);
        } else if (reponseId.equals("cardViewReponse4")){
            reponseTextView = findViewById(R.id.text_rep4);
        }


        if (reponseTextView.getText().toString().equals(quiz.get(currentQuestion).get(5))) {
            score++;
        }



        // Changement de question si different de la derniere
        if (currentQuestion != nbrQuestion - 1) {
            // Changement de la question
            currentQuestion = currentQuestion + 1;
            updateQuestion(currentQuestion);
        } else {

            Intent intent = new Intent(this, ResultatQuizActivity.class);
            //intent.putExtra("matiere", matiere);
            //intent.putExtra("titleLecon", titleLecon);
            //intent.putExtra("colorMatiere", colorMatiere);
            //intent.putExtra("colorDarkMatiere", colorDarkMatiere);
            //intent.putExtra("colorEntrainement", colorEntrainement);
            //intent.putExtra("colorQuiz", colorQuiz);
            intent.putExtra("course_id", course_id);
            intent.putExtra("score", score);
            intent.putExtra("nbrQuestion", nbrQuestion);
            startActivity(intent);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateQuestion(int currentQuestion) {
        number_question.setText("Question n°"+ (currentQuestion + 1));
        progressBar.setProgress(progressBar.getProgress() + ((currentQuestion + 1)*100)/nbrQuestion, true);
        text_question.setText(quiz.get(currentQuestion).get(0));
        text_rep1.setText(quiz.get(currentQuestion).get(1));
        text_rep2.setText(quiz.get(currentQuestion).get(2));
        text_rep3.setText(quiz.get(currentQuestion).get(3));
        text_rep4.setText(quiz.get(currentQuestion).get(4));

    }

    public void backStep(View view) {
        finish();
    }
}
