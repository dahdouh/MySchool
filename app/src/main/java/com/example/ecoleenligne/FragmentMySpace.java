package com.example.ecoleenligne;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.ecoleenligne.model.CourseContent;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Topic;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.CourseContentListAdapter;
import com.example.ecoleenligne.util.ForumListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMySpace extends Fragment {

    private Context context;
    private SharedPreferences sharedpreferences;
    private CourseContentListAdapter courseContentListAdapter;
    ListView listview;
    List<CourseContent> myDocuements = new ArrayList<CourseContent>();

    Dialog dialog;
    ImageView closePoppupNegativeImg;
    TextView negative_title, negative_content;
    Button negative_button;

    public FragmentMySpace() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myspace, container, false);
        this.courseContentListAdapter = new CourseContentListAdapter(getActivity(), myDocuements);
        ListView studentsListView = view.findViewById(R.id.list_students);
        studentsListView.setAdapter(courseContentListAdapter);

        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
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
                            Toast.makeText(context, "User not exist", Toast.LENGTH_LONG).show();
                        } else {
                            //String fullname_data = response.getString("firstName")+" "+response.getString("lastName");
                            //String birthday_data = response.getString("date_birth");

                            myDocuements = new ArrayList<>(response.getJSONArray("desktopDocuments").length());
                            for (int i = 0; i < response.getJSONArray("desktopDocuments").length(); i++) {
                                JSONObject item = response.getJSONArray("desktopDocuments").getJSONObject(i);
                                // String id = item.getString("_id");
                                String id = item.getString("id");
                                String title = item.getString("name");
                                String path = MainActivity.IP_myspace +"/TER.git/public/"+ item.getString("file");
                                String string = item.getString("file");
                                String[] parts = string.split("\\.");
                                String extension = parts[1];
                                String type = "";
                                if(extension.equals("pdf"))
                                    type = ""+1;
                                else if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png"))
                                    type = ""+3;

                                myDocuements.add(new CourseContent(Integer.parseInt(id), title, path, type));
                            }
                            courseContentListAdapter.setcourseContents(myDocuements);
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

    public void backStep(View view) {
        getActivity().finish();
    }


}
