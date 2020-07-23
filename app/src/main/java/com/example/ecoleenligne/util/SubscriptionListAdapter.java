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
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.DashboardActivity;
import com.example.ecoleenligne.FragmentSubscription;
import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Subscription;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Subscription> subscriptions = new ArrayList<Subscription>();
    Context context;

    public SubscriptionListAdapter(Context context, List<Subscription> subscriptions) {
        this.subscriptions.addAll(subscriptions);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Subscription subscription = (Subscription) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.subscription_item, null);
        }

        TextView user_id = view.findViewById(R.id.user_id);
        user_id.setText(""+ subscription.getSubject().getName());
        TextView name = view.findViewById(R.id.name);
        name.setText(subscription.getLevel().getName());

        TextView price = view.findViewById(R.id.price);
        price.setText("Prix: " + subscription.getPrice()+ "€");


        final int subscription_id = subscription.getId();
        final Button subscription_delete_btn = view.findViewById(R.id.subscription_delete_btn);
        subscription_delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent1 = new Intent(context, DashboardActivity.class);
                intent1.putExtra("ToSubscription", "1");
                context.startActivity(intent1);
                /*
                String url =  MainActivity.IP+"/api/subscription/delete/"+ subscription_id;
                //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
                // get subscriptions from server RESTful
                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                JsonObjectRequest requestUserConnected = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(context, " You are successfully unscribed", Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(context, SubscriptionListActivity.class);
                                context.startActivity(intent1);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.getMessage())
                                        .show();
                            }
                        }
                );
                queueUserConnected.add(requestUserConnected);
                */
            }

        });

        /*
        final Button quiz_btn = view.findViewById(R.id.quiz);
        quiz_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("course_id", course_id);
                context.startActivity(intent);
            }
        });

         */




        return view;
    }

    @Override
    public Object getItem(int position) {
        return subscriptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return subscriptions.size();
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }
}