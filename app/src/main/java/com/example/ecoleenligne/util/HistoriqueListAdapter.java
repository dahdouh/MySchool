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

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoriqueListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<User> students = new ArrayList<User>();

    public HistoriqueListAdapter(Context context, List<User> students) {
        this.students.addAll(students);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        User student = (User) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.students_item, null);
        }

        TextView textView = view.findViewById(R.id.name);
        textView.setText(student.getFirstname()+" \n\n"+ student.getLastname());

        CircleImageView circleImageView = view.findViewById(R.id.user_photo);
        circleImageView.setImageResource(R.drawable.histoire);

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