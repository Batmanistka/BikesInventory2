package com.example.android.bikesinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.bikesinventory.data.Contract.BikeEntry;

import java.util.Objects;

public class Provider extends ContentProvider {

    private static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int BIKES = 100;
    private static final int BIKE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_BIKES, BIKES);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_BIKES + "/#", BIKE_ID);
    }

    private DbHelper mDbHelper;

    public boolean onCreate() {

        // Creates a new database object.
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                cursor = database.query(BikeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BIKE_ID:
                selection = BikeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the clothes table where the _id quals 3 to return a
                // Curso containing that row of the table.

                cursor = database.query(BikeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return BikeEntry.CONTENT_LIST_TYPE;
            case BIKE_ID:
                return BikeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unkown URI" + uri + " with match " + match);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIKES:
                return insertBike(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    private Uri insertBike(Uri uri, ContentValues values) {

        String productName = values.getAsString(BikeEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Bike requires a name");
        }
        Integer price = values.getAsInteger(BikeEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Bike requires price");
        }

        Integer quantity = values.getAsInteger(BikeEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("We have to know how many bikes we have");
        }
        String supplierName = values.getAsString(BikeEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException(" A name of the supplier must be filled in");
        }

        Long supplierPhoneNumber = values.getAsLong(BikeEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhoneNumber == null)
            throw new IllegalArgumentException(" A valid phone number must be filled in");

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BikeEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Falied to insert row for " + uri);
            return null;
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

        @Override
        public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

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
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
            }
            return rowsDeleted;
        }

        @Override
        public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case BIKES:
                    return updateClothes(uri, contentValues, selection, selectionArgs);
                case BIKE_ID:
                    selection = BikeEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    return updateClothes(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }

        private int updateClothes(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            if (values.containsKey(BikeEntry.COLUMN_PRODUCT_NAME)) {
                String productName = values.getAsString(BikeEntry.COLUMN_PRODUCT_NAME);
                if (productName == null) {
                    throw new IllegalArgumentException(" Product requires a name");
                }
            }

            if (values.containsKey(BikeEntry.COLUMN_PRICE)) {
                Integer price = values.getAsInteger(BikeEntry.COLUMN_PRICE);
                if (price != null && price < 0) {
                    throw new IllegalArgumentException("Product requires a valid price either 0 or above");
                }
            }

            if (values.containsKey(BikeEntry.COLUMN_SUPPLIER_NAME)) {
                String supplierName = values.getAsString(BikeEntry.COLUMN_SUPPLIER_NAME);
                if (supplierName == null) {
                    throw new IllegalArgumentException("Please insert a valid supplier name");
                }
            }

            if (values.containsKey(BikeEntry.COLUMN_SUPPLIER_PHONE)) {
                Long supplierPhoneNumber = values.getAsLong(BikeEntry.COLUMN_SUPPLIER_PHONE);
                if (supplierPhoneNumber == null) {
                    throw new IllegalArgumentException("Plese insert a valid phone number");
                }
                if (values.size() == 0) {
                    return 0;
                }
            }

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            int rowsUpdated = database.update(BikeEntry.TABLE_NAME, values, selection, selectionArgs);

            if (rowsUpdated != 0)
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

            return rowsUpdated;
        }
    }