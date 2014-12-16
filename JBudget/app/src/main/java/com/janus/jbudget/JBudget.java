package com.janus.jbudget;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Janus on 2014-12-16.
 */
public class JBudget {

    static private JBudget __instance = null;

    List<JTransaction> transactionList;

    static public void init() {
        if(__instance == null)
        {
            __instance = new JBudget();
        }
    }

    static public JBudget get() {
        return __instance;
    }

    private JBudget(){
        transactionList = new ArrayList<JTransaction>();

        transactionList.add(new JTransaction( "Werk", "Mens", 5.568));
        transactionList.add(new JTransaction( "Skool", "Sakgeld", -6.4));
        transactionList.add(new JTransaction( "Beursie", "Noodsaaklik", 7.23));

    }

    public List<JTransaction> getTransactionList() {
        return transactionList;
    }

    public void save() {
        Log.d("Main", "Saved budget");
    }
}
