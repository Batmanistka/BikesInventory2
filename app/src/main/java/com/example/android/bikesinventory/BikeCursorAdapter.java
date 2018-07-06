package com.example.android.bikesinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bikesinventory.data.Contract;

public class BikeCursorAdapter extends CursorAdapter {

    public BikeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameCatalogTextView = view.findViewById(R.id.catalog_name);
        TextView priceCatalogTextView = view.findViewById(R.id.catalog_price);
        TextView quantityCatalogTextView = view.findViewById(R.id.catalog_quantity);
        ImageButton sellButton = view.findViewById(R.id.sell_button);

        int productNameColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_QUANTITY);

        String bikeName = cursor.getString(productNameColumnIndex);
        String priceBike = "Price: " + cursor.getString(priceColumnIndex) + " zl";
        String quantityBike = "Quantity: " + cursor.getString(quantityColumnIndex);

        nameCatalogTextView.setText(bikeName);
        priceCatalogTextView.setText(priceBike);
        quantityCatalogTextView.setText(quantityBike);

        String currentQuantityString = cursor.getString(quantityColumnIndex);

        final int currentQuantity = Integer.valueOf(currentQuantityString);

        final int productId = cursor.getInt(cursor.getColumnIndex(Contract.BikeEntry._ID));

        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentQuantity > 0) {
                    int newQuantity = currentQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(Contract.BikeEntry.CONTENT_URI, productId);
                    ContentValues values = new ContentValues();
                    values.put(Contract.BikeEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                } else {
                    Toast.makeText(context, "This bike is out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

