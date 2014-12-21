package com.janus.jbudget;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	public List<JCategory> categoryBalance;
	
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
        categoryBalance = new ArrayList<>();

        name = "Empty Budget";
        mVersion = -1;
    }

    public void budgetChanged() {
        mChanged = true;
		calculateBalance();
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
			calculateBalance();
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
        String desc = getHeading(intBytes);

        //next 32 bytes is the category
        intBytes = Arrays.copyOfRange(buff, 70, 102);
        String cat =  getHeading(intBytes);


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
            cat = getHeading(intBytes);

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
                cat = getHeading(intBytes);

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
	
	private String getHeading(byte buff[])
    {
    	String heading = new String(buff);
    	int idx = heading.indexOf('\u0000');    	
    	return heading.substring(0, idx);
    }

    public String save() {

        if(mChanged) {
            mChanged = false;

            if(writeBudget(mFileName)) {
                Log.d("Main", "Saved budget");

                return mFileName;
            }
        }

        return null;
    }
	
	private boolean writeBudget(String fileName) {

    	File budget = new File(fileName);

    	OutputStream out = null;
    	try {
    		out = new BufferedOutputStream(new FileOutputStream(budget));

    	} catch (FileNotFoundException e) {
            Log.d("Main", "File not found" + e.toString());
    	}

    	if(out == null)
    		return false;

    	byte intBytes[];
    	ByteBuffer bb;

    	//first 4 bytes is the version
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putInt(mVersion);
    	intBytes = bb.array();
    	write(out, intBytes);

    	//next 4 bytes is income value
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putFloat(mIncome);
    	intBytes = bb.array();
    	write(out, intBytes);

    	//next 4 bytes is bank value
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putFloat(mBank);
    	intBytes = bb.array();
    	write(out, intBytes);

    	writeTransactions(out);
    	writeCategories(out);

    	try {
    		out.close();
    	} catch (IOException e) {
            Log.d("Main", "File not closed" + e.toString());
    	}

    	return true;
    }

    private void writeTransactions(OutputStream out) {

    	ByteBuffer bb;
    	byte bytes[];

    	//next 4 bytes is the length of the transactions
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putInt(transactionList.size());
    	bytes = bb.array();
    	write(out, bytes);


    	for(JTransaction trans : transactionList)
    	{
    		bytes = getTransaction(trans);
    		write(out, bytes);
    	}
    }

    private byte[] getTransaction(JTransaction trans) {

    	byte bytes[] = new byte[106];
    	ByteBuffer bb;

    	//first 2 bytes is the year
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putInt(2014);
    	System.arraycopy(bb.array(), 0, bytes, 0, 2);

    	//next 2 bytes is the month
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putInt(4);
    	System.arraycopy(bb.array(), 0, bytes, 2, 2);

    	//next 2 bytes is the day
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putInt(14);
    	System.arraycopy(bb.array(), 0, bytes, 4, 2);


    	//next 64 bytes is the description
    	byte stringBytes[] = new byte[64];
    	System.arraycopy(trans.description.getBytes(), 0, stringBytes, 0, trans.description.length());
    	System.arraycopy(stringBytes, 0, bytes, 6, 64);

    	//next 32 bytes is the category
    	stringBytes = new byte[32];
    	System.arraycopy(trans.category.getBytes(), 0, stringBytes, 0, trans.category.length());
    	System.arraycopy(stringBytes, 0, bytes, 70, 32);


    	//next 4 bytes is the amount
    	bb = ByteBuffer.allocate(4);
    	bb.order(ByteOrder.LITTLE_ENDIAN); 
    	bb.putFloat(trans.amount);
    	System.arraycopy(bb.array(), 0, bytes, 102, 4);


    	return bytes;
    }

    private void writeCategories(OutputStream out) {

    	byte intBytes[];
    	ByteBuffer bb;

    	for(JCategory cat : categories)
    	{
    		//first 4 bytes is the amount of categories in the list
    		bb = ByteBuffer.allocate(4);
    		bb.order(ByteOrder.LITTLE_ENDIAN); 
    		bb.putInt(cat.subCategories.size());
    		intBytes = bb.array();
    		write(out, intBytes);

    		//next 32 bytes is the heading
    		intBytes = cat.heading.getBytes();
    		bb = ByteBuffer.allocate(32);
    		bb.put(intBytes, 0, intBytes.length);
    		intBytes = bb.array();
    		write(out, intBytes);		

    		//next 4 bytes is the amount
    		bb = ByteBuffer.allocate(4);
    		bb.order(ByteOrder.LITTLE_ENDIAN); 
    		bb.putFloat(cat.amount);
    		intBytes = bb.array();
    		write(out, intBytes);

    		if(cat.hasSubCategories())
    		{
    			for(JCategory subCat : cat.subCategories)
    			{
    				//next 32 bytes is the heading
    				intBytes = subCat.heading.getBytes();
    				bb = ByteBuffer.allocate(32);
    				bb.put(intBytes, 0, intBytes.length);
    				intBytes = bb.array();
    				write(out, intBytes);		

    				//next 4 bytes is the amount
    				bb = ByteBuffer.allocate(4);
    				bb.order(ByteOrder.LITTLE_ENDIAN); 
    				bb.putFloat(subCat.amount);
    				intBytes = bb.array();
    				write(out, intBytes);
    			}
    		}
    	}

    }

    private void write(OutputStream out, byte buff[]) {

    	if(out == null)
    		return;

    	try {
    		out.write(buff);
    	} catch (IOException e) {
    		Log.d("Main", "File not found" + e.toString());
    	}
    }

	/** Fills the categoryBalance list
	 * 
	 */
	private void calculateBalance() {

        categoryBalance.clear();


		for(JCategory cat : categories)
		{			
			JCategory parent;
			
			if(cat.hasSubCategories())
			{    			
				parent = new JCategory(cat.heading, 0);
				
				for(JCategory subCat : cat.subCategories)
				{
					parent.subCategories.add(new JCategory(subCat.heading, getBalance(cat, subCat)));
				}
			}
			else
			{
				parent = new JCategory(cat.heading, getBalance(cat));
			}

			categoryBalance.add(parent);
		}		
	}

	private float getBalance(JCategory parent) {

		float balance = parent.amount;

		for(JTransaction trans : transactionList) 
		{    		
			if(trans.category.equals(parent.heading))
			{
				balance -= trans.amount;
			}
		}

		return balance;
	}    

	private float getBalance(JCategory parent, JCategory child) {

		float balance = child.amount;

		for(JTransaction trans : transactionList) 
		{
			if((trans.category.equals(parent.heading)) && (trans.description.equals(child.heading)))
				balance -= trans.amount;
		}

		return balance;
	}

    public int getCategoryIndex(String heading) {

        for(int k = 0; k < categories.size(); k++)
        {
            if(categories.get(k).heading.equals(heading))
                return k;
        }

        return -1;
    }

}
