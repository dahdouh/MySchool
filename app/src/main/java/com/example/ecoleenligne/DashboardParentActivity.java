package com.example.ecoleenligne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.Subscription;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardParentActivity extends AppCompatActivity implements Response.Listener<JSONArray>, Response.ErrorListener {

    final Context context = this;
    SharedPreferences sharedpreferences;
    TextView user_name, user_profile;
    ListView listview;
    //avatar
    ImageView image;

    // offline mode
    SQLiteHelper sqLiteHelper;

    private static ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_parent);
        sqLiteHelper= new SQLiteHelper(this);

        user_name = findViewById(R.id.user_name);
        user_profile = findViewById(R.id.user_profile);

        /*------------------  make transparent Status Bar  -----------------*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle(R.string.navigation_menu_dashboard);
        //toolbar.setBackgroundDrawable(new ColorDrawable(R.color.primary_light));
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#283e4a")));
        //BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.bg_red));
        //toolbar.setBackgroundDrawable(background);

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String parent_connected_id = sharedpreferences.getString(MainActivity.Id, null);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("parent_id", ""+ parent_connected_id);
        editor.commit();





        Intent intent = getIntent();
        String ToSubscription = intent.getStringExtra("ToSubscription");
        //String ToProfile = intent.getStringExtra("ToProfile");
        String ToPayment = intent.getStringExtra("ToPayment");
        String ToLog = intent.getStringExtra("ToLog");

        if (ToSubscription != null && ToSubscription.contentEquals("1")) {
            toolbar.setTitle(R.string.navigation_menu_subscription);
            loadFragment(new FragmentParentSubscription());
            navigation.setSelectedItemId(R.id.subscription);

        }
        else if (ToPayment != null && ToPayment.contentEquals("1")) {
            toolbar.setTitle(R.string.navigation_menu_payment);
            loadFragment(new FragmentParentPayment());
            navigation.setSelectedItemId(R.id.payment);

        } else if (ToLog != null && ToLog.contentEquals("1")) {
            toolbar.setTitle(R.string.navigation_menu_log);
            loadFragment(new FragmentLog());
            navigation.setSelectedItemId(R.id.historic);

        }
        else {
            loadFragment(new FragmentParentDashboard());
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.dashboard:
                    toolbar.setTitle(R.string.navigation_menu_dashboard);
                    loadFragment(new FragmentParentDashboard());
                    return true;
                case R.id.profile:
                    toolbar.setTitle(R.string.navigation_menu_profile);
                    loadFragment(new FragmentProfile());
                    return true;
                case R.id.subscription:
                    toolbar.setTitle(R.string.subscribe_title);
                    loadFragment(new FragmentParentSubscription());
                    return true;
                case R.id.payment:
                    toolbar.setTitle(R.string.navigation_menu_payment);
                    loadFragment(new FragmentParentPayment());
                    return true;
                case R.id.historic:
                    toolbar.setTitle(R.string.navigation_menu_log);
                    loadFragment(new FragmentLog());
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onResponse(JSONArray response) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }




    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected = sharedpreferences.getString(MainActivity.Id, null);
        /*
        if(MainActivity.MODE.equals("ONLINE")) {
            String url = MainActivity.IP + "/students/" + user_connected;
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonArrayRequest microPostsRequest = new JsonArrayRequest(url, this, this);
            queue.add(microPostsRequest);
        }
        */
    }

}
