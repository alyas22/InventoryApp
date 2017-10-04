package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.Contract.InventoryEntry;

import static com.example.android.inventoryapp.data.DBHelper.LOG_TAG;


public class Provider extends ContentProvider {

    public static final String TAG = Provider.class.getSimpleName();

    private static final int INVENTORY = 1;
    private static final int INVENTORY_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY, INVENTORY);

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {

        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs,
                         String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);}
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);}
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(InventoryEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Item requires valid price");
        }

        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Item requires valid quantity");
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
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
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(InventoryEntry.COLUMN_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Item requires valid price");
            }

        }

        if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }
}
