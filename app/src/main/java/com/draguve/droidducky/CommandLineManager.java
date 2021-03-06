package com.draguve.droidducky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Draguve on 3/30/2018.
 */

public class CommandLineManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "commandLineManager.db";

    // Contacts table name
    private static final String TABLE_SCRIPTS = "scripts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";
    private static final String KEY_LANG = "lang";
    private static final String KEY_OS = "os";

    public CommandLineManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCRIPTS + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_CODE + " TEXT," + KEY_LANG + " TEXT," + KEY_OS + " INTEGER" +  ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCRIPTS);
        // Create tables again
        onCreate(db);
    }

    public void addCommandScript(CommandLineScript script){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,script.getID());
        values.put(KEY_NAME,script.getName());
        values.put(KEY_CODE,script.getCode());
        values.put(KEY_LANG,script.getLang());
        values.put(KEY_OS,script.getOS().ordinal());
        db.insert(TABLE_SCRIPTS,null,values);
        db.close();
    }

    public CommandLineScript getScript(String id){
        CommandLineScript script = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCRIPTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_CODE,KEY_LANG,KEY_OS }, KEY_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            script = new CommandLineScript(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), CommandLineScript.OperatingSystem.fromInteger(cursor.getInt(4)));
        }
        return script;
    }

    public ArrayList<CommandLineScript> getAllScripts(){
        ArrayList<CommandLineScript> scripts = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SCRIPTS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                CommandLineScript script = new CommandLineScript(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), CommandLineScript.OperatingSystem.fromInteger(cursor.getInt(4)));
                scripts.add(script);
            } while(cursor.moveToNext());
        }
        return scripts;
    }

    public void updateScript(CommandLineScript script){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, script.getName());
        values.put(KEY_CODE, script.getCode());
        values.put(KEY_LANG, script.getLang());
        values.put(KEY_OS,script.getOS().ordinal());
        // updating row
        db.update(TABLE_SCRIPTS, values, KEY_ID + " = ?", new String[] { String.valueOf(script.getID()) });
    }

    public void deleteScript(String id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SCRIPTS,KEY_ID + " = ?",new String[]{ id });
        db.close();
    }
}
