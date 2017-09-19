package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.CatalogActivity;
import com.example.android.pets.EditorActivity;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.content.ContentUris.parseId;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.android.pets.data.petsContract.petsEntry.CONTENT_URI;
import static com.example.android.pets.data.petsContract.petsEntry.GENDER_UNKNOWN;
import static com.example.android.pets.data.petsContract.petsEntry.TABLE_NAME;

public class petProvider extends ContentProvider {

    private petsDBHelper dbHelper;

    public static final int PETS = 100;
    public static final int PET_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(petsContract.CONTENT_AUTHORITY,petsContract.PATH_PETS,PETS);
        sUriMatcher.addURI(petsContract.CONTENT_AUTHORITY,petsContract.PATH_PETS_ID,PET_ID);
    }
    @Override
    public boolean onCreate() {

        dbHelper = new petsDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        //match = PETS;
        Log.v("xxxxxxxxx", "match  = " + match);
        Log.v("xxxxxxxxx", "selection = " + selection);
        Log.v("xxxxxxxxx", "selectionArgs = " + selectionArgs);
        Log.v("xxxxxxxxx", "sortOrder = " + sortOrder);
        switch (match) {
            case PETS:
                cursor = database.query(
                        petsContract.petsEntry.TABLE_NAME,
                        projection, null, null, null,
                        null, sortOrder);
                break;
            case PET_ID:
                 selection = petsContract.petsEntry.COLUMN_ID + "=?";
                 Log.v("xxxxxxxxx", "uri = " + uri.toString());
                 long id = ContentUris.parseId(uri);
                Log.v("xxxxxxxxx", "id = " + id);
                 selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                 sortOrder = petsContract.petsEntry.COLUMN_ID + " DESC";
                cursor = database.query(petsContract.petsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),petsContract.petsEntry.CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
       final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return petsContract.petsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return petsContract.petsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //final int match = sUriMatcher.match(uri);
        int match = PETS;
        switch (match) {
            case PETS:
                Log.v("Insert","first");
                return insertPet(uri, contentValues);
            default:
                Log.v("Insert","error");
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertPet(Uri uri, ContentValues values) {


        Log.v("In provider", "insertpet");
        Log.v("In provider", "Colname" + petsContract.petsEntry.COLUMN_NAME);
        String name = values.getAsString(petsContract.petsEntry.COLUMN_NAME);
        Log.v("insertpet", "namechk" + name);

        if (name == null || name.equals("")) {
            //Toast.makeText(getContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Name cannot be null");
        }

        String breed = values.getAsString(petsContract.petsEntry.COLUMN_BREED);
        if (breed == null || breed.equals("")) {
            //Toast.makeText(getContext(), "Breed cannot be blank", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Breed cannot be null");
        }

        int weight = values.getAsInteger(petsContract.petsEntry.COLUMN_WEIGHT);
        if (weight == 0 || weight == ' ' || weight <= 0 || weight >= 100) {
            //Toast.makeText(getContext(), "Weight cannot be blank", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("weight cannot be blank");

        }
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long newRowId = db.insert(petsContract.petsEntry.TABLE_NAME, null, values);

            if (newRowId == -1) {
                Log.e("xxxx", "Insert failed" + uri);
                return null;
            }

            getContext().getContentResolver().notifyChange(petsContract.petsEntry.CONTENT_URI, null);
            return ContentUris.withAppendedId(uri, id);
        }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        Log.v("in provider delete","uri :" +uri);
        Log.v("in provider delete","selection :" +selection);
        Log.v("in provider delete","selectionargs :" +selectionArgs);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.v("in provider delete", "match :" + match);
        switch (match){
            case PETS:
                getContext().getContentResolver().notifyChange(petsContract.petsEntry.CONTENT_URI, null);
                return db.delete(petsContract.petsEntry.TABLE_NAME,selection,selectionArgs);

            case PET_ID:
                Log.v("XXXXX","deleted in id");
                selection = petsContract.petsEntry._ID + "=?";
                Log.v("XXXXX","selection in id" +selection);
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                Log.v("XXXXX","selection in id" +selectionArgs);
                getContext().getContentResolver().notifyChange(petsContract.petsEntry.CONTENT_URI, null);
                return db.delete(petsContract.petsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " +uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS :
                return updatePet(uri,contentValues,selection,selectionArgs);
            case PET_ID:
                selection = petsContract.petsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(petsContract.petsEntry.COLUMN_NAME))
        {
        String name = values.getAsString(petsContract.petsEntry.COLUMN_NAME);
        if(name == null || name.equals(""))
        {
            Toast.makeText(getContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();

        }
        }

        if(values.containsKey(petsContract.petsEntry.COLUMN_BREED)){
            String breed = values.getAsString(petsContract.petsEntry.COLUMN_BREED);
            if(breed == null || breed.equals("")){
                Toast.makeText(getContext(),"Breed cannot be blank",Toast.LENGTH_SHORT).show();

            }
        }

        if(values.containsKey(petsContract.petsEntry.COLUMN_WEIGHT)){
            int weight = values.getAsInteger(petsContract.petsEntry.COLUMN_WEIGHT);
            if(weight == 0 || weight == ' ' || weight <=0 || weight >=100){
                Toast.makeText(getContext(),"Enter a valid weight",Toast.LENGTH_SHORT).show();

            }
        }
        if(values.containsKey(petsContract.petsEntry.COLUMN_GENDER)){
            Integer gender = values.getAsInteger(petsContract.petsEntry.COLUMN_GENDER);
            if(gender == null){
                Toast.makeText(getContext(),"Enter gender",Toast.LENGTH_SHORT).show();

            }
        }
        if(values.size() == 0){
            return 0;
        }

        Log.v("in update","name" +petsContract.petsEntry.COLUMN_NAME);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowID = db.update(petsContract.petsEntry.TABLE_NAME,values,selection,selectionArgs);
        Log.v("in update","name after :" +petsContract.petsEntry.COLUMN_NAME);
        getContext().getContentResolver().notifyChange(petsContract.petsEntry.CONTENT_URI, null);
        return rowID;
    }

}
