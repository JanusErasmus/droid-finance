package com.janus.jbudget;


import android.util.Log;

/**
 * Created by Janus on 2014-12-15.
 */
public class JTransaction {

    private String mCategory;
    private String mDescription;
    private double mAmount;


    public JTransaction()  {
        mCategory = "";
        mDescription = "";
        mAmount = 0;
    }

    public JTransaction(String category, String Description, double amount) {
        mDescription = Description;
        mCategory = category;
        mAmount = amount;
    }

    public void Log() {
       Log.d("Main", "Desc: " + mDescription);
        Log.d("Main", "Cat: " + mCategory);
        Log.d("Main", "Amnt: " + String.valueOf( mAmount ));
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
