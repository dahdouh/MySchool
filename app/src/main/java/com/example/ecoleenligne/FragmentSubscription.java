package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Level;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Subscription;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.example.ecoleenligne.util.SubscriptionListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSubscription extends Fragment {

    Context context;
    SharedPreferences sharedpreferences;

    private SubscriptionListAdapter subscriptionListAdapter;
    ListView listview;
    List<Subscription> subscriptions = new ArrayList<Subscription>();

    public FragmentSubscription() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);

        /*------------------------ Button add subscription ---------------------*/
        final ImageButton student_add_imageButton = view.findViewById(R.id.student_add_imageButton);
        student_add_imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent=new Intent(context, SubscriptionActivity.class);
                context.startActivity(intent);
            }
        });


        /*------------------------ List of subscriptions  ---------------------*/
        this.subscriptionListAdapter = new SubscriptionListAdapter(getActivity(), subscriptions);
        ListView subscriptionsListView = view.findViewById(R.id.list_students);
        subscriptionsListView.setAdapter(subscriptionListAdapter);

        String user_connected = sharedpreferences.getString(MainActivity.Id, null);
        // check if there is connection
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            Intent intent = getActivity().getIntent();
            final String parent_id = intent.getStringExtra("parent_id");
            // If parent, list all his students susbscriptions, otherwise show only student's connected subscriptions
            if(parent_id != null) {
                String url = MainActivity.IP + "/api/tutor/child/list/"+parent_id;
                RequestQueue queue = Volley.newRequestQueue(context);
                JSONObject jsonObject = new JSONObject();
                /*--------------- allow connection with https and ssl-------------*/
                HttpsTrustManager.allowAllSSL();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                    try{

                        List<String> students_ids = new ArrayList<String>();
                        for (int j = 0; j < response.getJSONArray("kinshipStudents").length(); j++) {
                            JSONObject studentJson = response.getJSONArray("kinshipStudents").getJSONObject(j).getJSONObject("student");
                            String student_id = studentJson.getString("id");
                            students_ids.add(student_id);
                        }

                        if(students_ids.size()>0){
                            for (String student_id: students_ids) {
                                //Toast.makeText(context, "########## "+ id, Toast.LENGTH_LONG).show();

                                String url2 = MainActivity.IP + "/api/subscription/list/" + student_id;
                                //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
                                /*-------------------- get subscriptions from server RESTful ------------------*/
                                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                                JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url2, null,
                                        res -> {
                                            try {
                                                if (res.length() != 0) {
                                                    for (int i = 0; i < res.length(); i++) {
                                                        JSONObject item = res.getJSONObject(i);
                                                        String subscription_id = item.getString("id");
                                                        String subscription_price = item.getString("price");

                                                        JSONObject levelJsonObject = item.getJSONObject("level");
                                                        String level_id = levelJsonObject.getString("id");
                                                        String level_name = levelJsonObject.getString("name");
                                                        for (int j = 0; j < item.getJSONArray("subjects").length(); j++) {
                                                            JSONObject subjectJsonObject = item.getJSONArray("subjects").getJSONObject(j);
                                                            String subject_id = subjectJsonObject.getString("id");
                                                            String subject_name = subjectJsonObject.getString("name");
                                                            Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);
                                                            Level level = new Level(Integer.parseInt(level_id), level_name);
                                                            subscriptions.add(new Subscription(Integer.parseInt(subscription_id), subject, subscription_price, level));
                                                        }
                                                    }
                                                    subscriptionListAdapter.setSubscriptions(subscriptions);
                                                }

                                            } catch (JSONException error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(error.toString())
                                                        .show();
                                            }
                                        },
                                        e -> new AlertDialog.Builder(context)
                                                .setTitle("Error")
                                                .setMessage(e.getMessage())
                                                .show()
                                );
                                queueUserConnected.add(requestUserConnected);

                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.toString())
                                .show();
                    }
                }, error -> new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage(error.toString())
                        .show());
                queue.add(request);



            } else {
                // If student
                String url = MainActivity.IP + "/api/subscription/list/" + user_connected;
                //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
                /*-------------------- get subscriptions from server RESTful ------------------*/
                RequestQueue queueUserConnected = Volley.newRequestQueue(context);
                JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                if (response.length() != 0) {
                                    subscriptions = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String subscription_id = item.getString("id");
                                        String subscription_price = item.getString("price");

                                        JSONObject levelJsonObject = item.getJSONObject("level");
                                        String level_id = levelJsonObject.getString("id");
                                        String level_name = levelJsonObject.getString("name");
                                        for (int j = 0; j < item.getJSONArray("subjects").length(); j++) {
                                            JSONObject subjectJsonObject = item.getJSONArray("subjects").getJSONObject(j);
                                            String subject_id = subjectJsonObject.getString("id");
                                            String subject_name = subjectJsonObject.getString("name");
                                            Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);
                                            Level level = new Level(Integer.parseInt(level_id), level_name);
                                            subscriptions.add(new Subscription(Integer.parseInt(subscription_id), subject, subscription_price, level));
                                        }
                                    }
                                    subscriptionListAdapter.setSubscriptions(subscriptions);
                                }

                            } catch (JSONException error) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.toString())
                                        .show();
                            }
                        },
                        error -> new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(error.getMessage())
                                .show()
                );
                queueUserConnected.add(requestUserConnected);
            }

        } else  {

        }

        return view;
    }

}
