package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private ActionBar toolbar;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle(R.string.course);
        //toolbar.setBackgroundDrawable(new ColorDrawable(R.color.primary_light));
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));
        //BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.bg_red));
        //toolbar.setBackgroundDrawable(background);

        Intent intent = getIntent();
        String ToSubscription = intent.getStringExtra("ToSubscription");
        String ToProfile = intent.getStringExtra("ToProfile");

        if (ToSubscription != null && ToSubscription.contentEquals("1")) {
            toolbar.setTitle(R.string.navigation_menu_subscription);
            loadFragment(new FragmentProfile());
            navigation.setSelectedItemId(R.id.subscription);

        } else if (ToProfile != null && ToProfile.contentEquals("1")) {
            toolbar.setTitle(R.string.navigation_menu_profile);
            loadFragment(new FragmentProfile());
            navigation.setSelectedItemId(R.id.profile);

        } else {
            loadFragment(new FragmentCourse());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.course:
                    toolbar.setTitle(R.string.navigation_menu_courses);
                    loadFragment(new FragmentCourse());
                    return true;
                case R.id.profile:
                    toolbar.setTitle(R.string.navigation_menu_profile);
                    loadFragment(new FragmentProfile());
                    return true;
                case R.id.log:
                    toolbar.setTitle(R.string.navigation_menu_log);
                    loadFragment(new FragmentLog());
                    return true;
                case R.id.forum:
                    toolbar.setTitle(R.string.navigation_menu_forum);
                    loadFragment(new FragmentForum());
                    return true;
                case R.id.myspace:
                    toolbar.setTitle(R.string.navigation_menu_myspace);
                    loadFragment(new FragmentMySpace());
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}