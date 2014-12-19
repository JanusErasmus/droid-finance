package com.janus.jbudget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by Janus on 2014-12-19.
 */

public class OnCategorySelectListener implements OnItemSelectedListener {

    private AddTransactionActivity mParent;

    public OnCategorySelectListener(AddTransactionActivity parent){
        mParent = parent;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        if(mParent != null)
            mParent.categorySelectionChangeCallback(pos);
    }



    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
}
