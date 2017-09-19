package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.petsContract;

public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView1 = (TextView) view.findViewById(R.id.name);
        TextView textView2 = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_NAME);
        int breedColumnIndex = cursor.getColumnIndex(petsContract.petsEntry.COLUMN_BREED);

        String name = cursor.getString(nameColumnIndex);
        String breed = cursor.getString(breedColumnIndex);

        textView1.setText(name);
        textView2.setText(breed);
    }
}

