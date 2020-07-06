package com.example.ecoleenligne.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import com.example.ecoleenligne.model.Profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class SynchronisationService extends Service {

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    List<Profile> contacts_mysql = new ArrayList<Profile>();
    List<Profile> contacts_sqlite = new ArrayList<Profile>();

    FileInputStream fis;
    SQLiteHelper sqLiteHelper;



    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        Context context;

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.

            try {
                //########

                //lire les contacts à partir du fichier sauvegardé par l'activité principale.
                String text = "";

                        //load data from server through restful service (volley)
                        //contacts_mysql = (ArrayList<String>) in.readObject();

                        //lire les contact à partir de fichier
                        //contacts_sqlite = sqLiteHelper.getContactsFromDb();

                        String[] tokens = {};
                        /*
                        * comparer les contact existent dans le fichier avec ceux de la base de données
                        * puis, on met les contacts de la base de données sqLite
                        * d'abord, on parcourt la liste des contacts de fichier
                         */
                        /*
                        for(String contact1: contacts_fichier) {
                            //si le conatct n'existe pas dans la base de données, insérer ce contact dans la table contacts
                                if(!contacts_sqlite.contains(contact1)) {
                                    tokens = contact1.split("_");
                                    sqLiteHelper.insertContact(sqLiteHelper.getDatabase(), tokens[0], tokens[1], tokens[2]);
                                }
                        }
                         */



                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        //instancer et initialiser sqLiteHelper
        sqLiteHelper= new SQLiteHelper(getApplicationContext());


        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "le service est démarré", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        serviceHandler.handleMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service est détruit", Toast.LENGTH_SHORT).show();
    }
}
