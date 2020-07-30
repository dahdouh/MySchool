package com.example.ecoleenligne;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.User;
import com.example.ecoleenligne.util.SQLiteHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileUpdateActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferences sharedpreferences;
    TextInputLayout password, firstname, lastname, email;
    String password_data, firstname_data, lastname_data, email_data, date_data;
    TextView msg_error;
    DatePicker picker;

    // image upload
    ImageView image;
    TextView choose;
    int PICK_IMAGE_REQUEST = 111;
    int PICK_CAMERA_REQUEST = 222;
    Bitmap bitmap;
    String imageString = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        //Actionbar config
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.navigation_menu_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.primary)));
        //getSupportActionBar().setBackgroundDrawable( new ColorDrawable( getResources().getColor(R.color.colorRedGo)));
        //Transparent statusbar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        password = findViewById(R.id.login_password);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        picker=(DatePicker)findViewById(R.id.datePicker);
        msg_error = findViewById(R.id.msg_error);

        //image upload
        image = (ImageView)findViewById(R.id.image);
        choose = (TextView)findViewById(R.id.choose);

        //opening image chooser option
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                 */

                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, PICK_CAMERA_REQUEST);
                        }
                        else if (options[item].equals("Choose from Gallery"))
                        {
                            Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                        }
                        else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });

        //button save
        final Button save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!validateFirstname() || !validateLastname() || !validatePassword() || !validateEmail()){
                    return;
                } else {
                    date_data = picker.getYear()+"-"+ (picker.getMonth() + 1)+"-"+picker.getDayOfMonth();
                    updateStudentRest(email_data, password_data, firstname_data, lastname_data, date_data);
                }
            }
        });

        /*--------------get user from session --------------*/
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        final String user_connected_id = sharedpreferences.getString(MainActivity.Id, null);

        //online mode
        if(MainActivity.MODE.equals("ONLINE")) {
            String url = MainActivity.IP + "/api/profile/" + user_connected_id;
            RequestQueue queue = Volley.newRequestQueue(context);

            JSONObject jsonObject = new JSONObject();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                try {
                    String user_exist = response.getString("email");


                    if (user_exist.equals("not found")) {
                        Toast.makeText(context, "=====>  User not exist", Toast.LENGTH_LONG).show();
                    } else {
                        firstname.getEditText().setText(response.getString("firstName"));
                        lastname.getEditText().setText(response.getString("lastName"));
                        email.getEditText().setText(response.getString("email"));
                        //uplaod user avatar
                        String path_img = MainActivity.IP_myspace +"/TER.git/public/upload/picture/"+ response.getString("image");
                        Picasso.get().load(path_img)
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(image);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(e.toString())
                            .show();
                }
            }, error -> new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setMessage(R.string.server_restful_error)
                    .show());

            queue.add(request);

        } else {
        }


    }

    public void uploadImageRest(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.login_avatar_upload_wait));
        progressDialog.show();

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String user_connected = sharedpreferences.getString(MainActivity.Id, null);
        String URL =  MainActivity.IP+"/api/profile/picture/new/"+ user_connected;
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if(s.equals("true")){
                    Toast.makeText(context, getString(R.string.login_avatar_upload_succes), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, getString(R.string.login_avatar_upload_error), Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getString(R.string.login_avatar_upload_error), Toast.LENGTH_LONG).show();;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imageString);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CAMERA_REQUEST) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(bitmap);
                BitMapToString(bitmap);
                //set image extention
                //imageEextension = "jpg";

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                BitMapToString(bitmap);
                image.setImageBitmap(bitmap);
                //get image extention
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                String filePath = null;
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                }
                cursor.close();
                //imageEextension = filePath.substring(filePath.lastIndexOf(".")+1); // Extension with dot .jpg, .png
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //update image in the server
        uploadImageRest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                String user_profile_data = sharedpreferences.getString(MainActivity.Role, null);
                if(user_profile_data.equals("ROLE_TUTOR")) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    intent.putExtra("ToProfile", "1");
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void backStep(View view) {

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra("ToProfile", "1");
        context.startActivity(intent);
    }

    // convert image to string for restful service
    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;
    }

    /*------------------ call restful service ----------------*/
    public void updateStudentRest(String email, String password, String firstName, String lastName, String date) {
        String url = MainActivity.IP+"/api/profile/update/"+email+"/"+password+"/"+firstName+"/"+lastName+"/"+date;
        Toast.makeText(context, "eee "+url, Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        // allow connection with https and ssl
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, jsonObject, response -> {
                  Intent intent=new Intent(context, ProfileActivity.class);
                  context.startActivity(intent);

        }, e -> new AlertDialog.Builder(context)
                .setTitle("#Error")
                .setMessage(e.toString())
                .show());

        queue.add(request);

    }

    /*------------------ validate data --------------*/
    private Boolean validatePassword() {
        password_data = password.getEditText().getText().toString();
        if (password_data.isEmpty()) {
            password.setError(getString(R.string.register_password_validat_empty));
            return false;
        } else {

            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password_data);
            if(!matcher.matches() && password_data.length()<4) {
                password.setError(getString(R.string.register_password_validat_regex));
                return false;
            }else {
                return true;
            }
        }
    }
    private Boolean validateFirstname() {
        firstname_data = firstname.getEditText().getText().toString();
        if (firstname_data.isEmpty()) {
            firstname.setError(getString(R.string.register_firstname_validat_empty));
            return false;
        } else {
            firstname.setError(null);
            firstname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateLastname() {
        lastname_data = lastname.getEditText().getText().toString();
        if (lastname_data.isEmpty()) {
            lastname.setError(getString(R.string.register_lastname_validat_empty));
            return false;
        } else {
            lastname.setError(null);
            lastname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateEmail() {
        email_data = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email_data.isEmpty()) {
            email.setError(getString(R.string.register_email_validat_empty));
            return false;
        } else if (!email_data.matches(emailPattern)) {
            email.setError(getString(R.string.register_email_validat_invalid));
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

}
