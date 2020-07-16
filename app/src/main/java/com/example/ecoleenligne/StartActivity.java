package com.example.ecoleenligne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StartActivity extends AppCompatActivity {

    private ViewPager mPager;
    private int[] layouts = {R.layout.activity_first_slide,
            R.layout.activity_second_slide,
            R.layout.activity_third_slide,
            R.layout.activity_fourth_slide};

    private MpagerAdapter mpagerAdapter;
    private LinearLayout dotsLayout;
    private ImageView dots[];
    private Button bnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new PreferenceManager(this).checkPreference())
            loadStartActivity();
        setContentView(R.layout.activity_start);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mpagerAdapter = new MpagerAdapter(layouts, this);
        mPager.setAdapter(mpagerAdapter);

        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        bnNext = (Button) findViewById(R.id.bnNext);

        bnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextSlide();
            }
        });

        createDots(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);

                if (position == layouts.length - 1) {
                    bnNext.setText(R.string.button_letsgo);
                }
                else {
                    bnNext.setText(R.string.button_next);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void createDots(int current_position) {
        if(dotsLayout != null)
            dotsLayout.removeAllViews();

        dots = new ImageView[layouts.length];

        for(int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);
            if(i == current_position)
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            else
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], params);
        }
    }

    private void loadStartActivity() {
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private void loadNextSlide() {
        int next_slide = mPager.getCurrentItem() + 1;

        if (next_slide < layouts.length) {
            mPager.setCurrentItem(next_slide);
        }
        else {
            loadStartActivity();
            new PreferenceManager(this).writePreference();
        }
    }
}