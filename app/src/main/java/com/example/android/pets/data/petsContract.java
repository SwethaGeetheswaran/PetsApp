package com.example.android.pets.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class  petsContract {


    public static final String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir";
    public static final String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item";
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final String CONTENT_AUTHORITY_ID = "com.example.android.pets/pets/#";
    public static final Uri BASE_URI_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";
    public static final String PATH_PETS_ID = "pets/#";
    private petsContract() {

    }
    public static final class petsEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI_CONTENT,PATH_PETS);
        public static final String TABLE_NAME = "pets";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";


        /**
         * Possible values for the GENDER of the PETS.
         */
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;

    }
}
