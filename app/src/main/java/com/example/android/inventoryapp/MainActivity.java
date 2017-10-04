package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.Contract.InventoryEntry;
import com.example.android.inventoryapp.data.DBHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    private DBHelper mDbHelper;
    private static final int INVENTORY_LOADER = 0;
    InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });
//        mDbHelper = new DBHelper(this);

    }
    private void insertItem() {

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, "Toto");
        values.put(InventoryEntry.COLUMN_PRICE, 15);
        values.put(InventoryEntry.COLUMN_QUANTITY, 0);
        values.put(InventoryEntry.COLUMN_QUANTITY, "android.resource://"+ getPackageName()+"/drawable/ic_store");

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }
    private void deleteAllItem() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from Inventory database");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllItem();
                return true;}
        return super.onOptionsItemSelected(item);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_IMAGE};

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
           }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

