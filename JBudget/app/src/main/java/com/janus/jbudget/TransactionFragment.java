package com.janus.jbudget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TransactionFragment newInstance(int sectionNumber) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TransactionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        ListView list = (ListView) view.findViewById(R.id.trans_list);

        JTransArrayAdapter adapter;
        adapter = new JTransArrayAdapter(
                getActivity(),
                JBudget.get().transactionList
        );
        list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //this is part of index in the MainActivity, let MainActivity know this was attached
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));


    }

static int count = -1;
    public void onResume()
    {
        super.onResume();

        ListView list = null;

        try {
            list = (ListView) getView().findViewById(R.id.trans_list);
        }catch(NullPointerException e) {
            Log.d("Main", "Null getting trans_list");
        }

        if(list != null)
            list.setSelection(list.getAdapter().getCount()-1);
    }
}
