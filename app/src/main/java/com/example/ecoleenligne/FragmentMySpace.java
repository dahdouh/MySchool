package com.example.ecoleenligne;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import com.example.ecoleenligne.util.FilePath;
import com.example.ecoleenligne.util.ForumListAdapter;
import com.example.ecoleenligne.util.VolleyMultipartRequest;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMySpace extends Fragment {

    private Context context;
    private SharedPreferences sharedpreferences;
    private CourseContentListAdapter courseContentListAdapter;
    List<CourseContent> myDocuements = new ArrayList<CourseContent>();

    //upload file
    Button choose;
    private String upload_URL = "";
    private RequestQueue rQueue;


    public FragmentMySpace() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myspace, container, false);

        choose = (Button)view.findViewById(R.id.choose);
        //btn = view.findViewById(R.id.btn);
        //tv = findViewById(R.id.tv);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();

                i.setType("*/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(i, "abc"),1);

            }
        });


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
                                    type = ""+1; //pdf
                                else if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif") || extension.equals("png"))
                                    type = ""+3; //image
                                else if(extension.equals("webm") || extension.equals("mp4") || extension.equals("flv") || extension.equals("avi"))
                                    type = ""+2; //video

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        uploadPDF(displayName,uri);
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPDF(final String pdfname, Uri pdffile){

        InputStream iStream = null;
        try {
            sharedpreferences = getActivity().getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String user_connected = sharedpreferences.getString(MainActivity.Id, null);
            iStream = getActivity().getContentResolver().openInputStream(pdffile);
            final byte[] inputData = getBytes(iStream);
            HttpsTrustManager.allowAllSSL();;
            upload_URL = MainActivity.IP+"/api/myspace/upload/file/new/"+user_connected;

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                    new Response.Listener<NetworkResponse>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("######################ressssssoo",new String(response.data));
                            rQueue.getCache().clear();
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                //jsonObject.getString("message")
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.login_avatar_upload_succes), Toast.LENGTH_SHORT).show();

                                jsonObject.toString().replace("\\\\","");
                            } catch (JSONException e) {
                                Toast.makeText(getActivity().getApplicationContext(),"==> "+ e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("filename", new DataPart(pdfname ,inputData));
                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(getActivity());
            rQueue.add(volleyMultipartRequest);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}
