package com.example.ecoleenligne;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Historic;
import com.example.ecoleenligne.util.HistoricListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLog extends Fragment {


    private Context context;
    private HistoricListAdapter historicListAdapter;
    ListView listview;
    List<Historic> historics = new ArrayList<Historic>();
    SharedPreferences sharedpreferences;

    public FragmentLog() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        // List of subscriptions
        this.historicListAdapter = new HistoricListAdapter(getActivity(), historics);
        ListView subscriptionsListView = view.findViewById(R.id.list_students);
        subscriptionsListView.setAdapter(historicListAdapter);

        // check if there is connection
        if(MainActivity.MODE.equals("ONLINE")) {
            /*------------------------ ONLINE MODE  ---------------------*/
            /*------------------  get profile of user connected from server  -----------------*/

            /*--------------get user from session --------------*/
            sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);

            /*-------------- call restful webservices ----------*/
            String url =  MainActivity.IP+"/api/history/list/"+ user_connected;
            //Toast.makeText(context, "ddddddd "+ url, Toast.LENGTH_SHORT).show();
            /*-------------------- get subscriptions from server RESTful ------------------*/
            RequestQueue queueUserConnected = Volley.newRequestQueue(context);
            JsonArrayRequest requestUserConnected = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            if (response.length() != 0) {
                                historics = new ArrayList<>(response.length());
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject item = response.getJSONObject(i);
                                    String id = item.getString("id");
                                    String description = item.getString("description");
                                    String date = item.getString("date");
                                    historics.add(new Historic(Integer.parseInt(id), description, date));
                                }
                                historicListAdapter.setHistorics(historics);
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

        } else  {}

        return view;
    }

}
