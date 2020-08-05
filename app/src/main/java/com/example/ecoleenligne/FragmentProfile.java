package com.example.ecoleenligne;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.CacheControl;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {

    Context context;
    SharedPreferences sharedpreferences;

    TextView fullname, role, email, birthday, level;
    //student avatar
    ImageView image;

    public FragmentProfile() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //change background of profile according to the profile of connected user
        RelativeLayout profile_relative_layout = view.findViewById(R.id.profile_layout);
        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);
        if(user_profile_data.equals("ROLE_TUTOR"))
            profile_relative_layout.setBackgroundResource(R.color.linkedin);
        else
            profile_relative_layout.setBackgroundResource(R.color.accent);

        ImageView img = (ImageView) view.findViewById(R.id.edit);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(context, ProfileUpdateActivity.class);
                context.startActivity(intent);
            }
        });

        //image upload
        image = (ImageView)view.findViewById(R.id.image);
        fullname = view.findViewById(R.id.fullname);
        role = view.findViewById(R.id.role);
        email = view.findViewById(R.id.email);
        birthday = view.findViewById(R.id.birthday);
        level = view.findViewById(R.id.level);


        final Button logout_btn = view.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(v -> {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Toast.makeText(context, getString(R.string.logout_success), Toast.LENGTH_LONG).show();

            Intent intent=new Intent(context, WelcomeActivity.class);
            context.startActivity(intent);
        });

        // get user from session
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        // If child selected, by parent, show his profile
        Intent intent = getActivity().getIntent();
        String parent_student_selected = sharedpreferences.getString("parent_student_selected", null);
        if(parent_student_selected != null) {
            user_connected_id = parent_student_selected;
        }

        /*-------------- check if there is connection--------------*/
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------  get profile from server  -----------------*/
            String url = MainActivity.IP + "/api/profile/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);

            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String user_exist = response.getString("email");
                        if (user_exist.equals("not found")) {
                            Toast.makeText(context, "=====>  User not exist", Toast.LENGTH_LONG).show();
                        } else {
                            //uplaod user avatar
                            String path_img = MainActivity.IP_myspace +"/TER.git/public/upload/picture/"+ response.getString("image");
                            Picasso.get().load(path_img)
                                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(image);
                            String fullname_data = response.getString("firstName")+" "+response.getString("lastName");
                            String birthday_data = response.getString("date_birth");
                            String[] parts = birthday_data.split("T");
                            String date1 = parts[0]; // dd/mm/YYYY
                            if(response.getJSONArray("subscriptions").length()>0) {
                                JSONObject studentSubscription = response.getJSONArray("subscriptions").getJSONObject(0);
                                JSONObject levelJson = studentSubscription.getJSONObject("level");
                                String student_level = levelJson.getString("name");
                                level.setText(student_level);
                            } else {
                                level.setVisibility(View.GONE);
                            }

                            fullname.setText(fullname_data);
                            role.setText(sharedpreferences.getString(MainActivity.Role, null));
                            email.setText(sharedpreferences.getString(MainActivity.Email, null));
                            birthday.setText(date1);

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
                            .setMessage(R.string.server_restful_error)
                            .show();
                }
            });

            queue.add(request);
        } else {

        }

        return view;
    }

}
