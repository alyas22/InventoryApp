package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp.data.Contract.InventoryEntry;

import static com.example.android.inventoryapp.R.drawable.ic_photo;


public class InventoryCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();
    private final MainActivity mainActivity;

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.mainActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name_item);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_item);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_item);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        ImageView saleImage = (ImageView) view.findViewById(R.id.sale_image);


        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

        final int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String Name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        String price1 = "Price: $" + price;
        final int quantity = cursor.getInt(quantityColumnIndex);
        final Uri image1 = Uri.parse(cursor.getString(imageColumnIndex));

        nameTextView.setText(Name);
        priceTextView.setText(price1);
        quantityTextView.setText(String.valueOf(quantity));
        Glide.with(context).load(image1).placeholder(ic_photo).error(ic_photo).crossFade().centerCrop().into(imageView);

        saleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int quantity1 = quantity - 1;
                    values.put(InventoryEntry.COLUMN_QUANTITY, quantity1);
                    view.getContext().getContentResolver().update(uri, values, null, null);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mainActivity.fullScreenImage(image1);
            }
        });
    }

}