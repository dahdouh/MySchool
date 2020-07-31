package com.example.ecoleenligne.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Subject;

import java.util.ArrayList;
import java.util.List;


public class SubjectListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Subject> subjects = new ArrayList<Subject>();
    Context context;

    public SubjectListAdapter(Context context, List<Subject> subjects) {
        this.subjects.addAll(subjects);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Subject subject = (Subject) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.subjects_item, null);
        }

        //String name_data = course.getName();
        //name_data = name_data.substring(0, 20);
        TextView subjectId = view.findViewById(R.id.subject_id);
        subjectId.setText(""+ subject.getId());
        TextView name = view.findViewById(R.id.name);
        name.setText(subject.getName());

        String icon = subject.getIcon();
        int id = view.getResources().getIdentifier(icon , "drawable", context.getPackageName());
        ImageView imageview = view.findViewById(R.id.user_photo);
        imageview.setImageDrawable(view.getResources().getDrawable(id));


        return view;
    }

    public static int getDrawable(Context context, String name)
    {
        //Assert.assertNotNull(context);
        //Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    @Override
    public Object getItem(int position) {
        return subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        notifyDataSetChanged();
    }
}