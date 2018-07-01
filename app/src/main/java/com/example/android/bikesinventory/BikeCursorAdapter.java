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
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);

        int productNameColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_PRODUCT_NAME);
        int suppplierNameColumnIndex = cursor.getColumnIndex(Contract.BikeEntry.COLUMN_SUPPLIER_NAME);

        String bikeName = cursor.getString(productNameColumnIndex);
        String suppplierName = cursor.getString(suppplierNameColumnIndex);

        if (TextUtils.isEmpty(suppplierName)) {
            suppplierName = context.getString(R.string.unknown_supplier);
        }
        // Populate fields with extracted properties
        nameTextView.setText(bikeName);
        summaryTextView.setText(suppplierName);
    }
}
