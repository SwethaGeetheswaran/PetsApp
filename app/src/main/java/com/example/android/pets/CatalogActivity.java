/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android.pets.data.petsContract.petsEntry;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



    PetCursorAdapter madapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Log.v("xxxx","in catalog");

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Log.v("XXXXXXX","start navigation");
                startActivity(intent);
            }
        });


        ListView listView = (ListView) findViewById(R.id.text_view_pet);

        View emptyview = (View) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyview);

        madapter = new PetCursorAdapter(this,null);
        listView.setAdapter(madapter);

        getLoaderManager().initLoader(0,null,this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(CatalogActivity.this,EditorActivity.class);
                Uri newUri = ContentUris.withAppendedId(petsEntry.CONTENT_URI,id);
                Log.v("XXXX","uri: " +petsEntry.CONTENT_URI);
                Log.v("XXXX","id: " +id);
                i.setData(newUri);
                startActivity(i);
            }
            });

    }

    public void insertRows() {

        ContentValues values = new ContentValues();
        values.put(petsEntry.COLUMN_NAME, "Toto");
        values.put(petsEntry.COLUMN_BREED, "Terrier");
        values.put(petsEntry.COLUMN_GENDER, petsEntry.GENDER_MALE);
        values.put(petsEntry.COLUMN_WEIGHT, 7);

       Uri newUri= getContentResolver().insert(petsEntry.CONTENT_URI,values);
        Log.v("CatalogActivity","uri:" +newUri);
    }
    public void deleteAllRows() {

        int deleteUri = getContentResolver().delete(petsEntry.CONTENT_URI,null,null);
        if(deleteUri != 0){
            Toast.makeText(getBaseContext(),"delete successfull",Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getBaseContext(),"unsuccessfull delete",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertRows();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllRows();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[] {petsEntry.COLUMN_ID,
                petsEntry.COLUMN_NAME,
                petsEntry.COLUMN_BREED};
        return new CursorLoader(this, petsEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        madapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        madapter.swapCursor(null);
    }
}
