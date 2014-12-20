package com.janus.jbudget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fileName;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        fileName = sharedPref.getString(getString(R.string.saved_file_name), "");

        JBudget.init();

        if(!fileName.isEmpty()) {
            JBudget.get().open(fileName);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0: {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SummaryFragment.newInstance(position + 1))
                        .commit();
            }
                break;
            case 1: {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TransactionFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            case 2: {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, CategoryFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            case 3:
            {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FilesFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            //create a empty fragment if something goes wrong or not implemented
            default:
            {

            }
            break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_summary);
                break;
            case 2:
                mTitle = getString(R.string.title_trans);
                break;
            case 3:
                mTitle = getString(R.string.title_category);
                break;
            case 4:
                mTitle = getString(R.string.title_files);
                break;
        }
    }

    public void showSummary()
    {
        ActionBar actionBar = getSupportActionBar();
        mTitle = getString(R.string.title_summary);
        actionBar.setTitle(mTitle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, SummaryFragment.newInstance(1))
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void onStop() {
        //save the budget
        String fileName = JBudget.get().save();

        if(fileName != null) {

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.saved_file_name), fileName);
            editor.commit();
        }

        super.onStop();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void showAddTransaction(View view) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }
}
