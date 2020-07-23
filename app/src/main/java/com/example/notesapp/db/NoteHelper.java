package com.example.notesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.example.notesapp.db.DatabaseContract.TABLE_NAME;

public class NoteHelper {
    private static final String DATABASE_TABLE = TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static NoteHelper INSTANCE;

    private static SQLiteDatabase sqLiteDatabase;

    private NoteHelper(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public static NoteHelper getInstance(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new NoteHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException{
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();

        if (sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }
    }

    public Cursor queryAll(){
        return sqLiteDatabase.query(
                DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " ASC"
        );
    }

    public Cursor queryById(String id){
        return sqLiteDatabase.query(
                DATABASE_TABLE,
                null,
                _ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null
        );
    }

    public long insert(ContentValues contentValues){
        return sqLiteDatabase.insert(DATABASE_TABLE, null, contentValues);
    }

    public int update(String id, ContentValues contentValues){
        return sqLiteDatabase.update(DATABASE_TABLE, contentValues, _ID + " = ?", new String[]{id});
    }

    public int deleteById(String id){
        return sqLiteDatabase.delete(DATABASE_TABLE, _ID + " = ?", new String[]{id});
    }
}
