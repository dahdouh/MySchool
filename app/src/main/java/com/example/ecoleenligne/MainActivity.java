package com.example.ecoleenligne;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 100;
    final Context context = this;
    //Variables
    ImageView image;
    TextView logo, slogan;
    Animation topAnimation, bottomAnimation;

    SharedPreferences sharedpreferences;

    public static final String IP="https://10.115.187.47:8000";
    //public static final String IP="https://onlineschool.cfapps.io";


    /*--------------- user connected session ------*/
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Login = "loginKey";
    public static final String Password = "passwordKey";
    public static final String Email = "emailKey";
    public static final String Id = "idKey";
    public static final String Role = "RoleKey";
    /*--------------- mode online or offline ------*/
    public static String MODE ;
    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sqLiteHelper= new SQLiteHelper(this);

        /*--------------- if there is no internet connection, load courses from sqllite databases ----------------*/
        if(isNetworkAvailable()) {
            MODE = "ONLINE";
            List<User> list_users_sqlite= sqLiteHelper.getUsersFromDb();
            if(list_users_sqlite.isEmpty()){
                sqLiteHelper.createSqliteDatabase();
                Intent intent=new Intent(MainActivity.this, SynchronisationActivity.class);
                context.startActivity(intent);
            }
        } else {
            MODE = "OFFLINE";
        }

        //add annimation
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        image.setAnimation(topAnimation);
        logo.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);



        // after 5 seconds, DahsboardActivity will open automatically
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //Intent intent=new Intent(MainActivity.this,LoginActivity.class);

                /*--------------get user from session --------------*/

                /*
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                */

                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
                String user_connected_login = sharedpreferences.getString(MainActivity.Login, null);
                String user_profile = sharedpreferences.getString(MainActivity.Role, null);
                /*-------------- check if the user is connected or not, if connected choose his/her profile--------------*/
                Intent intent = null;
                if(user_connected_id == null && user_connected_login == null) {
                     intent=new Intent(MainActivity.this, WelcomeActivity.class);
                } else {
                    if (user_profile.equals("ROLE_ADMIN")) {
                        Toast.makeText(context, "#admiiiin not yet configured", Toast.LENGTH_LONG).show();
                    } else if(user_profile.equals("ROLE_TUTOR")) {
                        //intent = new Intent(MainActivity.this, DashboardParentActivity.class);
                        intent = new Intent(MainActivity.this, DashboardParentActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                    }


                }

                // Attach all the elements those you want to animate in design
                Pair[]pairs=new Pair[2];
                pairs[0]=new Pair<View, String>(image,"logo_image");
                pairs[1]=new Pair<View, String>(logo,"logo_text");

                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
                {ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
            }
        },SPLASH_SCREEN);


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
