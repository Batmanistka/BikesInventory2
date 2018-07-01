package com.example.android.bikesinventory;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bikesinventory.data.Contract.BikeEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentBikeUri;

    private static final int EXISTING_BIKE_LOADER = 0;

    BikeCursorAdapter mBikeCursorAdapter;

    private EditText mProductNameEditText;

    private EditText mSupplierEditText;

    private EditText mQuantity;

    private EditText mPrice;

    private EditText mSupplierPhoneEditText;

    private boolean mBikeHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBikeHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBikeUri = intent.getData();

        if (mCurrentBikeUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_bike));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_bike));

            getLoaderManager().initLoader(EXISTING_BIKE_LOADER, null, this);
        }

        mProductNameEditText = findViewById(R.id.edit_product_name);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mQuantity = findViewById(R.id.edit_quantity);
        mPrice = findViewById(R.id.edit_price);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
    }

    private void saveBike() {
        String nameString = mProductNameEditText.getText().toString().trim();
        String supplierNameString = mSupplierEditText.getText().toString().trim();
        String quantityString = mQuantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String priceString = mPrice.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        int supplierPhone = Integer.parseInt(supplierPhoneString);

        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BikeEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BikeEntry.COLUMN_QUANTITY, quantity);
        values.put(BikeEntry.COLUMN_PRICE, price);
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        if (mCurrentBikeUri == null) {
            Uri newUri = getContentResolver().insert(BikeEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_bike_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_bike_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsAffected = getContentResolver().update(mCurrentBikeUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_bike_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_bike_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBikeUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBike();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBikeHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBikeHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BikeEntry._ID,
                BikeEntry.COLUMN_PRODUCT_NAME,
                BikeEntry.COLUMN_SUPPLIER_NAME,
                BikeEntry.COLUMN_QUANTITY,
                BikeEntry.COLUMN_SUPPLIER_PHONE,
                BikeEntry.COLUMN_PRICE};

        return new CursorLoader(this,
                mCurrentBikeUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_PRODUCT_NAME);
            int suppplierNameColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_SUPPLIER_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_QUANTITY);
            int suppplierPhoneColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_SUPPLIER_PHONE);
            int priceColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_PRICE);

            String currentName = cursor.getString(productNameColumnIndex);
            String currentSupplierName = cursor.getString(suppplierNameColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentPhone = cursor.getInt(suppplierPhoneColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);

            mProductNameEditText.setText(currentName);
            mSupplierEditText.setText(currentSupplierName);
            mQuantity.setText(Integer.toString(currentQuantity));
            mSupplierPhoneEditText.setText(Integer.toString(currentPhone));
            mPrice.setText(Integer.toString(currentPrice));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mSupplierEditText.setText("");
        mQuantity.setText("");
        mSupplierPhoneEditText.setText("");
        mPrice.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBike();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBike() {
        if (mCurrentBikeUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBikeUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_bike_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_bike_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}