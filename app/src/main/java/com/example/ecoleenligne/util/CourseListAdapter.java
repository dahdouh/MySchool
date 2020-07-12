package com.example.ecoleenligne.util;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoleenligne.ListeCourseContentActivity;
import com.example.ecoleenligne.ListeExercicesActivity;
import com.example.ecoleenligne.LoginActivity;
import com.example.ecoleenligne.ProfileActivity;
import com.example.ecoleenligne.QuizActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.TypeCoursActivity;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.User;

import java.util.ArrayList;
import java.util.List;


public class CourseListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Course> courses = new ArrayList<Course>();
    Context context;

    public CourseListAdapter(Context context, List<Course> courses) {
        this.courses.addAll(courses);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Course course = (Course) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.students_item, null);
        }

        //String name_data = course.getName();
        //name_data = name_data.substring(0, 20);
        TextView subject = view.findViewById(R.id.user_id);
        subject.setText(""+ course.getName());
        TextView name = view.findViewById(R.id.name);
        if(course.getDescription().length()>70)
            name.setText(course.getDescription().substring(0, 70)+ " ...");
        else
            name.setText(course.getDescription()+ " ...");


        final int course_id = course.getId();

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




        return view;
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    public void setCourses(List<Course> students) {
        this.courses = students;
        notifyDataSetChanged();
    }
}