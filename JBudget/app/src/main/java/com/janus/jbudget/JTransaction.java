package com.janus.jbudget;


import android.util.Log;

/**
 * Created by Janus on 2014-12-15.
 */
public class JTransaction {

    public String category;
    public String description;
    public double amount;


    public JTransaction()  {
        category = "";
        description = "";
        amount = 0;
    }

    public JTransaction(String category, String description, double amount) {
        this.description = description;
        this.category = category;
        this.amount = amount;
    }

    public void Log() {
        Log.d("Main", "Desc: " + description);
        Log.d("Main", "Cat: " + category);
        Log.d("Main", "Amnt: " + String.valueOf( amount ));
    }

}
