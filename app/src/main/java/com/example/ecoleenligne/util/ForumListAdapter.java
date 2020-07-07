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

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.PostActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.SubscriptionListActivity;
import com.example.ecoleenligne.model.Topic;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Topic> topics = new ArrayList<Topic>();
    Context context;

    public ForumListAdapter(Context context, List<Topic> topics) {
        this.topics.addAll(topics);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Topic topic = (Topic) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.forum_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        user_id.setText(""+ topic.getTitle());
        TextView name = view.findViewById(R.id.name);
        name.setText(topic.getAuthor().getFirstname()+ " "+topic.getAuthor().getLastname());
        TextView date = view.findViewById(R.id.date);
        String string = topic.getDate();
        String[] parts = string.split("T");
        String date1 = parts[0]; // dd/mm/YYYY
        String date2 = parts[1];
        String[] date3 = date2.split("\\+");
        String hour = date3[0];
        TextView desc = view.findViewById(R.id.desc);
        date.setText(date1 +"  "+hour);

        TextView subject = view.findViewById(R.id.subject);
        subject.setText(topic.getSubject().getName());



        final int topic_id = topic.getId();
        final String topic_subject = topic.getSubject().getName();
        //Toast.makeText(context, "##### "+ topic_id, Toast.LENGTH_LONG).show();
        final Button subscription_delete_btn = view.findViewById(R.id.subscription_delete_btn);
        subscription_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("id", topic_id);
                intent.putExtra("subject", topic_subject);
                context.startActivity(intent);
            }

        });


        return view;
    }

    @Override
    public Object getItem(int position) {
        return topics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }
}