package com.example.notesapp.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_NAME = "note";
    public static final String AUTHORITY = "com.example.notesapp";
    private static final String SCHEME = "content";

    public DatabaseContract() {
    }

    public static final class NoteColumns implements BaseColumns{
        public static String TITLE = "title";
        public static String DESCRIPTION = "description";
        public static String DATE = "date";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();
    }
}
