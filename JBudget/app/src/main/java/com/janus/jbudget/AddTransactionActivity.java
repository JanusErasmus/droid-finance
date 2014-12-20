package com.janus.jbudget;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class AddTransactionActivity extends Activity {

    private static final String ARG_TRANS_DIALOG_INDEX = "trans_dialog_selected_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction);

        List<String> list = new ArrayList<>();
        for(JCategory cat : JBudget.get().categories)
        {
            list.add(cat.heading);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        Spinner spin = (Spinner) findViewById(R.id.cat_spinner);
        spin.setAdapter(dataAdapter);
        spin.setOnItemSelectedListener(new OnCategorySelectListener(this));

        int transIdx = 0;
        Bundle b = getIntent().getExtras();
        if(!b.isEmpty())
            transIdx = b.getInt(ARG_TRANS_DIALOG_INDEX);


        if(transIdx > 0)
        {
            fillActivity(JBudget.get().transactionList.get(transIdx));
            return;
        }

        //ensure the first in the list is selected
        spin.setSelection(0);
        categorySelectionChangeCallback(0);


    }

    private void fillActivity(JTransaction trans) {
        Log.d("Main", "Trans: " + trans.category + " " + trans.description + " " + trans.amount);
    }

    private void fillSubCategorySpinner(Spinner desc, int idx){

        List<String> subCatList = new ArrayList<>();
        for(JCategory subCat : JBudget.get().categories.get(idx).subCategories)
        {
            subCatList.add(subCat.heading);
        }

        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,subCatList);

        subCatAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        desc.setAdapter(subCatAdapter);
    }

    public void categorySelectionChangeCallback(int idx) {

        EditText descEdit = (EditText) findViewById(R.id.desc_edit);
        Spinner descSpinner = (Spinner) findViewById(R.id.desc_spinner);

        if(JBudget.get().categories.get(idx).hasSubCategories())
        {

            fillSubCategorySpinner(descSpinner, idx);
            descSpinner.setVisibility(View.VISIBLE);
            descEdit.setVisibility(View.GONE);
        }
        else
        {
            descSpinner.setVisibility(View.GONE);
            descEdit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void AddTransaction(View view) {

        if(view == null)
            return;

        Spinner catSpinner = (Spinner) findViewById(R.id.cat_spinner);
        int catIdx = catSpinner.getSelectedItemPosition();
        String cat = JBudget.get().categories.get(catIdx).heading;

        String desc;
        EditText txt = (EditText) findViewById(R.id.desc_edit);

        if(txt.getVisibility() == View.VISIBLE)
        {
            desc = txt.getText().toString();
        }
        else
        {
            Spinner descSpinner = (Spinner) findViewById(R.id.desc_spinner);
            int descIdx = descSpinner.getSelectedItemPosition();
            desc = JBudget.get().categories.get(catIdx).subCategories.get(descIdx).heading;
        }


        txt = (EditText) findViewById(R.id.amount_edit);
        float amount = 0;
        String amountString = txt.getText().toString();
        if(!amountString.isEmpty())
            amount = Float.valueOf(amountString);

        //Log.d("Main", "Add " + cat + desc + " " + amount);

        JBudget.get().transactionList.add(new JTransaction(desc, cat, amount));
        JBudget.get().budgetChanged();

        finish();
    }
}
