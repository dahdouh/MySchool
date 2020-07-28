package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
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
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.example.ecoleenligne.util.SubjectListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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
    //student avatar
    ImageView image;
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
        //image upload
        image = (ImageView)view.findViewById(R.id.image);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);

        this.subjectListAdapter = new SubjectListAdapter(getActivity(), subjects);
        ListView subjectsListView = view.findViewById(R.id.list_subjects);
        subjectsListView.setAdapter(subjectListAdapter);

        //get list of courses for each selected subject
        subjectsListView.setOnItemClickListener((parent, view1, position, id) -> {
            String subject_id = ((TextView) view1.findViewById(R.id.subject_id)).getText().toString();
            String subject_name = ((TextView) view1.findViewById(R.id.name)).getText().toString();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("subject_id", ""+ subject_id);
            editor.putString("subject_name", ""+ subject_name);
            editor.commit();

            Intent intent = new Intent(context, ListeCoursActivity.class);
            context.startActivity(intent);
        });




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
                            //set user avatar
                            String path_img = MainActivity.IP_myspace +"/TER.git/public/upload/picture/"+ response.getString("image");
                            Picasso.get().load(path_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .into(image);
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
