package com.example.ecoleenligne.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.ChildActivity;
import com.example.ecoleenligne.EvaluationActivity;
import com.example.ecoleenligne.ListeQuestionsActivity;
import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.ProfileActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.SubscriptionActivity;
import com.example.ecoleenligne.model.Kinship;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        user_id.setText(""+kinship.getStudent().getId());
        TextView name = view.findViewById(R.id.name);
        name.setText(kinship.getStudent().getFirstname()+ " " + kinship.getStudent().getLastname());
        TextView email = view.findViewById(R.id.email);
        email.setText(kinship.getStudent().getEmail());
        TextView level = view.findViewById(R.id.level);
        level.setText(kinship.getStudent().getLevel());
        ImageView image = (ImageView)view.findViewById(R.id.image);
        //set avatar for each student
        String path_img = MainActivity.IP_myspace +"/TER.git/public/upload/picture/"+ kinship.getStudent().getImage();
        Picasso.get().load(path_img)
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(image);

        Dialog dialog_delete = new Dialog(context);
        dialog_delete.setContentView(R.layout.popup_negative_child_popup);

        // button delete child
        Button child_delete_btn = view.findViewById(R.id.child_delete_btn);
        child_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog_delete.findViewById(R.id.negative_close).setOnClickListener(vv -> dialog_delete.dismiss());
                Objects.requireNonNull(dialog_delete.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_delete.show();
            }
        });

        // button subscribe child
        Button child_subscribe_btn = view.findViewById(R.id.child_subscribe_btn);
        child_subscribe_btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubscriptionActivity.class);
            SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            sharedpreferences = context.getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("parent_student_selected", ""+kinship.getStudent().getId());
            editor.commit();
            context.startActivity(intent);
        });

        // button delete child
        RelativeLayout child_select = view.findViewById(R.id.child_select);
        child_select.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("id", ""+ kinship.getStudent().getId());
            context.startActivity(intent);
        });

        // button confirm delete child
        Button child_delete_confirm = dialog_delete.findViewById(R.id.negative_button);
        child_delete_confirm.setOnClickListener(v3 -> {
            String url =  MainActivity.IP+"/api/tutor/child/delete/"+ kinship.getId();
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        Toast.makeText(context, " The child is successfully deleted", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(context, ChildActivity.class);
                        context.startActivity(intent1);
                    },
                    error -> new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(error.getMessage())
                            .show()
            );
            queue.add(request);

        });


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