package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.Contract.InventoryEntry;

import java.io.File;

/**
 * Created by Toshiba on 02/10/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    ImageView imageView, addQuantity, removeQuantity;
    private Uri mCurrentItemUri;
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private static final int PICK_PICTURE_REQUEST = 0;

    private View mLayout;
    public static final String LOG_TAG = "EditorActivity";

    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        imageView = (ImageView) findViewById(R.id.image_view);
        addQuantity = (ImageView) findViewById(R.id.add_quantity);
        removeQuantity = (ImageView) findViewById(R.id.remove_quantity);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        imageView.setOnTouchListener(mTouchListener);
        addQuantity.setOnTouchListener(mTouchListener);
        removeQuantity.setOnTouchListener(mTouchListener);


        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.add_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPhoto(view);
            }
        });


        removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String previousString = mQuantityEditText.getText().toString();
                int previousInt;

                if (previousString.isEmpty()) {
                    return;
                } else if (previousString.equals("1")) {
                    Snackbar.make(view, "You cannot have less than 1 item.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }else {
                    previousInt = Integer.parseInt(previousString);
                    displayQuantity(previousInt - 1);
                }
            }
        });

        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String previousString = mQuantityEditText.getText().toString();
                int previousInt;
                if (previousString.isEmpty()) {
                    previousInt = 0;
                } else {
                    previousInt = Integer.parseInt(previousString);
                }
                displayQuantity(previousInt + 1);
            }
        });
    }

    private void displayQuantity(int number) {
        mQuantityEditText.setText("" + number);
    }


    private void showOrderConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("kj");
        builder.setPositiveButton("jj", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String name = mNameEditText.getText().toString().trim();
                String quantity = mQuantityEditText.getText().toString().trim();
                String TO_Email = "supplier@"+ name +".com";

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:" ));
                intent.putExtra(Intent.EXTRA_EMAIL , TO_Email);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "New order for "+ name);
                String bodyMessage = "Please send us as soon as possible more " +
                                      name + " in quantities  " + quantity;
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                    startActivity(intent);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }}});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // اقدر احطها بميثود onCreateOptionsMenu
        if (mCurrentItemUri == null) {
            MenuItem menuItem1 = menu.findItem(R.id.action_delete_item);
            MenuItem menuItem2 =  menu.findItem(R.id.action_order_item);

            menuItem1.setVisible(false);
            menuItem2.setVisible(false);

        }
        return true;
    }
    private void saveItem() {

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(priceString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_QUANTITY, "android.resource://"+ getPackageName()+"/drawable/ic_store");

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(InventoryEntry.COLUMN_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

        if (mCurrentItemUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful), Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
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

    private void deletePet() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;

            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_order_item:
                showOrderConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
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
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if( requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                Log.i(LOG_TAG, "Received response for Image permission request.");

                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                }else {
//                    Toast.makeText(this, "R.string.err_external_storage_permissions", Toast.LENGTH_LONG).show();
                   Snackbar.make(mLayout , "err_external_storage_permissions", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }}

    private void galleryIntent() {

        Intent photoIntent = new Intent();
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);
        photoIntent.setDataAndType(data, "image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(photoIntent, "Select File"),PICK_PICTURE_REQUEST);
//        Intent photoIntent = new Intent(Intent.ACTION_PICK);
//
//        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String pictureDirectoryPath = pictureDirectory.getPath();
//
//        Uri data = Uri.parse(pictureDirectoryPath);
//        photoIntent.setDataAndType(data, "image/*");
//
//        startActivityForResult(photoIntent, PICK_PICTURE_REQUEST);
    }
    public void displayPhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this,permisionRequest, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            galleryIntent();
        }
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
                mCurrentItemUri,
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);

            String Name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String image1 = cursor.getString(imageColumnIndex);

            mNameEditText.setText(Name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            imageView.setImageURI(Uri.parse(image1));

    }}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        imageView.setImageResource(R.drawable.ic_add_photo);
    }
}
