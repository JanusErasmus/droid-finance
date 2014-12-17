package com.janus.jbudget;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Janus on 2014-12-16.
 * For JBudget Android App.
 */
public class JBudget {

    static private JBudget __instance = null;

    public List<JTransaction> transactionList;
    public List<JCategory> categories;
    public String name;

    private boolean mChanged;
    private String mFileName;
    private int mVersion;
    private float mIncome;
    private float mBank;

    static public void init() {
        if (__instance == null) {
            __instance = new JBudget();
        }
    }

    static public JBudget get() {
        return __instance;
    }

    private JBudget() {

        mChanged = false;
        transactionList = new ArrayList<>();
        categories = new ArrayList<>();

        name = "Empty Budget";
        mVersion = -1;
    }

    public boolean open(String fileName) {
        //Log.d("Main", "Opening budget " + fileName);

        transactionList = new ArrayList<>();
        categories = new ArrayList<>();

        mFileName = fileName;

        File budget = new File(fileName);
        name = budget.getName();

        int len = 0;
        int size = (int)budget.length();
        byte buffer[] = new byte[size];

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(budget));

        } catch (FileNotFoundException e) {
            Log.d("Main", "File not found" + e.toString());
        }

        if(in != null)
        {
            try {
                len = in.read(buffer);
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

        if(len > 0)
        {
            populateBudget(buffer, len);

            return true;
        }

        return false;
    }

    private void populateBudget(byte buff[], int len) {

        if(len < 12)
            return;

        //first 4 bytes is the version
        byte intBytes[] = Arrays.copyOf(buff, 4);
        ByteBuffer bb = ByteBuffer.wrap(intBytes);
        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
        mVersion = bb.getInt();
        //Log.d("Main", "Populate budget version: " + String.valueOf(mVersion));

        //next 4 bytes is income value
        intBytes = Arrays.copyOfRange(buff, 4, 8);
        bb = ByteBuffer.wrap(intBytes);
        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
        mIncome = bb.getFloat();
        //Log.d("Main", "Income: " + mIncome);

        //next 4 bytes is bank value
        intBytes = Arrays.copyOfRange(buff, 8, 12);
        bb = ByteBuffer.wrap(intBytes);
        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
        mBank = bb.getFloat();
        //Log.d("Main", "Bank: " + mBank);

        //next 4 bytes is the length of the transactions
        intBytes = Arrays.copyOfRange(buff, 12, 16);
        bb = ByteBuffer.wrap(intBytes);
        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
        int transCount = bb.getInt();
        //Log.d("Main", "Transactions: " + transCount);

        //now read all the transactions from this block of memory
        /**A transaction in version 1 has
         * 2  Bytes for year
         * 2  Bytes for month
         * 2  Bytes for day
         * 64 Bytes for description
         * 32 Bytes for category
         * 4  Bytes for amount
         *  equals 106 Bytes per transaction
         */

        int transBuffLen = transCount * 106;
        byte transBuff[] = Arrays.copyOfRange(buff, 16, (16 + transBuffLen));
        fillTransactions(transBuff, transCount);

        byte catBuff[] = Arrays.copyOfRange(buff, (16 + transBuffLen), buff.length);
        fillCategories(catBuff);
    }

    private void fillTransactions(byte buff[], int len) {

        byte trBuff[];
        int start = 0;
        int end = 106;

        for(int k = 0; k < len; k++) {
            //extract transactions 106 bytes at a time
            trBuff = Arrays.copyOfRange(buff, start, end);

            transactionList.add(parseTransaction(trBuff));

            start = end;
            end += 106;
        }
    }

    private JTransaction parseTransaction(byte buff[]) {

        byte intBytes[];
        ByteBuffer bb;

//        //first 2 bytes is the year
//        intBytes = Arrays.copyOf(buff, 2);
//        bb = ByteBuffer.wrap(intBytes);
//        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
//        int year = bb.getInt();
//
//        //next 2 bytes is the month
//        intBytes = Arrays.copyOfRange(buff, 2, 4);
//        bb = ByteBuffer.wrap(intBytes);
//        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
//        int month = bb.getInt();
//
//        //next 2 bytes is the day
//        intBytes = Arrays.copyOfRange(buff, 4, 6);
//        bb = ByteBuffer.wrap(intBytes);
//        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
//        int day = bb.getInt();


        //next 64 bytes is the description
        intBytes = Arrays.copyOfRange(buff, 6, 70);
        String desc = new String(intBytes).replaceAll("\u0000.*", "");

        //next 32 bytes is the category
        intBytes = Arrays.copyOfRange(buff, 70, 102);
        String cat =  new String(intBytes).replaceAll("\u0000.*", "");


        //next 4 bytes is the amount
        intBytes = Arrays.copyOfRange(buff, 102, 106);
        bb = ByteBuffer.wrap(intBytes);
        bb = bb.order(ByteOrder.LITTLE_ENDIAN);
        float amount = bb.getFloat();

        return new JTransaction(desc, cat, amount);
    }

    private void fillCategories(byte buff[]) {

        byte intBytes[];
        ByteBuffer bb;
        String cat;

        int start;
        int end = 0;

        while(end < buff.length) {

            //first 4 bytes is the amount of categories in the list
            start = end;
            end += 4;
            intBytes = Arrays.copyOfRange(buff, start, end);
            bb = ByteBuffer.wrap(intBytes);
            bb = bb.order(ByteOrder.LITTLE_ENDIAN);
            int catCount = bb.getInt();
            //Log.d("Main", "Categories: " + catCount);

            //next 32 bytes is the heading
            start = end;
            end += 32;
            intBytes = Arrays.copyOfRange(buff, start, end);
            cat = new String(intBytes).replaceAll("\u0000.*", "");

            //next 4 bytes is the amount
            start = end;
            end += 4;
            intBytes = Arrays.copyOfRange(buff, start, end);
            bb = ByteBuffer.wrap(intBytes);
            bb = bb.order(ByteOrder.LITTLE_ENDIAN);
            float amount = bb.getFloat();

            JCategory category = new JCategory(cat, amount);
            //Log.d("Main", cat + amount);

            for(int k = 0; k < catCount; k++)
            {
                //next 32 bytes is the sub-category name
                start = end;
                end += 32;
                intBytes = Arrays.copyOfRange(buff, start, end);
                cat = new String(intBytes).replaceAll("\u0000.*", "");

                //next 4 bytes is the amount
                start = end;
                end += 4;
                intBytes = Arrays.copyOfRange(buff, start, end);
                bb = ByteBuffer.wrap(intBytes);
                bb = bb.order(ByteOrder.LITTLE_ENDIAN);
                float subAmount = bb.getFloat();

                category.subCategories.add(new JCategory(cat, subAmount));

                //Log.d("Main", " - " + cat + subAmount);
            }

            categories.add(category);

        }

    }

    public String save() {

        if(mChanged) {

            Log.d("Main", "Saved budget");
        }

        return mFileName;
    }

}
