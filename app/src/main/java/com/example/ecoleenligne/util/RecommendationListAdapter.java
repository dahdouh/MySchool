package com.example.ecoleenligne.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecoleenligne.R;
import com.example.ecoleenligne.RecommendationActivity;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendationListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Course> recommendations = new ArrayList<Course>();

    public RecommendationListAdapter(Context context, List<Course> recommendations) {
        this.recommendations.addAll(recommendations);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Course course = (Course) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.recommendations_item, null);
        }

        TextView user_id = view.findViewById(R.id.course_id);
        //user_id.setText(""+course.getId());
        user_id.setText(""+course.getId());
        TextView name = view.findViewById(R.id.libelle);
        //name.setText(course.getFirstname()+" "+ course.getLastname());
        name.setText(""+course.getDescription());
        //CircleImageView image = view.findViewById(R.id.course_image);
        //image.setImageDrawable(Drawable.createFromPath(course.getImage()));
        //image.setImageURI(Uri.parse(course.getImage()));
        //level.setText("TTTTTTTT");

        return view;
    }

    @Override
    public Object getItem(int position) {
        return recommendations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return recommendations.size();
    }

    public void setCourses(List<Course> recommendations) {
        this.recommendations = recommendations;
        notifyDataSetChanged();
    }
}
