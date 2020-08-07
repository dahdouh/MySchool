package com.example.ecoleenligne;


import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Subject;
import com.example.ecoleenligne.model.Topic;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.ForumListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForum extends Fragment {

    private Context context;

    SharedPreferences sharedpreferences;
    private ForumListAdapter forumListAdapter;
    ListView listview;
    List<Topic> topics = new ArrayList<Topic>();

    Dialog dialog_forum_topic_add;
    String subject_data, user_level ="1", title_data, content_data;
    Spinner student_subject_spinner;

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
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.length() != 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject item = response.getJSONObject(i);
                                    String topic_exist = item.getString("title");
                                    if (!topic_exist.equals("not found")) {
                                        String topic_id = item.getString("id");
                                        String topic_title = item.getString("title");
                                        String topic_date = item.getString("date");

                                        JSONObject subjectJsonObject = item.getJSONObject("subject");
                                        String subject_id = subjectJsonObject.getString("id");
                                        String subject_name = subjectJsonObject.getString("name");
                                        Subject subject = new Subject(Integer.parseInt(subject_id), subject_name);

                                        JSONObject authorJsonObject = item.getJSONObject("author");
                                        String author_id = authorJsonObject.getString("id");
                                        String author_firstName = authorJsonObject.getString("firstName");
                                        String author_lastName = authorJsonObject.getString("lastName");

                                        if(authorJsonObject.getJSONArray("subscriptions").length()>0) {
                                            JSONObject studentSubscription = authorJsonObject.getJSONArray("subscriptions").getJSONObject(0);
                                            JSONObject levelJson = studentSubscription.getJSONObject("level");
                                            user_level = levelJson.getString("id");

                                        }
                                        User author = new User(Integer.parseInt(author_id), author_firstName, author_lastName);

                                        topics.add(new Topic(Integer.parseInt(topic_id), topic_title, topic_date, subject, author));
                                    }
                                }
                                forumListAdapter.setTopics(topics);
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

        final Button add_topic_btn = view.findViewById(R.id.add_topic_btn);
        add_topic_btn.setOnClickListener(v -> {
            //Add child dialog popup
            dialog_forum_topic_add = new Dialog(context);
            dialog_forum_topic_add.setContentView(R.layout.popup_positive_forum_topic_add);
            ImageView closePoppupNegativeImg = dialog_forum_topic_add.findViewById(R.id.positive_close);
            closePoppupNegativeImg.setOnClickListener(v2 -> dialog_forum_topic_add.dismiss());
            Objects.requireNonNull(dialog_forum_topic_add.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_forum_topic_add.show();

            // Spinner for topic subject
            student_subject_spinner = (Spinner) dialog_forum_topic_add.findViewById(R.id.student_subject_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.matiere_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            student_subject_spinner.setAdapter(adapter);
            student_subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subject_data = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });


            final Button add_form_topic_button = dialog_forum_topic_add.findViewById(R.id.add_form_topic_btn);
            add_form_topic_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        if(!validateTitle() || !validateContent()){
                            return;
                        } else {
                            addTopic(title_data, content_data, subject_data, user_level);
                        }


                }
            });
        });

        return view;
    }

    private Boolean validateTitle() {
        TextInputLayout titleInput = (TextInputLayout) dialog_forum_topic_add.findViewById(R.id.title);
        title_data = titleInput.getEditText().getText().toString();
        if (title_data.isEmpty()) {
            titleInput.setError(getString(R.string.forum_post_add_title_error));
            return false;
        } else{
            return true;
        }
    }

    private Boolean validateContent() {
        TextInputLayout titleInput = (TextInputLayout) dialog_forum_topic_add.findViewById(R.id.content);
        content_data = titleInput.getEditText().getText().toString();
        if (content_data.isEmpty()) {
            titleInput.setError(getString(R.string.forum_post_add_content_error));
            return false;
        } else{
            return true;
        }
    }

    // send topic to server (RESTful)
    public void addTopic(String title, String content, String subject, String user_level) {
        sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String url = MainActivity.IP + "/api/forum/topic/add/"+ user_connected_id +"/"+ user_level +"/"+ subject +"/"+ title +"/"+ content;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                response -> {
                    dialog_forum_topic_add.cancel();
                    Intent intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra("ToForum", "1");
                    startActivity(intent);
                },
                e -> new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(e.toString())
                .show());
        queue.add(request);
    }

}
