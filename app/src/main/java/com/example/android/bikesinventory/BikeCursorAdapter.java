package com.example.android.bikesinventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameCatalogTextView = view.findViewById(R.id.catalog_name);
        TextView priceCatalogTextView = view.findViewById(R.id.catalog_price);
        TextView quantityCatalogTextView = view.findViewById(R.id.catalog_quantity);

        int productNameColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_QUANTITY);

        String bikeName = cursor.getString(productNameColumnIndex);
        String priceBike = cursor.getString(priceColumnIndex);
        String quantityBike = cursor.getString(quantityColumnIndex);

        nameCatalogTextView.setText(bikeName);
        priceCatalogTextView.setText(priceBike);
        quantityCatalogTextView.setText(quantityBike);
    }
}
