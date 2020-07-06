package com.example.ecoleenligne.util;


public interface DatabaseConstants {

    public static String DB_NAME = "myschool.db";
    /*-------------------- COURSE TABLE ----------------------*/
    public static final String TABLE_COURSE = "course";
    public static final String COL_COURSE_ID = "id";
    public static final String COL_COURSE_LIBELLE = "libelle";
    public static final String COL_COURSE_DESCRIPTION = "description";
    public static final String COL_COURSE_LEVEL = "level";
    public static final String COL_COURSE_PROGRESS = "progress";
    public static final String COL_COURSE_IMAGE = "image";

    /*-------------------- COURSE TABLE ----------------------*/
    public static final String TABLE_USER = "user";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_FIRSTNAME = "firstname";
    public static final String COL_USER_LASTNAME = "lastname";
    public static final String COL_USER_COMPTE_ID = "compte_id";
    public static final String COL_USER_PARENT_ID = "parent_id";
    public static final String COL_USER_PARENT_RELATION = "parent_relation";
    public static final String COL_USER_LEVEL = "level";
    public static final String COL_USER_TEL = "tel";
    public static final String COL_USER_VILLE = "ville";
    public static final String COL_USER_SEX = "sex";

    /*-------------------- COMPTE TABLE ----------------------*/
    public static final String TABLE_COMPTE = "compte";
    public static final String COL_COMPTE_ID = "id";
    public static final String COL_COMPTE_LOGIN = "login";
    public static final String COL_COMPTE_PASSWORD = "password";
    public static final String COL_COMPTE_ACTIVATE = "activate";
    public static final String COL_COMPTE_PROFILE_ID = "profile_id";

    /*-------------------- PROFILE TABLE ----------------------*/
    public static final String TABLE_PROFILE = "pofile";
    public static final String COL_PROFILE_ID = "id";
    public static final String COL_PROFILE_LIBELLE = "libelle";
    public static final String COL_PROFILE_ROLE = "role";

    /*-------------------- RECOMMENDATION TABLE ----------------------*/
    public static final String TABLE_RECOMMENDATION = "recommendation";
    public static final String COL_RECOMMENDATION_ID = "id";
    public static final String COL_RECOMMENDATION_CONTENU = "contenu";
    public static final String COL_RECOMMENDATION_DATE = "date";

}
