package com.example.android.bikesinventory;

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

import com.example.android.bikesinventory.data.Contract.BikeEntry;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BIKE_LOADER = 0;

    BikeCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bikeListView = findViewById(R.id.list_view_bike);
        View emptyView = findViewById(R.id.empty_view);
        bikeListView.setEmptyView(emptyView);

        mCursorAdapter = new BikeCursorAdapter(this, null);
        bikeListView.setAdapter(mCursorAdapter);

        bikeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentBikeUri = ContentUris.withAppendedId(BikeEntry.CONTENT_URI, id);

                intent.setData(currentBikeUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BIKE_LOADER, null, this);
    }

    private void insertBike() {
        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_PRODUCT_NAME, "Cannondale");
        values.put(BikeEntry.COLUMN_SUPPLIER_NAME, "AmiBike");
        values.put(BikeEntry.COLUMN_QUANTITY, 1);
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, 100000000);
        values.put(BikeEntry.COLUMN_PRICE, 3400);

        Uri newUri = getContentResolver().insert(BikeEntry.CONTENT_URI, values);
    }

    private void deleteAllBikes() {
        int rowsDeleted = getContentResolver().delete(BikeEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from bike database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_data:
                insertBike();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBikes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                BikeEntry._ID,
                BikeEntry.COLUMN_PRODUCT_NAME,
                BikeEntry.COLUMN_PRICE,
                BikeEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                BikeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}