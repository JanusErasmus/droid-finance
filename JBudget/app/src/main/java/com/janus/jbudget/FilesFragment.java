package com.janus.jbudget;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FilesFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private MainActivity mMainActivity;
    private File files[];

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FilesFragment newInstance(int sectionNumber) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public FilesFragment()
    {
        mMainActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;

        //this is part of index in the MainActivity, let MainActivity know this was attached
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }


    public void onResume()
    {
        super.onResume();

        File budgetDirectory = new File(Environment.getExternalStoragePublicDirectory("Documents"), "jFinance");
        if(budgetDirectory.exists())
        {
            Log.d("Main", "Directory exists");
        }
        else
        {
            if (!budgetDirectory.mkdirs())
                Log.d("Main", "Directory not created");
            else
                Log.d("Main", "Directory created");
        }

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".jbud")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        files = budgetDirectory.listFiles(textFilter);

        List<String> fileNames = new ArrayList<String>();
        //remove path
        for(File file : files)//for(int k =0; k < files.length; k++)
        {
            fileNames.add(file.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                fileNames
        );


        ListView list = (ListView) getView().findViewById(R.id.file_list);
        list.setAdapter(adapter);
        list.setSelection(list.getAdapter().getCount()-1);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               JBudget.get().open(files[position].toString());

                //check if file was opened
                if(true && (mMainActivity != null))
                {
                    mMainActivity.showSummary();
                }
            }
        });

    }
}
