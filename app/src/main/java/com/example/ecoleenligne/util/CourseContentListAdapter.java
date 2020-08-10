package com.example.ecoleenligne.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.example.ecoleenligne.ListeExercicesActivity;
import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.VideoContentActivity;
import com.example.ecoleenligne.ContentActivity;
import com.example.ecoleenligne.QuizActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.CourseContent;
import com.squareup.picasso.Picasso;

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

        ImageView mainImage = view.findViewById(R.id.content_image);

        Intent intent = ((Activity) context).getIntent();
        String course_id = intent.getStringExtra("course_id");
        String course_image = intent.getStringExtra("course_image");
        String path_img = MainActivity.IP_myspace +"/TER.git/public/upload/course/"+ course_id +"/"+ course_image;
        Picasso.get().load(path_img).into(mainImage);
        /*
        if(courseContent.type.equals("1")) {
            mainImage.setBackgroundResource(R.drawable.image_pdf);
        }else if(courseContent.type.equals("3")) {
            mainImage.setBackgroundResource(R.drawable.image_logo);
        }
        */

        TextView video_title = view.findViewById(R.id.video_title);
        video_title.setText(""+ courseContent.getTitle());

        ImageView content_image = view.findViewById(R.id.content_image);
        content_image.setBackgroundColor(Color.TRANSPARENT);
        if(courseContent.type.equals("1")) { //pdf
            content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf));
        } else if(courseContent.type.equals("3")) { //image
            content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.image_logo));
        } else { //video
            content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icons8_video_96));
        }


        //Start video player
        final LinearLayout video_play = view.findViewById(R.id.video_play);
        video_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //pdf content
                if(courseContent.type.equals("1")) {
                    content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf));
                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra("lien", courseContent.getPath());
                    intent.putExtra("course_name", courseContent.getTitle());

                    context.startActivity(intent);
                } else if(courseContent.type.equals("3")) {
                    content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.image_logo));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(courseContent.getPath()), "image/*");
                    context.startActivity(intent);

                } else {
                    //video content
                    content_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icons8_video_96));
                    Intent intent = new Intent(context, VideoContentActivity.class);
                    intent.putExtra("lien", courseContent.getPath());
                    intent.putExtra("course_name", courseContent.getTitle());
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