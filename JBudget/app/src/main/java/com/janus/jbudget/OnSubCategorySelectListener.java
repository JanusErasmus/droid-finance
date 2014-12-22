package com.janus.jbudget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by Janus on 2014-12-19.
 * For JBudget droid application
 */

public class OnSubCategorySelectListener implements OnItemSelectedListener {

    private AddTransactionActivity mParent;

    public OnSubCategorySelectListener(AddTransactionActivity parent){
        mParent = parent;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        if(mParent != null)
            mParent.setAmountCallback(-1, pos);
    }



    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
