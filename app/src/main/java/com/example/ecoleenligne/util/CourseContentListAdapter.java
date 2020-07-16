package com.example.ecoleenligne.util;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.example.ecoleenligne.ListeExercicesActivity;
import com.example.ecoleenligne.VideoContentActivity;
import com.example.ecoleenligne.ContentActivity;
import com.example.ecoleenligne.QuizActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.CourseContent;

import java.util.ArrayList;
import java.util.List;


public class CourseContentListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CourseContent> courseContents = new ArrayList<CourseContent>();
    Context context;

    public CourseContentListAdapter(Context context, List<CourseContent> courseContents) {
        this.courseContents.addAll(courseContents);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CourseContent courseContent = (CourseContent) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.content_item, null);
        }

        //Pdf content
        if(courseContent.type.equals("1")) {
            ImageView mainImage = view.findViewById(R.id.content_image);
            mainImage.setBackgroundResource(R.drawable.image_pdf);
        }

        TextView video_title = view.findViewById(R.id.video_title);
        video_title.setText(""+ courseContent.getTitle());

        //Start video player
        final LinearLayout video_play = view.findViewById(R.id.video_play);
        video_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //pdf content
                if(courseContent.type.equals("1")) {
                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra("lien", courseContent.getPath());

                    context.startActivity(intent);
                } else {
                    //video content
                    Intent intent = new Intent(context, VideoContentActivity.class);
                    intent.putExtra("lien", courseContent.getPath());
                    context.startActivity(intent);
                }


            }
        });

/*
        final int course_id = courseContent.getId();

        final Button details_btn = view.findViewById(R.id.details);
        details_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ListeCourseContentActivity.class);
                intent.putExtra("course_id", ""+course_id);
                intent.putExtra("course_title", ""+course.getName());
                context.startActivity(intent);
            }
        });

        final Button exercices_btn = view.findViewById(R.id.exercices);
        exercices_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ListeExercicesActivity.class);
                intent1.putExtra("id",course_id);
                context.startActivity(intent1);
            }
        });


        final Button quiz_btn = view.findViewById(R.id.quiz);
        quiz_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, QuizActivity.class);
                intent.putExtra("course_id", ""+course_id);
                context.startActivity(intent);
            }
        });

 */


        return view;
    }

    @Override
    public Object getItem(int position) {
        return courseContents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return courseContents.size();
    }

    public void setcourseContents(List<CourseContent> students) {
        this.courseContents = students;
        notifyDataSetChanged();
    }
}