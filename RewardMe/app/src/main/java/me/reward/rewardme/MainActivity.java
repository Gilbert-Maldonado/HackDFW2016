package me.reward.rewardme;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

import me.reward.rewardme.DataObjects.UserInfo;
import me.reward.rewardme.dummy.DummyContent;

public class MainActivity extends Activity
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
        if (UserInfo.getCurrentUser() == null) {
            Toast.makeText(this, "System Error, please login or sign up again", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            PebbleDictionary dict = new PebbleDictionary();
            final int AppKeyMoney = 0;

		/*At this point, get the integer money from the server


		*/
            int money = 55 ;
            String input = Integer.toString(money);

            dict.addString(AppKeyMoney, input);
            final UUID appUuid = UUID.fromString("28aeeea7-ca68-4468-b3ed-28ff9e06be04");
            PebbleKit.sendDataToPebble(getApplicationContext(), appUuid, dict);

//            Intent intent = new Intent(this, NewHabit.class);
//            startActivity(intent);
//            return true;
            Toast.makeText(this, "SENT MONEY INFO", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
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

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        private int sectionNum;
        private FloatingActionButton newTask;
        private final int[] layout_view = {R.layout.fragment_main, R.layout.fragment_payments};

        public static PlaceholderFragment newInstance(int sectionNumber) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            Log.d("SECTIONNUM", Integer.toString(sectionNumber) + " section number");
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            sectionNum = args.getInt(ARG_SECTION_NUMBER) - 1;
            View rootView = inflater.inflate(layout_view[sectionNum], container, false);
            if(sectionNum == 0) {
                newTask = (FloatingActionButton) rootView.findViewById(R.id.fab);
                newTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), NewHabit.class);
                        startActivity(intent);
                    }
                });

                ListView habits = (ListView) rootView.findViewById(R.id.habits);
                habits.setAdapter(new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.fragment_habit_detail,
                        R.id.habit_detail,
                        UserInfo.getCurrentHabitsString()));
                habits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserInfo.removeHabit(position);
                        parent.notify();
                    }
                });
            }
            // make the payments API STUFF.
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }


}
