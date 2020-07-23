package com.example.ecoleenligne;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.example.ecoleenligne.util.SubjectListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCourse extends Fragment {

    Context context;
    /*------------------ Menu ----------------*/
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menu;
    TextView textView;
    GridLayout mainGrid;
    TextView user_name, user_profile;
    SharedPreferences sharedpreferences;

    private SubjectListAdapter subjectListAdapter;
    ListView listview;
    List<Subject> subjects = new ArrayList<Subject>();

    /*------ offline mode -------*/
    SQLiteHelper sqLiteHelper;


    public FragmentCourse() {
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

        View view = inflater.inflate(R.layout.fragment_course, container, false);

        user_name = view.findViewById(R.id.user_name);
        user_profile = view.findViewById(R.id.user_profile);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);

        //UnoFragment.bind(this, view);

        this.subjectListAdapter = new SubjectListAdapter(getActivity(), subjects);
        ListView subjectsListView = view.findViewById(R.id.list_subjects);
        subjectsListView.setAdapter(subjectListAdapter);



        //check if there is connection
        if(MainActivity.MODE.equals("ONLINE")) {
            // get user from session
            sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            // call restful webservices
            String url =  MainActivity.IP+"/api/subject/list";
            //get list of subject
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            // allow connection with https and ssl
            HttpsTrustManager.allowAllSSL();
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                if (response.length() != 0) {
                                    subjects = new ArrayList<>(response.length());
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject item = response.getJSONObject(i);
                                        String id = item.getString("id");
                                        String name = item.getString("name");
                                        String icon = item.getString("icon");
                                        //String content = item.getString("content");
                                        subjects.add(new Subject(Integer.parseInt(id), name, icon));
                                        //for(Course c: courses)
                                        //Toast.makeText(context, "ddddddd "+ c.getName(), Toast.LENGTH_SHORT).show();

                                    }
                                    subjectListAdapter.setSubjects(subjects);
                                }

                            } catch (JSONException error) {
                                new AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage(error.toString())
                                        .show();
                            }
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

        } else  {

        }



        sharedpreferences = this.getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);



        if(MainActivity.MODE.equals("ONLINE")) {
            //  get profile of user connected from server
            String url = MainActivity.IP+"/api/user/"+user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            // allow connection with https and ssl
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url,jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("email");
                        if(user_exist.equals("not found")) {
                            Toast.makeText(context, "=====>  User not found", Toast.LENGTH_LONG).show();
                        } else {
                            String fullname_data = response.getString("firstName")+" "+response.getString("lastName");
                            String role = sharedpreferences.getString(MainActivity.Role, null);
                            user_name.setText(fullname_data);
                            user_profile.setText(role);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(context)
                                .setTitle("Error")
                                .setMessage(e.toString())
                                .show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(error.toString())
                            .show();
                }
            });
            queue.add(request);
        } else  {

        }



        // Inflate the layout for this fragment
        return view;
    }

}
