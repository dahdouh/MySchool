package com.example.ecoleenligne;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Topic;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.ForumListAdapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForum extends Fragment {

    private Context context;

    SharedPreferences sharedpreferences;
    private ForumListAdapter forumListAdapter;
    ListView listview;
    List<Topic> topics = new ArrayList<Topic>();

    public FragmentForum() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.forumListAdapter = new ForumListAdapter(getActivity(), topics);
        ListView forumListView = view.findViewById(R.id.list_students);
        forumListView.setAdapter(forumListAdapter);

        // check if there is connection
        if(MainActivity.MODE.equals("ONLINE")) {
            sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);
            // call restful webservices
            String url =  MainActivity.IP+"/api/forum/list/"+ user_connected;
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            JsonObjectRequest requestUserConnected = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.length() != 0) {
                                String topic_exist = response.getString("title");
                                if (!topic_exist.equals("not found")) {
                                    topics = new ArrayList<>(response.length());
                                    String topic_id = response.getString("id");
                                    String topic_title = response.getString("title");
                                    String topic_date = response.getString("date");

                                    JSONObject subjectJsonObject = response.getJSONObject("subject");
                                    String subject_id = subjectJsonObject.getString("id");
                                    String subject_name = subjectJsonObject.getString("name");
                                    Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);

                                    JSONObject authorJsonObject = response.getJSONObject("author");
                                    String author_id = authorJsonObject.getString("id");
                                    String author_firstName = authorJsonObject.getString("firstName");
                                    String author_lastName = authorJsonObject.getString("lastName");
                                    User author = new User(Integer.parseInt(author_id), author_firstName, author_lastName);

                                    topics.add(new Topic(Integer.parseInt(topic_id), topic_title, topic_date, subject, author));

                                    forumListAdapter.setTopics(topics);
                                }
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

        } else {}

        return view;
    }

}
