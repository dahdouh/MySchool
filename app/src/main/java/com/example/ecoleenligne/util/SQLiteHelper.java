package com.example.ecoleenligne.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.Toast;

import com.example.ecoleenligne.R;
import com.example.ecoleenligne.model.Compte;
import com.example.ecoleenligne.model.Course;
import com.example.ecoleenligne.model.Profile;
import com.example.ecoleenligne.model.Recommendation;
import com.example.ecoleenligne.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class SQLiteHelper extends SQLiteOpenHelper implements DatabaseConstants {
    private Resources resources;
    private SQLiteDatabase database;
    public static final int DB_VERSION = 2;


    //Construteur de de la classe SQKiteHelper
    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        resources = context.getResources();
        openDatabase();
        //onCreate(this.database);

    }

    public void createSqliteDatabase(){
        onCreate(this.database);
    }

    //methode permet d'ouvrir la base de données SQLite
    public void openDatabase() {
        try {
            this.database = getWritableDatabase();
            //this.database = SQLiteDatabase.openOrCreateDatabase(DB_NAME, null);
        } catch (final SQLException se) {
            se.printStackTrace();
        }
    }

    //create sqlite tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*-------------------- USER TABLE ----------------------*/
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COURSE);
        db.execSQL("CREATE TABLE " + TABLE_COURSE + "(" + COL_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +", "+
                COL_COURSE_LIBELLE + " VARCHAR(255) NULL, "+
                COL_COURSE_DESCRIPTION + " VARCHAR(255) NULL, "+
                COL_COURSE_IMAGE + " VARCHAR(255) NULL, "+
                COL_COURSE_LEVEL + " VARCHAR(255) NULL, "+
                COL_COURSE_PROGRESS + " VARCHAR(255) NULL);");

        /*-------------------- PROFILE TABLE ----------------------*/
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PROFILE);
        db.execSQL("CREATE TABLE " + TABLE_PROFILE + "(" + COL_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +", "+
                COL_PROFILE_LIBELLE + " VARCHAR(255) NULL, "+
                COL_PROFILE_ROLE + " VARCHAR(255) NULL);");

        /*-------------------- PROFILE TABLE ----------------------*/
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COMPTE);
        db.execSQL("CREATE TABLE " + TABLE_COMPTE + "(" + COL_COMPTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +", "+
                COL_COMPTE_LOGIN + " VARCHAR(255) NULL, "+
                COL_COMPTE_PASSWORD + " VARCHAR(255) NULL, "+
                COL_COMPTE_ACTIVATE + " VARCHAR(255) NULL, "+
                COL_COMPTE_PROFILE_ID + " INTEGER NULL);");

        /*-------------------- PROFILE TABLE ----------------------*/
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_USER);
        db.execSQL("CREATE TABLE " + TABLE_USER + "(" + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +", "+
                COL_USER_EMAIL + " VARCHAR(255) NULL, "+
                COL_USER_FIRSTNAME + " VARCHAR(255) NULL, "+
                COL_USER_LASTNAME + " VARCHAR(255) NULL, "+
                COL_USER_COMPTE_ID + " INTEGER NULL, "+
                COL_USER_PARENT_ID + " INTEGER NULL, "+
                COL_USER_PARENT_RELATION + " VARCHAR(255) NULL, "+
                COL_USER_LEVEL + " VARCHAR(255) NULL, "+
                COL_USER_TEL + " VARCHAR(255) NULL, "+
                COL_USER_VILLE + " VARCHAR(255) NULL, "+
                COL_USER_SEX + " INTEGER NULL);");

        /*-------------------- RECOMMENDATION TABLE ----------------------*/
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_RECOMMENDATION);
        db.execSQL("CREATE TABLE " + TABLE_RECOMMENDATION + "(" + COL_RECOMMENDATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +", "+
                COL_RECOMMENDATION_CONTENU + " VARCHAR(255) NULL, "+
                COL_RECOMMENDATION_DATE + " VARCHAR(255) NULL);");

    }

    /*--------------------  MODIFY DATA - LOCAL DATABASE (sqlite) ----------------------*/
    public static void insertCourse(SQLiteDatabase db, Course course) {
        //Log.i("eee","############################################# "+course.getId());
        db.execSQL("INSERT INTO " + TABLE_COURSE +" ("+ COL_COURSE_ID +", " + COL_COURSE_LIBELLE + ","+ COL_COURSE_DESCRIPTION +", "+ COL_COURSE_IMAGE +", "+ COL_COURSE_LEVEL +", "+ COL_COURSE_PROGRESS +") " +
                "VALUES('"+ course.getId() +"','" + course.getLibelle().replace("'","''") + "', '"+ course.getDescription().replace("'","''") +"', '"+ course.getImage().replace("'","''") +"', '"+ course.getLevel().replace("'","''") +"','"+ course.getProgress().replace("'","''") +"')");
    }
    public static void updateCourse(SQLiteDatabase db, Course course) {
        db.execSQL("UPDATE " + TABLE_COURSE +"  SET " + COL_COURSE_PROGRESS +"='"+ course.getProgress() +"' WHERE "+ COL_COURSE_ID +"=" + course.getId());
    }

    public static void insertProfile(SQLiteDatabase db, Profile profile) {
        db.execSQL("INSERT INTO " + TABLE_PROFILE +" (" + COL_PROFILE_ID +", "+ COL_PROFILE_LIBELLE +", "+ COL_PROFILE_ROLE +") " +
                "VALUES('"+ profile.getId() +"','" + profile.getLibelle().replace("'","''") +"','"+ profile.getRole().replace("'","''") +"')");
    }
    public static void updateProfile(SQLiteDatabase db, Profile profile) {
        db.execSQL("UPDATE " + TABLE_PROFILE +"  SET " + COL_PROFILE_LIBELLE +"='"+ profile.getLibelle().replace("'","''") +"' WHERE "+ COL_PROFILE_ID +"=" +profile.getId());
    }


    public static void insertCompte(SQLiteDatabase db, Compte compte) {
        Log.i("eee","############################################# "+ compte.getId());
        db.execSQL("INSERT INTO " + TABLE_COMPTE +" (" + COL_COMPTE_ID +", "+ COL_COMPTE_LOGIN +", "+ COL_COMPTE_PASSWORD +","+ COL_COMPTE_PROFILE_ID +","+ COL_COMPTE_ACTIVATE +") " +
                "VALUES('"+ compte.getId() +"','" + compte.getLogin().replace("'","''") +"','"+ compte.getPassword().replace("'","''") +"', '"+ compte.getProfile_id() +"','"+ compte.getActivate() +"')");
    }
    public static void updateCompte(SQLiteDatabase db, Compte compte) {
        db.execSQL("UPDATE " + TABLE_COMPTE +"  SET " + COL_COMPTE_LOGIN +"='"+ compte.getLogin() +"', "+ COL_COMPTE_PASSWORD +"= '"+ compte.getPassword() +"' WHERE "+ COL_COMPTE_ID +"=" + compte.getId());
    }


    public static void insertUser(SQLiteDatabase db, User user) {
        db.execSQL("INSERT INTO " + TABLE_USER +" (" + COL_USER_ID +", "+ COL_USER_EMAIL +", "+ COL_USER_FIRSTNAME +","+ COL_USER_LASTNAME +","+ COL_USER_COMPTE_ID +","+ COL_USER_PARENT_ID +", "+ COL_USER_PARENT_RELATION +", "+ COL_USER_LEVEL +", "+ COL_USER_TEL +", "+ COL_USER_VILLE +", "+ COL_USER_SEX +") " +
                "VALUES('"+ user.getId() +"','" + user.getEmail().replace("'","''") +"','"+ user.getFirstname().replace("'","''") +"', '"+ user.getLastname().replace("'","''") +
                "','"+ user.getCompte_id() +"', '"+ user.getParent_id() +
                "', '"+ user.getParentRelation()+
                "', '"+ user.getLevel()+
                "', '"+ user.getTel()+
                "', '"+ user.getVille()+
                "', '"+ user.getSex()+
                "')");
    }

    public static void insertRecommendation(SQLiteDatabase db, Recommendation recommendation) {
        db.execSQL("INSERT INTO " + TABLE_RECOMMENDATION +" (" + COL_RECOMMENDATION_ID +", "+ COL_RECOMMENDATION_CONTENU +", "+ COL_RECOMMENDATION_DATE +") " +
                "VALUES('"+ recommendation.getId() +"','" + recommendation.getContenu().replace("'","''") +"','"+ recommendation.getDate().replace("'","''") +"')");
    }

    public static void updateUser(SQLiteDatabase db, User user) {
        db.execSQL("UPDATE " + TABLE_USER +"  SET " + COL_USER_FIRSTNAME +"='"+ user.getFirstname().replace("'","''") +"' WHERE "+ COL_USER_ID +"=" + user.getId());
    }

    public static void updateUserParent(SQLiteDatabase db, User user, Compte compte) {
        db.execSQL("UPDATE " + TABLE_USER +"  SET " + COL_USER_FIRSTNAME +"='"+ user.getFirstname().replace("'","''") +"', "+
                COL_USER_LASTNAME +"='"+ user.getLastname() +"', " +
                COL_USER_LASTNAME +"='"+ user.getLastname() +"', " +
                COL_USER_EMAIL +"='"+ user.getEmail() +"', " +
                COL_USER_TEL +"='"+ user.getTel() +"', " +
                COL_USER_VILLE +"='"+ user.getVille() +"' " +
                "WHERE "+ COL_USER_ID +"=" + user.getId());
        compte.setId(user.getCompte_id());
        updateCompte(db, compte);
    }

    public static void updateStudent(SQLiteDatabase db, String student_selected, int compte_id, String login, String password, String firstName, String lastName, String parent, String level){
        db.execSQL("UPDATE " + TABLE_USER +"  SET " + COL_USER_FIRSTNAME +"='"+ firstName.replace("'","''") +"', "+ COL_USER_LASTNAME +"='"+ lastName.replace("'","''") +"', "+ COL_USER_PARENT_RELATION +"='"+ parent +"', "+ COL_USER_LEVEL +"='"+ level +"' WHERE "+ COL_USER_ID +"=" + student_selected);
        db.execSQL("UPDATE " + TABLE_COMPTE +"  SET " + COL_COMPTE_LOGIN +"='"+ login.replace("'","''") +"', "+ COL_COMPTE_PASSWORD +"='"+ password.replace("'","''") +"' WHERE "+ COL_COMPTE_ID +"=" + compte_id);
    }



    // drop tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PROFILE);
        //onCreate(db);

    }

    /*--------------------  RETREIVE DATA FROM SQLITE DATABASE ----------------------*/
    public List<Course> getCoursesFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_COURSE + " ORDER BY "
                + COL_COURSE_LIBELLE;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<Course> coursesList = new ArrayList<Course>();
        int id;
        String libelle, description, image, level, progress;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(COL_COURSE_ID));
            libelle = cursor.getString(cursor.getColumnIndex(COL_COURSE_LIBELLE));
            description = cursor.getString(cursor.getColumnIndex(COL_COURSE_DESCRIPTION));
            image = cursor.getString(cursor.getColumnIndex(COL_COURSE_IMAGE));
            level = cursor.getString(cursor.getColumnIndex(COL_COURSE_LEVEL));
            progress = cursor.getString(cursor.getColumnIndex(COL_COURSE_PROGRESS));
            coursesList.add(new Course(id, libelle, description, image, level, progress));

        }
        cursor.close();
        return coursesList;
    }

    /*--------------------  COURSE FOR RECOMMENDATION ----------------------*/
    public List<Course> getCoursesRecommendationFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_COURSE + " ORDER BY "+ COL_COURSE_LIBELLE+ " LIMIT 5";

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<Course> coursesList = new ArrayList<Course>();
        int id;
        String libelle, description, image, level, progress;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex(COL_COURSE_ID));
            libelle = cursor.getString(cursor.getColumnIndex(COL_COURSE_LIBELLE));
            description = cursor.getString(cursor.getColumnIndex(COL_COURSE_DESCRIPTION));
            image = cursor.getString(cursor.getColumnIndex(COL_COURSE_IMAGE));
            level = cursor.getString(cursor.getColumnIndex(COL_COURSE_LEVEL));
            progress = cursor.getString(cursor.getColumnIndex(COL_COURSE_PROGRESS));
            coursesList.add(new Course(id, libelle, description, image, level, progress));

        }
        cursor.close();
        return coursesList;
    }

    public Course getCourseByIdFromDb(int id_compte) {
        final String sqlQuery = "SELECT * FROM " + TABLE_COURSE + " WHERE id=" + id_compte;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        Course course = new Course();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_COURSE_ID));
            String libelle = cursor.getString(cursor.getColumnIndex(COL_COURSE_LIBELLE));
            String description = cursor.getString(cursor.getColumnIndex(COL_COURSE_DESCRIPTION));
            String level = cursor.getString(cursor.getColumnIndex(COL_COURSE_LEVEL));
            String progress = cursor.getString(cursor.getColumnIndex(COL_COURSE_PROGRESS));
            String image = cursor.getString(cursor.getColumnIndex(COL_COURSE_IMAGE));
            course.setId(id);
            course.setLibelle(libelle);
            course.setDescription(description);
            course.setLevel(level);
            course.setProgress(progress);
            course.setImage(image);
        }
        cursor.close();
        return course;
    }


    public List<Profile> getProfilesFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_PROFILE + " ORDER BY "
                + COL_PROFILE_LIBELLE;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<Profile> profiles = new ArrayList<Profile>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_PROFILE_ID));
            String libelle = cursor.getString(cursor.getColumnIndex(COL_PROFILE_LIBELLE));
            String role = cursor.getString(cursor.getColumnIndex(COL_PROFILE_ROLE));
            profiles.add(new Profile(id,libelle, role));

        }
        cursor.close();
        return profiles;
    }

    public Profile getProfileByIdFromDb(int id_profile) {
        final String sqlQuery = "SELECT * FROM " + TABLE_PROFILE + " WHERE id="+id_profile;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        Profile profile = new Profile();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_PROFILE_ID));
            String libelle = cursor.getString(cursor.getColumnIndex(COL_PROFILE_LIBELLE));
            String role = cursor.getString(cursor.getColumnIndex(COL_PROFILE_ROLE));
            profile.setId(id);
            profile.setLibelle(libelle);
            profile.setRole(role);
        }
        cursor.close();
        return profile;
    }


    public List<Compte> getComptesFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_COMPTE + " ORDER BY "
                + COL_COMPTE_LOGIN;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<Compte> comptes = new ArrayList<Compte>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_ID));
            String login = cursor.getString(cursor.getColumnIndex(COL_COMPTE_LOGIN));
            String password = cursor.getString(cursor.getColumnIndex(COL_COMPTE_PASSWORD));
            String activate = cursor.getString(cursor.getColumnIndex(COL_COMPTE_ACTIVATE));
            int profile_id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_PROFILE_ID));
            comptes.add(new Compte(id,login, password, activate, profile_id));

        }
        cursor.close();
        return comptes;
    }

    public Compte getCompteByIdFromDb(int id_compte) {
        final String sqlQuery = "SELECT * FROM " + TABLE_COMPTE + " WHERE id="+id_compte;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        Compte compte = new Compte();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_ID));
            String login = cursor.getString(cursor.getColumnIndex(COL_COMPTE_LOGIN));
            String password = cursor.getString(cursor.getColumnIndex(COL_COMPTE_PASSWORD));
            String activate = cursor.getString(cursor.getColumnIndex(COL_COMPTE_ACTIVATE));
            int profile_id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_PROFILE_ID));
            compte.setId(id);
            compte.setLogin(login);
            compte.setPassword(password);
            compte.setActivate(activate);
            compte.setProfile_id(profile_id);
        }
        cursor.close();
        return compte;
    }

    public Compte getCompteByLoginPassFromDb(String username, String password) {
        final String sqlQuery = "SELECT * FROM " + TABLE_COMPTE + " WHERE "+ COL_COMPTE_LOGIN +"='"+ username +"' AND "+ COL_COMPTE_PASSWORD +"='"+ password +"'";
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        Compte compte = new Compte();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_ID));
            String login = cursor.getString(cursor.getColumnIndex(COL_COMPTE_LOGIN));
            String pass = cursor.getString(cursor.getColumnIndex(COL_COMPTE_PASSWORD));
            String activate = cursor.getString(cursor.getColumnIndex(COL_COMPTE_ACTIVATE));
            int profile_id = cursor.getInt(cursor.getColumnIndex(COL_COMPTE_PROFILE_ID));
            compte.setId(id);
            compte.setLogin(login);
            compte.setPassword(pass);
            compte.setActivate(activate);
            compte.setProfile_id(profile_id);
        }
        cursor.close();
        return compte;
    }





    public List<User> getUsersFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_USER + " ORDER BY "
                + COL_USER_ID;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<User> users = new ArrayList<User>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
            String firstname = cursor.getString(cursor.getColumnIndex(COL_USER_FIRSTNAME));
            String lastname = cursor.getString(cursor.getColumnIndex(COL_USER_LASTNAME));
            int compte_id = cursor.getInt(cursor.getColumnIndex(COL_USER_COMPTE_ID));
            int parent_id = cursor.getInt(cursor.getColumnIndex(COL_USER_PARENT_ID));
            String parentRelation = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_RELATION));
            String level = cursor.getString(cursor.getColumnIndex(COL_USER_LEVEL));
            String tel = cursor.getString(cursor.getColumnIndex(COL_USER_TEL));
            String ville = cursor.getString(cursor.getColumnIndex(COL_USER_VILLE));
            String sex = cursor.getString(cursor.getColumnIndex(COL_USER_SEX));
            users.add(new User(id, email, firstname, lastname, parentRelation, level, tel, ville, sex, compte_id, parent_id));

        }
        cursor.close();
        return users;
    }

    public User getUserByIdFromDb(int id_user) {
        final String sqlQuery = "SELECT * FROM " + TABLE_USER + " WHERE id="+id_user;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        User user = new User();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
            String firstname = cursor.getString(cursor.getColumnIndex(COL_USER_FIRSTNAME));
            String lastname = cursor.getString(cursor.getColumnIndex(COL_USER_LASTNAME));
            int compte_id = cursor.getInt(cursor.getColumnIndex(COL_USER_COMPTE_ID));
            //String parent_id = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_ID));
            String parentRelation = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_RELATION));
            String level = cursor.getString(cursor.getColumnIndex(COL_USER_LEVEL));
            String tel = cursor.getString(cursor.getColumnIndex(COL_USER_TEL));
            String ville = cursor.getString(cursor.getColumnIndex(COL_USER_VILLE));
            String sex = cursor.getString(cursor.getColumnIndex(COL_USER_SEX));
            user.setId(id);
            user.setEmail(email);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setCompte_id(compte_id);
            user.setParentRelation(parentRelation);
            user.setLevel(level);
            user.setTel(tel);
            user.setVille(ville);
            user.setSex(sex);
        }
        cursor.close();
        return user;
    }

    public User getUserByIdCompte(int id_compte) {
        final String sqlQuery = "select * from user where "+ COL_USER_COMPTE_ID +"="+id_compte;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        User user = new User();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
            Log.i("eee","################## "+ id +" email= "+email);
            String firstname = cursor.getString(cursor.getColumnIndex(COL_USER_FIRSTNAME));
            String lastname = cursor.getString(cursor.getColumnIndex(COL_USER_LASTNAME));
            int compte_id = cursor.getInt(cursor.getColumnIndex(COL_USER_COMPTE_ID));
            //String parent_id = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_ID));
            String parentRelation = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_RELATION));
            String level = cursor.getString(cursor.getColumnIndex(COL_USER_LEVEL));
            String tel = cursor.getString(cursor.getColumnIndex(COL_USER_TEL));
            String ville = cursor.getString(cursor.getColumnIndex(COL_USER_VILLE));
            String sex = cursor.getString(cursor.getColumnIndex(COL_USER_SEX));
            user.setId(id);
            user.setEmail(email);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setCompte_id(compte_id);
            user.setParentRelation(parentRelation);
            user.setLevel(level);
            user.setTel(tel);
            user.setVille(ville);
            user.setSex(sex);
        }
        cursor.close();
        return user;
    }


    public List<User> getUserByParentFromDb(int id_parent) {

        final String sqlQuery = "SELECT * FROM " + TABLE_USER + " WHERE parent_id="+ id_parent +" ORDER BY "+ COL_USER_ID;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<User> users = new ArrayList<User>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
            String firstname = cursor.getString(cursor.getColumnIndex(COL_USER_FIRSTNAME));
            String lastname = cursor.getString(cursor.getColumnIndex(COL_USER_LASTNAME));
            int compte_id = cursor.getInt(cursor.getColumnIndex(COL_USER_COMPTE_ID));
            int parent_id = cursor.getInt(cursor.getColumnIndex(COL_USER_PARENT_ID));
            String parentRelation = cursor.getString(cursor.getColumnIndex(COL_USER_PARENT_RELATION));
            String level = cursor.getString(cursor.getColumnIndex(COL_USER_LEVEL));
            String tel = cursor.getString(cursor.getColumnIndex(COL_USER_TEL));
            String ville = cursor.getString(cursor.getColumnIndex(COL_USER_VILLE));
            String sex = cursor.getString(cursor.getColumnIndex(COL_USER_SEX));
            users.add(new User(id, email, firstname, lastname, parentRelation, level, tel, ville, sex, compte_id, parent_id));

        }
        cursor.close();
        return users;
    }

    public List<Recommendation> getRecommendationsFromDb() {
        final String sqlQuery = "SELECT * FROM " + TABLE_RECOMMENDATION + " ORDER BY "
                + COL_RECOMMENDATION_ID;

        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        List<Recommendation> recommendations = new ArrayList<Recommendation>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_RECOMMENDATION_ID));
            String contenu = cursor.getString(cursor.getColumnIndex(COL_RECOMMENDATION_CONTENU));
            String date = cursor.getString(cursor.getColumnIndex(COL_RECOMMENDATION_DATE));
            recommendations.add(new Recommendation(id, contenu, date));
        }
        cursor.close();
        return recommendations;
    }

    public Recommendation getRecommendationByIdFromDb(int id_recommendation) {
        final String sqlQuery = "SELECT * FROM " + TABLE_RECOMMENDATION + " WHERE id="+id_recommendation;
        final Cursor cursor = this.database.rawQuery(sqlQuery, null);
        Recommendation recommendation = new Recommendation();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COL_RECOMMENDATION_ID));
            String contenu = cursor.getString(cursor.getColumnIndex(COL_RECOMMENDATION_CONTENU));
            String date = cursor.getString(cursor.getColumnIndex(COL_RECOMMENDATION_DATE));
            recommendation.setId(id);
            recommendation.setContenu(contenu);
            recommendation.setDate(date);
        }
        cursor.close();
        return recommendation;
    }


    public SQLiteDatabase getDatabase(){
        return this.database;
    }
}