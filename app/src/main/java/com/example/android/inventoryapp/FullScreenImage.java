package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Toshiba on 11/10/17.
 */

public class FullScreenImage extends Activity {

    public FullScreenImage() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_image);

        ImageView imageView;
        ImageView btnClose;

        imageView = (ImageView) findViewById(R.id.imageDisplay);
        btnClose = (ImageView) findViewById(R.id.close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            Uri imageUri = intent.getData();
            if (imageUri != null && imageView != null) {
                Glide.with(this).load(imageUri).into(imageView);
            }
        }
    }
}