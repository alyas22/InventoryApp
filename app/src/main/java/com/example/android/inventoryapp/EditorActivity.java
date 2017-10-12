package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.inventoryapp.data.Contract.InventoryEntry;

import static com.example.android.inventoryapp.R.drawable.ic_photo;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    ImageView imageView, addQuantity, removeQuantity;
    Button imageButton;
    private Uri mCurrentItemUri;
    private String image1 = "none";
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;
    private static final int PICK_PICTURE_REQUEST = 0;

    private View mLayout;
    public static final String LOG_TAG = "EditorActivity";

    private boolean mItemHasChanged = false;
    private boolean zoomOut =  false;
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
        imageView = (ImageView) findViewById(R.id.image_view_edit);
        imageButton = (Button) findViewById(R.id.image_button);
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
                if(zoomOut) {
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setAdjustViewBounds(true);
                    zoomOut =false;
                }else{
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    zoomOut = true;
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhoto(v);
            }
        });

        removeQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String previousString = mQuantityEditText.getText().toString();
                int previousInt;

                if (previousString.isEmpty()) {
                } else if (previousString.equals("0")) {
                } else if (previousString.equals("1")) {
                    Snackbar.make(view, R.string.error_1, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    previousInt = Integer.parseInt(previousString);
                    int newInt = previousInt - 1;
                    displayQuantity(newInt);
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
                int newInt = previousInt + 1;
                displayQuantity(newInt);
            }
        });
    }

    private void displayQuantity(int number) {
        mQuantityEditText.setText("" + number);
    }

    private void showOrderConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to order more?");
        builder.setPositiveButton("ORDER", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String name = mNameEditText.getText().toString().trim();
                String quantity = mQuantityEditText.getText().toString().trim();
                String price = mPriceEditText.getText().toString().trim();
                String[] TO_Email = {name + "@supplier.com"};

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, TO_Email);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "New order for " + name);
                String message = "Please send me " +
                        quantity + " " + name + '\n' + "Price: $" + price + '\n' + '\n' + "THANK YOU";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem1 = menu.findItem(R.id.action_delete_item);
            MenuItem menuItem2 = menu.findItem(R.id.action_order_item);

            menuItem1.setVisible(false);
            menuItem2.setVisible(false);
        }
        return true;
    }

    private void saveItem() {

        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (mCurrentItemUri == null) {
            if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_IMAGE, image1);

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
        finish();
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
                Toast.makeText(this, getString(R.string.editor_delete_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                }
            }
        }
    }

    private void galleryIntent() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PICTURE_REQUEST);
    }

    public void displayPhoto(View v) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        galleryIntent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_PICTURE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri mProductPhotoUri = resultData.getData();
                image1 = mProductPhotoUri.toString();
                if (resultData != null) {
                    Glide.with(this).load(mProductPhotoUri).placeholder(ic_photo).error(ic_photo)
                            .crossFade().centerCrop().into(imageView);
                }
            }
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
            image1 = cursor.getString(imageColumnIndex);

            mNameEditText.setText(Name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            Glide.with(this).load(image1).placeholder(ic_photo).error(ic_photo)
                    .crossFade().centerCrop().into(imageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        imageView.setImageResource(R.drawable.ic_photo);
    }
}
