package com.example.android.inventoryapp;

/**
 * Created by Toshiba on 02/10/17.
 */

public class Inventory {

    private String mName;
    private String mPrice;
    private String mQuantity;
    private String mImage;


    public Inventory(String Name, String Price, String Quantity, String Image) {
        mName = Name;
        mPrice = Price;
        mQuantity = Quantity;
        mImage = Image;
    }


    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public String getImage() {
        return mImage;
    }
}

