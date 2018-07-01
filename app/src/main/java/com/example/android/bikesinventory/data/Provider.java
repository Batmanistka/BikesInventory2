package com.example.android.bikesinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.bikesinventory.data.Contract.BikeEntry;

public class Provider extends ContentProvider {

    private static final int BIKES = 100;
    private static final int BIKE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_BIKES, BIKES);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_BIKES + "/#", BIKE_ID);
    }

    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                cursor = database.query(BikeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BIKE_ID:
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BikeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return insertBike(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBike(Uri uri, ContentValues values) {
        // Check that the name is not null
        String productName = values.getAsString(BikeEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Bike requires a name");
        }

        Integer price = values.getAsInteger(BikeEntry.COLUMN_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Bike requires price");
        }

        Integer quantity = values.getAsInteger(BikeEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("We have to know how many bikes we have");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BikeEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return updateBike(uri, contentValues, selection, selectionArgs);
            case BIKE_ID:
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBike(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBike(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BikeEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BikeEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Bike requires a name");
            }
        }

        if (values.containsKey(BikeEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BikeEntry.COLUMN_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Bike requires price");
            }
        }

        if (values.containsKey(BikeEntry.COLUMN_QUANTITY)) {
            Integer weight = values.getAsInteger(BikeEntry.COLUMN_QUANTITY);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("We have to know how many bikes we have");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BikeEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                rowsDeleted = database.delete(BikeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BIKE_ID:
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BikeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return BikeEntry.CONTENT_LIST_TYPE;
            case BIKE_ID:
                return BikeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}