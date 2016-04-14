package com.villanova.edu.gladiator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Debug;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TeamActivity extends AppCompatActivity {


    Firebase firebaseRef;
    ListView player_list;
    TextView team_name;
    TextView team_stat;
    ImageView team_icon;
    String team = "None";
    Boolean onTeam = false;
    Boolean isCaptain = false;
    Button reqJoin;
    LinearLayout errorLayout;
    String username = " ";
    ListView requests;
    TextView requestTitle;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String mActivityTitle;

    static final String appPrefKey = "TheGladiatorApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        Firebase.setAndroidContext(this);
        SharedPreferences prefs = getSharedPreferences(appPrefKey, MODE_PRIVATE);
        username=   prefs.getString("User","error");
        firebaseRef = new Firebase("https://blistering-fire-747.firebaseio.com/");
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

             //Code to add a team manually
        /*
             String[] names = new String[]{"Matt","Lauren","Will","Henry","John","Eric","Kevin","Sean","Donald"};
        Bitmap[] images = new Bitmap[9];
        for(int x = 0; x< names.length;x++) {
            firebaseRef.child("Teams").child("Team1").child("Players").child(names[x]).child("Captain").setValue(false);
            firebaseRef.child("Teams").child("Team1").child("Players").child(names[x]).child("icon").setValue("ICONNN");
        }
        firebaseRef.child("Teams").child("Team1").child("Stats").child("Wins").setValue(24);
        firebaseRef.child("Teams").child("Team1").child("Stats").child("Losses").setValue(9);
        firebaseRef.child("Teams").child("Team1").child("Info").child("Name").setValue("CoolTeam#1");
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        firebaseRef.child("Teams").child("Team1").child("Icon").setValue(bm);
*/
        //This code gets the team values from firebase

        player_list = (ListView) findViewById(R.id.team_list);
        team_name = (TextView) findViewById(R.id.team_name);
        team_stat = (TextView) findViewById(R.id.team_stat);
        team_icon = (ImageView) findViewById(R.id.team_icon);
        errorLayout = (LinearLayout)findViewById(R.id.error_holder);
        reqJoin = (Button)findViewById(R.id.req_button);
        requests = (ListView)findViewById(R.id.request_list);
        requestTitle = (TextView)findViewById(R.id.request_title);
        addDrawerItems();
        setupDrawer();
        if(getIntent() != null){
            team = getIntent().getStringExtra("team");
            if(team == null){
                team = "None";
            }
        }
        //Check if the user is on the team, and check if they are captain
        //DEBUG
        firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot shotA : snapshot.getChildren()) {
                    if (shotA.child("Info").child("Name").getValue().toString().contains(team)) {
                        Log.d("DEBUG", "TEAM FOUND");
                        for (DataSnapshot shot : shotA.getChildren()) {
                            if (shot.getKey() == "Players") {
                                for (DataSnapshot shot2 : shot.getChildren()) {
                                    Log.d("DEBUG", shot2.getKey().toLowerCase());
                                    if (shot2.getKey().toLowerCase().contains(username.toLowerCase())) {
                                        onTeam = true;
                                        Log.d("DEBUUGG", "ONTEAM");
                                        if ((Boolean) shot2.child("Captain").getValue()) {
                                            isCaptain = true;
                                            Log.d("THING", "CAPTAIN LOGGED IN");
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                if (onTeam) {
                    reqJoin.setVisibility(View.GONE);
                }


                //Deal with adding captain compoents
                //If the user is the captain show the active requests to join the team.
                final List<String> requestUsers = new ArrayList<>();

                if (isCaptain) {
                    firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot shotA : snapshot.getChildren()) {
                                if (shotA.child("Info").child("Name").getValue().toString().contains(team)) {
                                    for (DataSnapshot shot : shotA.getChildren()) {
                                        if (shot.getKey() == "Requests") {
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                requestUsers.add(shot2.getKey());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError error) {
                        }
                    });
                    if (requestUsers.size() > 0) {
                        requests.setVisibility(View.VISIBLE);
                        requestTitle.setVisibility(View.VISIBLE);
                        Log.d("THING", "SET REQ TO VISIBLE");
                    }
                }

                List<Bitmap> reqImages = new ArrayList<Bitmap>();
                for (int x = 0; x < requestUsers.size(); x++) {
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    reqImages.add(largeIcon);
                }
                requests.setAdapter(new TeamRequestListAdapter(getBaseContext(), requestUsers, reqImages, firebaseRef));


                //Handle other stuff
                if (!team.contains("None")) {
                    if (!onTeam) {
                        reqJoin.setVisibility(View.VISIBLE);
                    }
                    final List<String> playerNames = new ArrayList<>();
                    firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot shotA : snapshot.getChildren()) {
                                if (shotA.child("Info").child("Name").getValue().toString().contains(team)) {
                                    for (DataSnapshot shot : shotA.getChildren()) {
                                        if (shot.getKey() == "Players") {
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                playerNames.add(shot2.getKey());
                                            }
                                        }
                                        if (shot.getKey() == "Info") {
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                team_name.setText(shot2.getValue().toString());
                                            }
                                        }
                                        if (shot.getKey() == "Stats") {
                                            String temp = " ";
                                            String wins = " ";
                                            String loss = " ";
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                if (shot2.getKey() == "Wins") {
                                                    wins = shot2.getValue().toString();
                                                }
                                                if (shot2.getKey() == "Losses") {
                                                    loss = shot2.getValue().toString();
                                                }
                                            }
                                            team_stat.setText(wins + "-" + loss);
                                        }
                                    }
                                }
                                Bitmap[] images = new Bitmap[playerNames.size()];
                                player_list.setAdapter(new TeamListAdapter(getBaseContext(), playerNames.toArray(new String[playerNames.size()]), images));
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError error) {
                        }
                    });

                    team_icon.setImageResource(R.mipmap.ic_launcher);
                } else {
                    player_list.setVisibility(View.GONE);
                    team_icon.setVisibility(View.GONE);
                    team_name.setVisibility(View.GONE);
                    team_stat.setVisibility(View.GONE);
                    player_list.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


    }







    public void handleNewTeam(View v){
        Intent intent = new Intent(this,TeamSearchActivity.class);
        startActivity(intent);
    }

    public void requestJoin(View v){

        firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot shot : dataSnapshot.getChildren()){
                    if(shot.child("Info").child("Name").getValue().toString().contains(team)){
                        firebaseRef.child("Teams").child(shot.getKey()).child("Requests").child(username).setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                reqJoin.setText("ERROR");
            }
        });
        reqJoin.setText("Request Sent");
        reqJoin.setEnabled(false);
   }

    private void addDrawerItems() {
        String[] osArray = { "News", "Schedule", "My Team", "Stats", "Settings" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        //Do nothing and close the thing
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        break;
                    case 1:
                        //Schedule acitivyt
                        break;
                    case 2:
                        //Team activity
                        Intent intent = new Intent(getBaseContext(), TeamActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                Log.d("DEBUG",getSharedPreferences(appPrefKey,0).getString("User","SHIT"));
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









}
