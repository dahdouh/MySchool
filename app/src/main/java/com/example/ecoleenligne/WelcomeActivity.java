package com.example.ecoleenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

public class WelcomeActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        /*--------------------------- integrate video in my app --------------------------*/

        String myYouTubeVideoUrl = "https://www.youtube.com/embed/XUTXU6fy94E";
        String dataUrl =
                "<html>" +
                        "<body>" +
                        "<br>" +
                        "<iframe width=\"960\" height=\"500\" src=\""+myYouTubeVideoUrl+"\" frameborder=\"0\" allowfullscreen/>" +
                        "</body>" +
                        "</html>";

        WebView myWebView = findViewById(R.id.mWebView);

        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.loadData(dataUrl, "text/html", "utf-8");
        //button signin
        final Button signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                context.startActivity(intent);
            }
        });
        //button register
        final Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                intent.putExtra("role", "ROLE_STUDENT");
                context.startActivity(intent);

            }
        });

        //button demo
        final Button demo = findViewById(R.id.demo);
        demo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(MainActivity.Login, "karim.dahdouh.fr@gmail.com");
                editor.putString(MainActivity.Password, "schoolax");
                editor.putString(MainActivity.Email, "karim.dahdouh.fr@gmail.com");
                editor.putString(MainActivity.Id, ""+17);
                editor.putString(MainActivity.Role, "ROLE_STUDENT");
                editor.commit();
                Intent intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
                context.startActivity(intent);

                context.startActivity(intent);
            }
        });

        LinearLayout student_card = (LinearLayout) findViewById(R.id.student);
        LinearLayout parent_card = (LinearLayout) findViewById(R.id.parent);

        student_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        parent_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}