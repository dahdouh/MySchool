package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Kinship;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.KinshipListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChildActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferences sharedpreferences;

    private KinshipListAdapter kinshipListAdapter;
    List<Kinship> kinships = new ArrayList<Kinship>();

    String email_data;

   // Popup dialog
    Button positive, negative;
    Dialog dialog, dialog_child_form;
    ImageView closePoppupPositiveImg, closePoppupNegativeImg;
    TextView positive_title, positive_content, negative_title, negative_content, formChildError;
    Button positive_button, negative_button;
    ImageButton child_add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.parent_children);
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.linkedin)));
        //Transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.kinshipListAdapter = new KinshipListAdapter(this, kinships);
        ListView listView = findViewById(R.id.list_students);
        listView.setAdapter(kinshipListAdapter);

        //get list of children from server
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        String url = MainActivity.IP + "/api/tutor/child/list/"+user_connected_id;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        /*--------------- allow connection with https and ssl-------------*/
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.length()>0) {
                        String emailResponse = response.getString("email");
                        String tutor_id = response.getString("id");
                        String tutor_firstName = response.getString("firstName");
                        String tutor_lastName = response.getString("lastName");
                        User tutor = new User(Integer.parseInt(tutor_id), tutor_firstName, tutor_lastName);

                        kinships = new ArrayList<>(response.getJSONArray("kinshipStudents").length());
                        for (int j = 0; j < response.getJSONArray("kinshipStudents").length(); j++) {
                            JSONObject kindshipJsonObject = response.getJSONArray("kinshipStudents").getJSONObject(j);
                            String kinship_id = kindshipJsonObject.getString("id");

                            JSONObject studentJson = kindshipJsonObject.getJSONObject("student");
                            String student_id = studentJson.getString("id");
                            String student_firstName = studentJson.getString("firstName");
                            String student_lastName = studentJson.getString("lastName");
                            String student_email = studentJson.getString("email");
                            String student_image = studentJson.getString("image");

                            User student = new User();
                            if (studentJson.getJSONArray("subscriptions").length() > 0) {
                                JSONObject studentSubscription = studentJson.getJSONArray("subscriptions").getJSONObject(0);
                                JSONObject levelJson = studentSubscription.getJSONObject("level");
                                String student_level = levelJson.getString("name");
                                String student_subscription_type = studentSubscription.getString("type");
                                student = new User(Integer.parseInt(student_id), student_firstName, student_lastName, student_email, student_level, student_image, student_subscription_type);
                            } else {
                                student = new User(Integer.parseInt(student_id), student_firstName, student_lastName, student_email);
                            }

                            kinships.add(new Kinship(Integer.parseInt(kinship_id), tutor, student));
                        }
                    }

                    dialog = new Dialog(context);
                    if(kinships.isEmpty()){
                        actionNegative();
                        negative_title = dialog.findViewById(R.id.negative_title);
                        negative_content = dialog.findViewById(R.id.negative_content);
                        negative_button = dialog.findViewById(R.id.negative_button);
                        negative_button.setOnClickListener(v -> {
                            dialog.dismiss();
                        });
                        negative_title.setText(R.string.parent_children);
                        negative_content.setText(R.string.parent_child_not_found);
                        negative_button.setText(R.string.button_ok);
                    }
                    kinshipListAdapter.setKinships(kinships);
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
            public void onErrorResponse(VolleyError e) {
                new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage(e.toString())
                        .show();
            }
        });
        queue.add(request);

        //Add child dialog popup
        dialog_child_form = new Dialog(context);
        dialog_child_form.setContentView(R.layout.popup_positive_child_add);
        Button child_add_btn = findViewById(R.id.child_add_btn);
        child_add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*dialog_child_form.findViewById(R.id.positive_close).setOnClickListener(vv -> dialog_child_form.dismiss());
                Objects.requireNonNull(dialog_child_form.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_child_form.show();
                 */
                Intent intent = new Intent(context, StudentAddActivity.class);
                context.startActivity(intent);
            }
        });

        //Add child dialog form
        formChildError = dialog_child_form.findViewById(R.id.formChildError);
        final Button child_add_form_btn = dialog_child_form.findViewById(R.id.add_form_button);
        child_add_form_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog_child_form.findViewById(R.id.positive_close).setOnClickListener(v2 -> dialog_child_form.dismiss());

                Button add_form_button = dialog_child_form.findViewById(R.id.add_form_button);
                add_form_button.setOnClickListener(v3 -> {
                    TextInputLayout emailInput = (TextInputLayout) dialog_child_form.findViewById(R.id.email);
                    String email = emailInput.getEditText().getText().toString();
                    if(!validateEmail()){
                        return;
                    } else {
                        addChild(email);
                    }
                });

                Objects.requireNonNull(dialog_child_form.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_child_form.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(context, DashboardParentActivity.class);
                context.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private Boolean validateEmail() {
        TextInputLayout emailInput = (TextInputLayout) dialog_child_form.findViewById(R.id.email);
        email_data = emailInput.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email_data.isEmpty()) {
            emailInput.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!email_data.matches(emailPattern)) {
            emailInput.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            emailInput.setError(null);
            emailInput.setErrorEnabled(false);
            return true;
        }
    }

    /*------------------ Authentification (RESTful) ----------------*/
    public void addChild(String email) {
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);
            String url = MainActivity.IP + "/api/tutor/add/child/"+ email +"/"+user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);
            JSONObject jsonObject = new JSONObject();
            /*--------------- allow connection with https and ssl-------------*/
            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        String emailResponse = response.getString("email");
                        if (emailResponse.equals("not found")) {
                            formChildError.setText("Le compte étudiant associé à cette adresse mail n'existe pas");
                        } else if(emailResponse.equals("already your child")){
                            formChildError.setText("Vous avez déjà associé ce compte étudiant");
                        } else if(emailResponse.equals("already associated")){
                            formChildError.setText("Ce compte étudiant a déjà été associé à un compte parent");
                        } else {
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
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
                public void onErrorResponse(VolleyError e) {
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(e.toString())
                            .show();
                }
            });
            queue.add(request);
    }



    private void actionPositive() {
        dialog.setContentView(R.layout.popup_positive);
        closePoppupPositiveImg = dialog.findViewById(R.id.positive_close);
        closePoppupPositiveImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void actionNegative() {
        dialog.setContentView(R.layout.popup_negative);
        closePoppupNegativeImg = dialog.findViewById(R.id.negative_close);
        closePoppupNegativeImg.setOnClickListener(v -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}