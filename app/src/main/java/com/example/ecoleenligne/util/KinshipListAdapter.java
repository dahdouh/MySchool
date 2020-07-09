package com.example.ecoleenligne.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Kinship;
import com.example.ecoleenligne.model.Post;

import java.util.ArrayList;
import java.util.List;

public class KinshipListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Kinship> kinships = new ArrayList<Kinship>();
    Context context;

    public KinshipListAdapter(Context context, List<Kinship> kinships) {
        this.kinships.addAll(kinships);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Kinship kinship = (Kinship) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.child_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        user_id.setText(""+ kinship.getStudent().getFirstname() +" "+kinship.getStudent().getLastname());
        TextView name = view.findViewById(R.id.name);
        name.setText("########");
        /*TextView date = view.findViewById(R.id.date);
        String string = kinship.getDate();
        String[] parts = string.split("T");
        String date1 = parts[0]; // dd/mm/YYYY
        String date2 = parts[1];
        String[] date3 = date2.split("\\+");
        String hour = date3[0];
        TextView desc = view.findViewById(R.id.desc);
        date.setText(date1 +"  "+hour)
        ;
         */

        return view;
    }

    @Override
    public Object getItem(int position) {
        return kinships.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return kinships.size();
    }

    public void setKinships(List<Kinship> kinships) {
        this.kinships = kinships;
        notifyDataSetChanged();
    }
}