package com.example.onlytube;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;


public class SuggestionCursorAdapter extends CursorAdapter {

    private SearchView searchView;

    public SuggestionCursorAdapter(Context context, Cursor c, boolean autoRequery, SearchView searchView) {
        super(context, c, autoRequery);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.search_suggestion_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView suggest = view.findViewById(R.id.suggest);
        ImageView putInSearchBox = view.findViewById(R.id.put_in_search_box);
        String body = cursor.getString(cursor.getColumnIndexOrThrow("suggestion"));
        suggest.setText(body);
        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(suggest.getText(), true);
                searchView.clearFocus();
            }
        });
        putInSearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(suggest.getText(), false);
            }
        });
    }
}
