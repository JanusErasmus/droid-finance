package com.janus.jbudget;


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
