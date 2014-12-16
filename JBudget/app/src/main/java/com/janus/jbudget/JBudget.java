package com.janus.jbudget;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janus on 2014-12-16.
 */
public class JBudget {

    static private JBudget __instance = null;

    private List<JTransaction> transactionList;
    private String mName;
    private String mFileName;

    static public void init() {
        if (__instance == null) {
            __instance = new JBudget();
        }
    }

    static public JBudget get() {
        return __instance;
    }

    private JBudget() {
        transactionList = new ArrayList<JTransaction>();

        mName = "Empty Budget";

        transactionList.add(new JTransaction("Werk", "Mens", 5.568));
        transactionList.add(new JTransaction("Skool", "Sakgeld", -6.4));
        transactionList.add(new JTransaction("Beursie", "Noodsaaklik", 7.23));

    }

    public boolean open(String fileName) {
        Log.d("Main", "Opening budget " + fileName);

        mFileName = fileName;

        File budget = new File(fileName);
        mName = budget.getName();

        populateBudget(budget);


        return false;
    }

    public List<JTransaction> getTransactionList() {
        return transactionList;
    }

    public String getName() {
        return mName;
    }

    private void populateBudget(File budget) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(budget));

        } catch (FileNotFoundException e) {
            Log.d("Main", "File not found" + e.toString());
        }

        if(in != null)
        {
            Log.d("Main", "File opened...");

            byte buffer[] = new byte[256];

            try {
                int len = in.read(buffer);
                for(int k =0;k<len;k++)
                {
                    Log.d("Main",String.valueOf(buffer[k]));
                }
            }
            catch (IOException e) {
                Log.d("Main", "File IO" + e.toString());
            }

            try{
                in.close();
            }
            catch (IOException e) {
            Log.d("Main", "File IO" + e.toString());
        }
        }
    }


    public String save() {

        Log.d("Main", "Saved budget");

        return mFileName;
    }
}
