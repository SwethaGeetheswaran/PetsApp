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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.petsContract;
import com.example.android.pets.data.petsContract.petsEntry;
import com.example.android.pets.data.petsDBHelper;

import static com.example.android.pets.data.petsContract.petsEntry.GENDER_UNKNOWN;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentUri;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    private petsDBHelper mDbHelper;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Log.v("xxxx", "in editor");

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
        mDbHelper = new petsDBHelper(getApplicationContext());

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if (mCurrentUri != null) {
            Log.v("xxx", "edit");
            getSupportActionBar().setTitle("Edit a pet");
            getLoaderManager().initLoader(0, null, this);
        } else {
            getSupportActionBar().setTitle("Add a pet");
            invalidateOptionsMenu();
            Log.v("xxxx", "add");
        }


    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = petsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = petsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = petsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private void savePets() {

        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String nameValue = mNameEditText.getText().toString().trim();
        Log.v("In editor", "name:" + nameValue);
        String breedValue = mBreedEditText.getText().toString().trim();
        Log.v("In editor", "breed:" + breedValue);
        //int weightValue = (Integer.parseInt(mWeightEditText.getText().toString().trim()));
        String weightValue = mWeightEditText.getText().toString().trim();
        Log.v("In editor", "wt:" + weightValue);
        //int weight = Integer.parseInt(weightValue);
        int genderValue = mGender;
        Log.v("In editor", "wt:" + genderValue);

        if (TextUtils.isEmpty(nameValue) && TextUtils.isEmpty(breedValue) &&
                genderValue == GENDER_UNKNOWN && TextUtils.isEmpty(weightValue)) {
            Log.v("Say", "HI");
            return;
        }


        ContentValues values = new ContentValues();
        Log.v("say", "Inside content");
        values.put(petsEntry.COLUMN_NAME, nameValue);
        if (breedValue == null || breedValue.equals("")) {
            values.put(petsEntry.COLUMN_BREED, "Unknown breed");
        } else {
            values.put(petsEntry.COLUMN_BREED, breedValue);
        }
        values.put(petsEntry.COLUMN_GENDER, genderValue);
        int weght = 0;
        if (!TextUtils.isEmpty(weightValue)) {
            weght = Integer.parseInt(weightValue);
        }
        values.put(petsEntry.COLUMN_WEIGHT, weightValue);
        Log.v("say", "outside content");


        Log.v("say", "URI");
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        if (mCurrentUri != null) {

            Log.v("XXXX in editor", "name " + nameValue);
            Log.v("XXXX in editor", "weight " + weightValue);
            int updateId = getContentResolver().update(mCurrentUri, values, null, null);
            if (updateId != 0) {
                Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri newRowId = getContentResolver().insert(petsEntry.CONTENT_URI, values);
            Log.v("say value of", "newRowID:" + newRowId);

            Log.v("EditorActivity", "row ID:" + newRowId);
            if (newRowId != null) {
                Toast.makeText(this, "Value inserted.!" + newRowId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePets();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v("XXXXX", "uri:" + mCurrentUri);
        Log.v("XXXXX", "name in oncreate:" + petsEntry.COLUMN_NAME);
        String[] projection = new String[]{petsEntry.COLUMN_NAME,
                petsEntry.COLUMN_BREED,
                petsEntry.COLUMN_GENDER,
                petsEntry.COLUMN_WEIGHT};
        return new CursorLoader(this,
                mCurrentUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        Log.v("XXXXX", "name in onloadFinish loader id = " + loader.getId());
        Log.v("XXXXX", "name in onloadFinish loader to string = " + loader.toString());
        Log.v("XXXXX", "name in onloadFinish loader data to string = " + loader.dataToString(cursor));
        Log.v("XXXXX", "name in onloadFinish cursor = " + cursor.toString());

        if (cursor.moveToNext()) {
            int nameColumn = cursor.getColumnIndex(petsEntry.COLUMN_NAME);
            Log.v("XXXXX", "name in onloadFinish" + nameColumn);
            int breedColumn = cursor.getColumnIndex(petsEntry.COLUMN_BREED);
            int genderColumn = cursor.getColumnIndex(petsEntry.COLUMN_GENDER);
            int weight = cursor.getColumnIndex(petsEntry.COLUMN_WEIGHT);


            String currName = cursor.getString(nameColumn);
            Log.v("XXXX", "name:" + currName);
            String currBreed = cursor.getString(breedColumn);
            int currGender = cursor.getInt(genderColumn);
            int currWt = cursor.getInt(weight);

            mNameEditText.setText(currName);
            mBreedEditText.setText(currBreed);
            mWeightEditText.setText(Integer.toString(currWt));

            switch (currGender) {
                case petsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case petsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(" ");
        Log.v("xxxxx", "set blank");
        mBreedEditText.setText(" ");
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText(" ");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener mTrackClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, mTrackClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        int rowsDeleted = 0;
        Log.v("in delete","uri" +mCurrentUri);
        if (mCurrentUri != null) {
            rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted != 0) {
                Toast.makeText(EditorActivity.this, "Pet deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditorActivity.this, "Unsuccessfull delete", Toast.LENGTH_SHORT).show();
            }

        }
        finish();
    }

}