package com.villanova.edu.gladiator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import android.view.View;


import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wildcat on 4/13/2016.
 */
public class MainActivity extends AppCompatActivity {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    FrameLayout fragmentHolder;
    String username;
    String usersTeam = "None";
    private static final String prefsKey = "TheGladiatorApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        fragmentHolder = (FrameLayout)findViewById(R.id.fragment_holder);

        Firebase myFirebaseRef = new Firebase("https://blistering-fire-747.firebaseio.com/");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());// 0 - for private mode
        username=   prefs.getString("User","error");
        Log.d("LOGIN DEBUG",username + " Try 2");
        if(username.contains("error")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        username  = prefs.getString("User","error");
        usersTeam = prefs.getString("Team","None");
        if(usersTeam.trim().isEmpty()){
            usersTeam = "None";
            SharedPreferences.Editor edit = prefs.edit();
          edit.putString("Team","None");
            edit.commit();
        }

        // Create ArrayAdapter using the planet list.



        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectItem(0);
    }

    private void addDrawerItems() {
        String[] osArray = { "News", "Schedule", "My Team", "Stats", "Settings" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }


    public void selectItem(int position){

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new NewsFragment();

        switch(position){
            case 0:
                fragment = new NewsFragment();
                break;
            case 1:
                //Schedule acitivyt
                fragment = new ScheduleFragment();
                break;
            case 2:
                //Team activity
                fragment = TeamActivity.newInstance(usersTeam);
                break;
            case 3:
                //Leauge Stats
                break;
            case 4:
                //Settings
                fragment = new SettingsFragment();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .commit();
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                Log.d("DEBUG", PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("User", "SHIT"));
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






    //THis is only used for the teams so we can always assume the user wants to be on the team page.
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String teamOut ="None";
        if(data != null){
             teamOut = data.getStringExtra("OUTPUT_TEAM");
        }
       Fragment fragment = TeamActivity.newInstance(teamOut);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .commitAllowingStateLoss();


    }
}
