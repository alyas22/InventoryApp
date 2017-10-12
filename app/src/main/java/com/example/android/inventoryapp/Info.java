package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by Toshiba on 10/10/17.
 */

public class Info extends AppCompatActivity {

    public Info() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        TextView t1 = (TextView) findViewById(R.id.github);
        t1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView t2 = (TextView) findViewById(R.id.linkedin);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
