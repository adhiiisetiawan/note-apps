package com.example.notesapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.example.notesapp.db.NoteHelper;

import static com.example.notesapp.db.DatabaseContract.AUTHORITY;
import static com.example.notesapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.example.notesapp.db.DatabaseContract.TABLE_NAME;

public class NoteProvider extends ContentProvider {
    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;
    private NoteHelper noteHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE);

        sUriMatcher.addURI(AUTHORITY,
                TABLE_NAME + "/#",
                NOTE_ID);
    }

    public NoteProvider() {
    }

    @Override
    public boolean onCreate() {
        noteHelper = NoteHelper.getInstance(getContext());
        noteHelper.open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case NOTE:
                cursor = noteHelper.queryAll();
                break;
            case NOTE_ID:
                cursor = noteHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
       return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long added;
        switch (sUriMatcher.match(uri)){
            case NOTE:
                added = noteHelper.insert(values);
                break;
            default:
                added = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI +"/#" + added);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int update;
        switch (sUriMatcher.match(uri)){
            case NOTE_ID:
                update = noteHelper.update(uri.getLastPathSegment(), values);
                break;
            default:
                update = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return update;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delete;
        switch (sUriMatcher.match(uri)){
            case NOTE_ID:
                delete = noteHelper.deleteById(uri.getLastPathSegment());
                break;
            default:
                delete = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return delete;
    }
}
