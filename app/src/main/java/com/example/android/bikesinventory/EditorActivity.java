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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.bikesinventory.data.Contract.BikeEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BIKE_LOADER = 0;
    BikeCursorAdapter mBikeCursorAdapter;
    private Uri mCurrentBikeUri;
    private EditText mProductNameEditText;

    private EditText mSupplierEditText;

    private EditText mQuantity;

    private EditText mPrice;

    private EditText mSupplierPhoneEditText;

    private boolean mBikeHasChanged = false;

    private int changeQuantity;

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

        ImageButton mPlusQuantityBike = findViewById(R.id.plus_bike);
        ImageButton mMinusQuantityBike = findViewById(R.id.minus_bike);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mPlusQuantityBike.setOnTouchListener(mTouchListener);
        mMinusQuantityBike.setOnTouchListener(mTouchListener);

        mPlusQuantityBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    changeQuantity = Integer.parseInt(quantity);
                    mQuantity.setText(String.valueOf(changeQuantity + 1));
                }
            }
        });

        mMinusQuantityBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(EditorActivity.this, R.string.editor_quantity_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    changeQuantity = Integer.parseInt(quantity);
                    if ((changeQuantity - 1) >= 0) {
                        mQuantity.setText(String.valueOf(changeQuantity - 1));
                    } else {
                        Toast.makeText(EditorActivity.this, R.string.editor_quantity_less_then_0, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        final Button mCallToOrderButton = findViewById(R.id.call_to_order);

        mCallToOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mSupplierPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
            }

        });
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

    private void saveBike() {
        String nameString = mProductNameEditText.getText().toString().trim();
        String quantityString = mQuantity.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String supplierNameString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        if (mCurrentBikeUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, getString(R.string.fill_fields), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            mProductNameEditText.setError(getString(R.string.fill_name_field));
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            mPrice.setError(getString(R.string.fill_price_field));
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            mQuantity.setError(getString(R.string.fill_quantity_field));
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            mSupplierEditText.setError(getString(R.string.fill_sup_name_field));
            return;
        }
        if (TextUtils.isEmpty(supplierPhoneString)) {
            mSupplierPhoneEditText.setError(getString(R.string.fill_sup_phone_field));
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BikeEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(BikeEntry.COLUMN_QUANTITY, quantityString);
        values.put(BikeEntry.COLUMN_PRICE, priceString);
        values.put(BikeEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BikeEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        if (mCurrentBikeUri == null) {
            Uri newUri = getContentResolver().insert(BikeEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_bike_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_bike_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();

        } else {
            int rowsAffected = getContentResolver().update(mCurrentBikeUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_bike_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_bike_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BikeEntry._ID,
                BikeEntry.COLUMN_PRODUCT_NAME,
                BikeEntry.COLUMN_PRICE,
                BikeEntry.COLUMN_QUANTITY,
                BikeEntry.COLUMN_SUPPLIER_NAME,
                BikeEntry.COLUMN_SUPPLIER_PHONE,
        };

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
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_SUPPLIER_PHONE);
            int priceColumnIndex = cursor.getColumnIndex(BikeEntry.COLUMN_PRICE);

            String currentName = cursor.getString(productNameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplierName = cursor.getString(suppplierNameColumnIndex);
            int currentPhone = cursor.getInt(supplierPhoneColumnIndex);

            mProductNameEditText.setText(currentName);
            mPrice.setText(Integer.toString(currentPrice));
            mQuantity.setText(Integer.toString(currentQuantity));
            mSupplierEditText.setText(currentSupplierName);
            mSupplierPhoneEditText.setText(Integer.toString(currentPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
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

        finish();
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
}