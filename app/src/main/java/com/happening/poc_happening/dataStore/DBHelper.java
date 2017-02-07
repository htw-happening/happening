package com.happening.poc_happening.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.happening.poc_happening.models.ChatEntryModel;

import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "happening.db";

    // Query Strings - CREATE TABLES
    private static final String SQL_CREATE_PROFILE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.PROFILE_TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.PROFILE_COLUMN_USERNAME + " TEXT NOT NULL," +
                    DBContract.DBEntry.PROFILE_COLUMN_FIRSTNAME + " TEXT NOT NULL," +
                    DBContract.DBEntry.PROFILE_COLUMN_LASTNAME + " TEXT NOT NULL)";

    private static final String SQL_CREATE_DEVICE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.DEVICES_TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.DEVICES_COLUMN_NAME + " TEXT NOT NULL," +
                    DBContract.DBEntry.DEVICES_COLUMN_ADDRESS + " TEXT NOT NULL," +
                    DBContract.DBEntry.DEVICES_COLUMN_LAST_SEEN + " TEXT NOT NULL)";

    private static final String SQL_CREATE_PRIVATE_MESSAGES_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.PRIVATE_MESSAGES_TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.PRIVATE_MESSAGES_COLUMN_FROM_DEVICE_ID + " TEXT NOT NULL," +
                    DBContract.DBEntry.PRIVATE_MESSAGES_COLUMN_CREATION_TIME + " TEXT NOT NULL," +
                    DBContract.DBEntry.PRIVATE_MESSAGES_COLUMN_TYPE + " TEXT NOT NULL," +
                    DBContract.DBEntry.PRIVATE_MESSAGES_COLUMN_CONTENT + " TEXT NOT NULL)";

    private static final String SQL_CREATE_GLOBAL_MESSAGES_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.GLOBAL_MESSAGES_TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_FROM_DEVICE_ID + " TEXT NOT NULL," +
                    DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CREATION_TIME + " TEXT NOT NULL," +
                    DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_TYPE + " TEXT NOT NULL," +
                    DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CONTENT + " TEXT NOT NULL)";



    // Query Strings - DROP TABLES

    private static final String SQL_DELETE_DEVICE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.DEVICES_TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROFILE_ENTRIES);
        db.execSQL(SQL_CREATE_DEVICE_ENTRIES);
        db.execSQL(SQL_CREATE_PRIVATE_MESSAGES_ENTRIES);
        db.execSQL(SQL_CREATE_GLOBAL_MESSAGES_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // db.execSQL(SQL_CREATE_PROFILE_ENTRIES);
        // db.execSQL(SQL_DELETE_ENTRIES);
        // db.execSQL(SQL_CREATE_PRIVATE_MESSAGES_ENTRIES);
        // db.execSQL(SQL_CREATE_GLOBAL_MESSAGES_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Get Data

    public Cursor getDevice(int id) {
        SQLiteDatabase db = this.getReadableDatabase("password");

        Cursor res = db.rawQuery("select * from " + DBContract.DBEntry.DEVICES_TABLE_NAME + " where " + DBContract.DBEntry._ID + " = " + id + "", null);
        res.close();
        db.close();
        return res;
    }

    public ArrayList<String> getAllDeviceNames() {
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase("password");
        Cursor res = db.rawQuery("select * from " + DBContract.DBEntry.DEVICES_TABLE_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            list.add(res.getString(res.getColumnIndex(DBContract.DBEntry.DEVICES_COLUMN_NAME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return list;
    }

    public ArrayList<ChatEntryModel> getAllGlobalMessagesRaw() {
        ArrayList<ChatEntryModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase("password");
        Cursor res = db.rawQuery("select * from " + DBContract.DBEntry.GLOBAL_MESSAGES_TABLE_NAME, null);

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            ChatEntryModel chatEntryModel = new ChatEntryModel(
                    res.getString(res.getColumnIndex(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_FROM_DEVICE_ID)),
                    res.getString(res.getColumnIndex(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CREATION_TIME)),
                    res.getString(res.getColumnIndex(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_TYPE)),
                    res.getString(res.getColumnIndex(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CONTENT))
            );
            list.add(chatEntryModel);
            res.moveToNext();
        }
        res.close();
        db.close();
        return list;
    }

    // Insert Methods

    public boolean insertDevice(String name, String address, String lastSeen) {
        SQLiteDatabase db = this.getWritableDatabase("password");

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_NAME, name);
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_ADDRESS, address);
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_LAST_SEEN, lastSeen);

        db.insert(DBContract.DBEntry.DEVICES_TABLE_NAME, null, contentValues);
        db.close();

        return true;
    }

    public boolean insertGlobalMessage(String name, String time, String type, String content) {
        SQLiteDatabase db = this.getWritableDatabase("password");
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_FROM_DEVICE_ID, name);
        contentValues.put(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CREATION_TIME, time);
        contentValues.put(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_TYPE, type);
        contentValues.put(DBContract.DBEntry.GLOBAL_MESSAGES_COLUMN_CONTENT, content);

        db.insert(DBContract.DBEntry.GLOBAL_MESSAGES_TABLE_NAME, null, contentValues);
        db.close();

        return true;
    }

    // Update Methods

    public boolean updateDevice(Integer id, String name, String address, String lastSeen) {
        SQLiteDatabase db = this.getWritableDatabase("password");

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_NAME, name);
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_ADDRESS, address);
        contentValues.put(DBContract.DBEntry.DEVICES_COLUMN_LAST_SEEN, lastSeen);

        db.update(DBContract.DBEntry.DEVICES_TABLE_NAME, contentValues, DBContract.DBEntry._ID + " = ? ", new String[]{Integer.toString(id)});
        db.close();

        return true;
    }

    // Delete Methods

    public Integer deleteDevice(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase("password");
        int res = db.delete(DBContract.DBEntry.DEVICES_TABLE_NAME,
                DBContract.DBEntry._ID + " = ? ",
                new String[]{Integer.toString(id)});
        db.close();

        return res;
    }


    /**
     * AndroidDatabaseManager Helper Methodes
     * https://github.com/sanathp/DatabaseManager_For_Android
     */


    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase("password");
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();
                sqlDB.close();
                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            sqlDB.close();
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            sqlDB.close();

            return alc;
        }


    }

    public void clearTables() {
        SQLiteDatabase db = this.getWritableDatabase("password");
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.DBEntry.GLOBAL_MESSAGES_TABLE_NAME);
        db.execSQL(SQL_CREATE_GLOBAL_MESSAGES_ENTRIES);
        db.close();

    }
}