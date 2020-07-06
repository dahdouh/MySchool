package com.example.ecoleenligne.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecoleenligne.CorrectionActivity;
import com.example.ecoleenligne.ListeExercicesActivity;
import com.example.ecoleenligne.ProfileActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Exercice;

import java.util.ArrayList;
import java.util.List;

public class ExerciceListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Exercice> exercices = new ArrayList<Exercice>();
    Context context;
    private int number = 0;

    public ExerciceListAdapter(Context context, List<Exercice> exercices) {
        this.exercices.addAll(exercices);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Exercice exercice = (Exercice) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.exercices_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        number ++;
        user_id.setText("Exercice "+ number);
        TextView name = view.findViewById(R.id.name);
        if(exercice.getContent().length()>70)
            name.setText(exercice.getContent().substring(0, 70)+ " ...");
        else
            name.setText(exercice.getContent()+ " ...");


        final int exercice_id = exercice.getId();

        final Button correction_btn = view.findViewById(R.id.correction);
        correction_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, CorrectionActivity.class);
                intent.putExtra("exercice_id", exercice_id);
                intent.putExtra("course_id", exercice.getCourse().getId());
                context.startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public Object getItem(int position) {
        return exercices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return exercices.size();
    }

    public void setExercices(List<Exercice> exercices) {
        this.exercices = exercices;
        notifyDataSetChanged();
    }
}