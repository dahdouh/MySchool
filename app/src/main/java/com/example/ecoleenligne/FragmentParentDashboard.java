package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.example.ecoleenligne.util.SubjectListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentParentDashboard extends Fragment {

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


    public FragmentParentDashboard() {
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
        View view = inflater.inflate(R.layout.fragment_parent_dashboard, container, false);

        user_name = view.findViewById(R.id.user_name);
        user_profile = view.findViewById(R.id.user_profile);
        //image upload
        image = (ImageView)view.findViewById(R.id.image);
        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);

        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        LinearLayout child = (LinearLayout) view.findViewById(R.id.child);
        LinearLayout subscription = (LinearLayout) view.findViewById(R.id.subscription);
        LinearLayout lbhist = (LinearLayout) view.findViewById(R.id.rlhist);
        LinearLayout recommendation = (LinearLayout) view.findViewById(R.id.recommendation);

        child.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChildActivity.class);
            startActivity(intent);
        });

        subscription.setOnClickListener(v -> {
            if(MainActivity.MODE.equals("ONLINE")) {
                Intent intent1 = new Intent(context, DashboardParentActivity.class);
                intent1.putExtra("ToSubscription", "1");
                context.startActivity(intent1);
            } else {
                Toast toast = Toast.makeText(context, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                View view12 = toast.getView();
                view12.setBackgroundColor(Color.parseColor("#ff0040"));
                toast.show();
            }
        });

        lbhist.setOnClickListener(v -> {
            if(MainActivity.MODE.equals("ONLINE")) {
                Intent intent1 = new Intent(context, DashboardParentActivity.class);
                intent1.putExtra("ToLog", "1");
                context.startActivity(intent1);
            } else {
                Toast toast = Toast.makeText(context, Html.fromHtml("<font color='#FFFFFF'><b>"+ getString(R.string.connection_msg) +"</b></font>"), Toast.LENGTH_SHORT);
                View view1 = toast.getView();
                view1.setBackgroundColor(Color.parseColor("#ff0040"));
                toast.show();
            }
        });

        recommendation.setOnClickListener(v -> {
            Intent intent1 = new Intent(context, DashboardParentActivity.class);
            intent1.putExtra("ToProfile", "1");
            context.startActivity(intent1);
        });







        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/
            String url = MainActivity.IP+"/api/user/"+user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
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
                                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
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
            User user= sqLiteHelper.getUserByIdFromDb(Integer.parseInt(user_connected_id));
            String fullname_data = user.getFirstname()+" "+user.getLastname();
            int compte_id = user.getCompte_id();
            Compte compte = sqLiteHelper.getCompteByIdFromDb(compte_id);
            int profile_id = compte.getProfile_id();
            Profile profile = sqLiteHelper.getProfileByIdFromDb(profile_id);
            String profile_role_data = profile.getLibelle();
            user_name.setText(fullname_data);
            user_profile.setText(profile_role_data);
        }



        // Inflate the layout for this fragment
        return view;
    }

}
