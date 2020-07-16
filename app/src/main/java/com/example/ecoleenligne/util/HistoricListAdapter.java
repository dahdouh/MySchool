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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.MainActivity;
import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Historic;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoricListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Historic> historics = new ArrayList<Historic>();
    Context context;

    public HistoricListAdapter(Context context, List<Historic> historics) {
        this.historics.addAll(historics);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Historic historic = (Historic) getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.historic_item, null);
        }

        TextView name = view.findViewById(R.id.name);
        name.setText("" + historic.getDescription());

        String string = historic.getDate();
        String[] parts = string.split("T");
        String date1 = parts[0]; // dd/mm/YYYY
        String date2 = parts[1]; // dd/mm/YYYY
        String[] date3 = date2.split("\\+");
        String hour = date3[0];
        TextView desc = view.findViewById(R.id.desc);
        desc.setText(date1);
        TextView desc2 = view.findViewById(R.id.desc2);
        desc2.setText("at  "+hour);

        return view;
    }

    @Override
    public Object getItem(int position) {
        return historics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return historics.size();
    }

    public void setHistorics(List<Historic> historics) {
        this.historics = historics;
        notifyDataSetChanged();
    }
}