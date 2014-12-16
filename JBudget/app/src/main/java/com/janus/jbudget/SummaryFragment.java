package com.janus.jbudget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.janus.jbudget.R;

public class SummaryFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

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
    public SummaryFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this is part of index 0 in the MainActivity, let MainActivity know this was attached
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void onResume() {
        super.onResume();

        TextView name = (TextView) getView().findViewById(R.id.budget_name);
        String nameString = JBudget.get().getName();

        if( nameString.lastIndexOf('.') > 0)
            nameString = nameString.substring(0, nameString.lastIndexOf('.'));

        name.setText(nameString);

    }
}
