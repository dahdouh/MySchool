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

import com.example.ecoleenligne.PostActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Post> posts = new ArrayList<Post>();
    Context context;

    public PostListAdapter(Context context, List<Post> posts) {
        this.posts.addAll(posts);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Post post = (Post) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.post_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        user_id.setText(""+ post.getContent());
        TextView name = view.findViewById(R.id.name);
        name.setText(post.getAuthor().getFirstname()+ " "+post.getAuthor().getLastname());
        TextView date = view.findViewById(R.id.date);
        String string = post.getDate();
        String[] parts = string.split("T");
        String date1 = parts[0]; // dd/mm/YYYY
        String date2 = parts[1];
        String[] date3 = date2.split("\\+");
        String hour = date3[0];
        TextView desc = view.findViewById(R.id.desc);
        date.setText(date1 +"  "+hour);

        //TextView subject = view.findViewById(R.id.subject);
        //subject.setText(post.get().getName());

        return view;
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
}