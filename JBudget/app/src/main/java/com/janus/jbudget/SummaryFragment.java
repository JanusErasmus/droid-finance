package com.janus.jbudget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class SummaryFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private JCategoryListAdapter mAdapter;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SummaryFragment newInstance(int sectionNumber) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryFragment() {
        mAdapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        TextView name = (TextView) view.findViewById(R.id.budget_name);
        String nameString = JBudget.get().name;

        if( nameString.lastIndexOf('.') > 0)
            nameString = nameString.substring(0, nameString.lastIndexOf('.'));

        name.setText(nameString);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.expCategory);

        mAdapter = new JCategoryListAdapter(
                getActivity(),
                JBudget.get().categoryBalance
        );
        listView.setAdapter(mAdapter);

        setBankBalance(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this is part of index 0 in the MainActivity, let MainActivity know this was attached
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void onResume() {
        super.onResume();

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();

            setBankBalance(getView());
        }
    }

    private void setBankBalance(View view) {

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String balanceString = formatter.format(JBudget.get().getBalance());

        TextView name = (TextView) view.findViewById(R.id.bank_balance);
        name.setText(getString(R.string.balance_summary) + " " + balanceString);
    }
}
