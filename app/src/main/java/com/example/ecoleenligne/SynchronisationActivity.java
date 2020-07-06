package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.Recommendation;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SynchronisationActivity extends AppCompatActivity /* implements Response.Listener<JSONArray>, Response.ErrorListener */{

    final Context context = this;
    SQLiteHelper sqLiteHelper;
    List<Course> list_courses_server = new ArrayList<Course>();
    List<Profile> list_profiles_server = new ArrayList<Profile>();
    List<Compte> list_comptes_server = new ArrayList<Compte>();
    List<User> list_users_server = new ArrayList<User>();

    /*------------ PROGRESS BAR -----------*/
    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronisation);
        sqLiteHelper= new SQLiteHelper(this);

        /*--------------- Synchronisation of courses ----------------*/
       /* String url =MainActivity.IP+"/api/cours";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
        queue.add(microPostsRequest);

        */

        final Button synch_btn = findViewById(R.id.synch_btn);
        synch_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*List<Course> list_courses_sqlite= sqLiteHelper.getCoursesFromDb();
                for(Course c3 : list_courses_sqlite){
                    Toast.makeText(SynchronisationActivity.this, "LLLLLLLLLLLLLLLLLLLLLL  "+ c3.getLibelle(), Toast.LENGTH_LONG).show();
                }
                */

                Intent intent=new Intent(SynchronisationActivity.this, DashboardActivity.class);
                context.startActivity(intent);
            }
        });

        /*-------------------------- Synchronisation Action Bar annimation-----------------------------*/
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);
        mProgress.setSecondaryProgress(100);
        mProgress.setMax(100);
        mProgress.setProgressDrawable(drawable);

        tv = (TextView) findViewById(R.id.tv);
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (pStatus < 100) {
                    pStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");
                        }
                    });
                    try {
                        Thread.sleep(56);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


       // SQLiteHelper.updateCourse(sqLiteHelper.getDatabase(), new Course(1, "Maths", "Cours de maths avec plusieurs chapitres: algèbre, arithmetique, etc",
         //       "https://image.shutterstock.com/image-vector/creative-hand-drawn-vector-maths-260nw-461142283.jpg", null, "50"));

        /*-------------------------- COURSE SYNCHRONIZATION --------------------------*/
        String url = MainActivity.IP+"/api/cours";
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        //JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                // String id = item.getString("_id");
                                int id = item.getInt("id");
                                String libelle = item.getString("libelle");
                                String path = item.getString("image");
                                String iden = item.getString("id");
                                String description = item.getString("description");
                                String level = item.getString("level");
                                String progress = item.getString("progress");
                                list_courses_server.add(new Course(id, libelle, description, path, level, progress));
                                /* Check if a course not in sqllite database insert it.*/
                                Course course = sqLiteHelper.getCourseByIdFromDb(id);
                                if(course.getId() == 0) {
                                    sqLiteHelper.insertCourse(sqLiteHelper.getDatabase(), new Course(id, libelle, description, path, level, progress));
                                }
                            }


                            List<Course> list_courses_sqlite= sqLiteHelper.getCoursesFromDb();
                            //Toast.makeText(SynchronisationActivity.this, "size c  "+ list_courses_sqlite.size(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(SynchronisationActivity.this, "size c2  "+ list_courses_server.size(), Toast.LENGTH_SHORT).show();

                            for(Course p: list_courses_sqlite) {
                                for(Course p2: list_courses_server) {
                                    if (p.getId() == p2.getId()) {
                                        String url_course_synch = MainActivity.IP + "/api/course/synchronize/"+ p.getId() +"/"+p.getProgress();
                                        //Toast.makeText(SynchronisationActivity.this, "cours:  "+ p.getLibelle(), Toast.LENGTH_SHORT).show();
                                        RequestQueue queueCourse_synch = Volley.newRequestQueue(context);
                                        JsonObjectRequest requestCourse_synch = new JsonObjectRequest(Request.Method.GET, url_course_synch, new JSONObject(), new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {}
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(R.string.server_restful_error)
                                                        .show();
                                            }
                                        });
                                        queueCourse_synch.add(requestCourse_synch);

                                    }
                                }
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
        queue.add(request);


        //SQLiteHelper.updateProfile(sqLiteHelper.getDatabase(), new Profile(1, "parent", "ROLE_PARENT"));

        /*-------------------------- PROFILE SYNCHRONIZATION --------------------------*/
        String url_profile = MainActivity.IP+"/profile";
        RequestQueue queueProfile = Volley.newRequestQueue(context);
        JsonArrayRequest requestProfile = new JsonArrayRequest(Request.Method.GET, url_profile, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                int id = item.getInt("id");
                                String libelle = item.getString("libelle");
                                String role = item.getString("role");
                                list_profiles_server.add(new Profile(id, libelle, role));
                                Profile profile = sqLiteHelper.getProfileByIdFromDb(id);
                                /*If there is new profile in server insert it in local database sqlite*/
                                if(profile.getId() == 0) {
                                    sqLiteHelper.insertProfile(sqLiteHelper.getDatabase(), new Profile(id, libelle, role));
                                }

                            }

                            List<Profile> list_profiles_sqlite= sqLiteHelper.getProfilesFromDb();
                            for(Profile p: list_profiles_sqlite) {
                                for(Profile p2: list_profiles_server) {
                                    if (p.getId() == p2.getId()) {
                                        String url_profile_synch = MainActivity.IP + "/profile/synchronize/" + p.getId() + "/" + p.getLibelle() + "/" + p.getRole();
                                        RequestQueue queueProfile_synch = Volley.newRequestQueue(context);
                                        JSONObject jsonObject = new JSONObject();
                                        JsonObjectRequest requestProfile_synch = new JsonObjectRequest(Request.Method.GET, url_profile_synch, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {}
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(R.string.server_restful_error)
                                                        .show();
                                            }
                                        });
                                        queueProfile_synch.add(requestProfile_synch);
                                    }
                                }
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
        queueProfile.add(requestProfile);



        //SQLiteHelper.updateCompte(sqLiteHelper.getDatabase(), new Compte(83, "karim", "karim","yes", 1));
        /*-------------------------- COMPTE SYNCHRONIZATION --------------------------*/
        String url_compte = MainActivity.IP+"/compte";
        RequestQueue queueCompte = Volley.newRequestQueue(context);
        JsonArrayRequest requestCompte = new JsonArrayRequest(Request.Method.GET, url_compte, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                int id = item.getInt("id");
                                String login = item.getString("login");
                                String password = item.getString("password");
                                String activate = item.getString("activate");
                                JSONObject profileJsonObject=item.getJSONObject("profile");
                                int profile_id = profileJsonObject.getInt("id");
                                Compte compte = sqLiteHelper.getCompteByIdFromDb(id);
                                if(compte.getId() == 0) {
                                    sqLiteHelper.insertCompte(sqLiteHelper.getDatabase(), new Compte(id, login, password, activate, profile_id));
                                    Compte c = sqLiteHelper.getCompteByIdFromDb(id);
                                    //Toast.makeText(SynchronisationActivity.this, "llllllllllllll =======  "+ compte.getId() + " iii="+compte.getLogin(), Toast.LENGTH_SHORT).show();
                                }
                                list_comptes_server.add(new Compte(id, login, password, activate, profile_id));
                                //sqLiteHelper.insertCompte(sqLiteHelper.getDatabase(), new Compte(id, login, password, activate, profile_id));
                            }


                            List<Compte> list_comptes_sqlite= sqLiteHelper.getComptesFromDb();
                            for(Compte p: list_comptes_sqlite) {
                                for(Compte p2: list_comptes_server) {
                                    if (p.getId() == p2.getId()) {
                                        String url_compte_synch = MainActivity.IP + "/compte/synchronize/" + p.getId() + "/" + p.getLogin() + "/" + p.getPassword() + "/" + p.getActivate();
                                        RequestQueue queueCompte_synch = Volley.newRequestQueue(context);
                                        JSONObject jsonObject = new JSONObject();
                                        JsonObjectRequest requestCompte_synch = new JsonObjectRequest(Request.Method.GET, url_compte_synch, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {}
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(R.string.server_restful_error)
                                                        .show();
                                            }
                                        });
                                        queueCompte_synch.add(requestCompte_synch);
                                    }
                                }
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
        queueCompte.add(requestCompte);




        //SQLiteHelper.updateUser(sqLiteHelper.getDatabase(), new User(81, "karim", "karim", "yes"));

        /*-------------------------- USER SYNCHRONIZATION --------------------------*/
        String url_user = MainActivity.IP+"/user";
        RequestQueue queueUser = Volley.newRequestQueue(context);
        JsonArrayRequest requestUser = new JsonArrayRequest(Request.Method.GET, url_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                int id = item.getInt("id");
                                String email = item.getString("email");
                                String firstname = item.getString("firstname");
                                String lastname = item.getString("lastname");
                                JSONObject compteJsonObject=item.getJSONObject("compte");
                                int compte_id = compteJsonObject.getInt("id");
                                Integer parent_id = null;
                                if(item.getJSONObject("parent") == null) {
                                    parent_id = null;
                                } else {
                                    JSONObject parentJsonObject = item.getJSONObject("parent");
                                    parent_id = parentJsonObject.getInt("id");
                                }
                                String parentRelation = item.getString("parentRelation");
                                String level = item.getString("level");
                                String tel = item.getString("tel");
                                String ville = item.getString("ville");
                                String sex = item.getString("sex");
                                User user = new User(id, email, firstname, lastname, parentRelation, level, tel, ville, sex, compte_id, parent_id);
                                list_users_server.add(user);

                                User us = sqLiteHelper.getUserByIdFromDb(id);
                                if(us.getId() == 0) {
                                    sqLiteHelper.insertUser(sqLiteHelper.getDatabase(), user);
                                    User u = sqLiteHelper.getUserByIdFromDb(id);
                                    //Toast.makeText(SynchronisationActivity.this, "llllllllllllll =======  "+ u.getId() + " iii="+u.getFirstname(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            List<User> list_users_sqlite= sqLiteHelper.getUsersFromDb();
                            for(User p: list_users_sqlite) {
                                for(User p2: list_users_server) {
                                    if (p.getId() == p2.getId()) {
                                        String url_user_synch = MainActivity.IP + "/user/synchronize/" + p.getId() + "/" + p.getEmail() + "/" + p.getFirstname() + "/" + p.getLastname()+ "/" + p.getParentRelation() + "/" + p.getLevel() + "/" + p.getTel() + "/" + p.getVille() + "/" + p.getSex();
                                        RequestQueue queueUser_synch = Volley.newRequestQueue(context);
                                        JSONObject jsonObject = new JSONObject();
                                        JsonObjectRequest requestUser_synch = new JsonObjectRequest(Request.Method.GET, url_user_synch, jsonObject, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {}
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                new AlertDialog.Builder(context)
                                                        .setTitle("Error")
                                                        .setMessage(R.string.server_restful_error)
                                                        .show();
                                            }
                                        });
                                        queueUser_synch.add(requestUser_synch);

                                    }
                                }
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
        queueUser.add(requestUser);



        /*-------------------------- RECOMMENDATION SYNCHRONIZATION --------------------------*/
        String url_recom_msg =MainActivity.IP+"/recommendation/msg";
        RequestQueue queueRecommendation = Volley.newRequestQueue(context);
        JsonArrayRequest requestRecommendation = new JsonArrayRequest(Request.Method.GET, url_recom_msg, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                int id = item.getInt("id");
                                String contenu = item.getString("contenu");
                                String date = item.getString("date");
                                Recommendation recommendation = new Recommendation(id, contenu, date);

                                Recommendation rec = sqLiteHelper.getRecommendationByIdFromDb(id);
                                if(rec.getId() == 0) {
                                    sqLiteHelper.insertRecommendation(sqLiteHelper.getDatabase(), recommendation);
                                    Recommendation r = sqLiteHelper.getRecommendationByIdFromDb(id);
                                    //Toast.makeText(SynchronisationActivity.this, "rrrrrrr =======  "+ r.getId() + " iii="+r.getContenu()+ " fff ="+r.getDate(), Toast.LENGTH_SHORT).show();
                                }
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
        queueRecommendation.add(requestRecommendation);




     }
/*
    @Override
    public void onResponse(JSONArray response) {
        try{
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = response.getJSONObject(i);
                // String id = item.getString("_id");
                String libelle = item.getString("libelle");
                String path = item.getString("image");
                String iden = item.getString("id");
                String description = item.getString("description");
                String level = item.getString("level");
                String progress = item.getString("progress");
                list_courses.add(new Course(libelle, description, path, level, progress));
            }

            for(Course course: list_courses) {
                sqLiteHelper.insertCourse(sqLiteHelper.getDatabase(), course);
                //sqLiteHelper.insertContact(sqLiteHelper.getDatabase(), "dddd", "ggg", "hhh", "oooo", "ppp");
                //Toast.makeText(SynchronisationActivity.this, "OOOOOOOO  "+ libelle, Toast.LENGTH_LONG).show();
            }


        } catch (JSONException error) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error.toString())
                    .show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //toDoListAdapter.setOffres(new ArrayList<Offre>());
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error.getMessage())
                .show();
    }
 */

}
