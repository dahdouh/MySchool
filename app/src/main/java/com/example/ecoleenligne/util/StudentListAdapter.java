package com.example.ecoleenligne.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.User;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<User> students = new ArrayList<User>();

    public StudentListAdapter(Context context, List<User> students) {
        this.students.addAll(students);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        User student = (User) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.students_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        user_id.setText(""+student.getId());
        TextView name = view.findViewById(R.id.name);
        name.setText(student.getFirstname()+" "+ student.getLastname());
        TextView level = view.findViewById(R.id.level);
        level.setText(student.getLevel());

        return view;
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    public void setStudents(List<User> students) {
        this.students = students;
        notifyDataSetChanged();
    }
}