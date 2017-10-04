package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.Contract.InventoryEntry;


public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name_item);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_item);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_item);
        ImageView saleImage = (ImageView) view.findViewById(R.id.sale_image);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);
//        image.setImageResource(R.drawable.gummibear);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

        String Name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String image1 = cursor.getString(imageColumnIndex);
//        Uri thumbUri = Uri.parse(cursor.getString(imageColumnIndex));

        nameTextView.setText(Name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));
        image.setImageURI(Uri.parse(image1));

    }
}
