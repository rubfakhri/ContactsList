package com.example.contactslist;

public class ContactsDB {
    //db name
    public static final String DB_NAME = "TalkToMe_DB" ;
    //db version
    public static final int DB_VERSION = 1 ;
    // table name
    public static final String TABLE_NAME = "CONTACT_LIST" ;
    // fields of the table
    public static final String C_ID = "ID" ;
    public static final String C_NAME = "NAME" ;
    public static final String C_IMAGE = "IMAGE" ;
    public static final String C_PHONE = "PHONE" ;
    public static final String C_EMAIL = "EMAIL" ;
    // whatsApp, caregiver/child

    // query
    public static final String CREATE_TABLE = "CREATE TABLE" + TABLE_NAME + "("
            + C_ID +"INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + C_NAME +"TEXT ,"
            + C_IMAGE +"TEXT ,"
            + C_PHONE +"TEXT ,"
            + C_EMAIL +"TEXT "
            // whatsApp, caregiver/child
            + ")";

}
