package com.villanova.edu.gladiator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TeamActivity extends Fragment implements View.OnClickListener {


    Firebase firebaseRef;
    ListView player_list;
    TextView team_name;
    TextView team_stat;
    ImageView team_icon;
    String team = "None";
    Boolean onTeam = false;
    Boolean isCaptain = false;
    Button reqJoin;
    Button newTeamButton;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View out = inflater.inflate(R.layout.activity_team, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        username=   prefs.getString("User","error");
        team = prefs.getString("Team","None");
        firebaseRef = new Firebase("https://blistering-fire-747.firebaseio.com/");
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

        player_list = (ListView) out.findViewById(R.id.team_list);
        team_name = (TextView) out.findViewById(R.id.team_name);
        team_stat = (TextView)out.findViewById(R.id.team_stat);
        team_icon = (ImageView) out.findViewById(R.id.team_icon);
        errorLayout = (LinearLayout)out.findViewById(R.id.error_holder);
        reqJoin = (Button)out.findViewById(R.id.req_button);
        reqJoin.setOnClickListener(this);
        requests = (ListView)out.findViewById(R.id.request_list);
        requestTitle = (TextView)out.findViewById(R.id.request_title);
        newTeamButton = (Button)out.findViewById(R.id.new_team_button);
        newTeamButton.setOnClickListener(this);

        setVisibleTeam(getArguments().getString("Team", "None"));

        Log.d("DEBUG",getArguments().getString("Team","None"));


        return out;

    }

    public static Fragment newInstance(String team){
        TeamActivity act = new TeamActivity();
        Bundle args = new Bundle();
        args.putString("Team", team);
        act.setArguments(args);
        return act;
    }










    public void requestJoin(){
        firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    if (shot.child("Info").child("Name").getValue().toString().contains(getArguments().getString("Team", "None"))){
                        Log.d("DEBUG","REQUESTED TEAM FOUND");
                        firebaseRef.child("Teams").child(shot.getKey()).child("Requests").child(username).child("active").setValue(true);
                    }
                }
                reqJoin.setText("Request Sent");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                reqJoin.setText("ERROR");
            }
        });

        reqJoin.setEnabled(false);
   }



    public void setVisibleTeam(final String theTeam){

//Check if the user is on the team, and check if they are captain
        //DEBUG
        firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot shotA : snapshot.getChildren()) {
                    if (shotA.child("Info").child("Name").getValue().toString().contains(theTeam)) {
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
                    firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot shotA : snapshot.getChildren()) {
                                if (shotA.child("Info").child("Name").getValue().toString().contains(theTeam)) {
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


                //HERE'S THE CODE TO GET THE IMAGES FOR THE REQEUSTING USERS
                List<Bitmap> reqImages = new ArrayList<Bitmap>();
                for (int x = 0; x < requestUsers.size(); x++) {
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    reqImages.add(largeIcon);
                }
                requests.setAdapter(new TeamRequestListAdapter(getActivity(), requestUsers, reqImages, firebaseRef, team));


                //Things to do if the team is not none
                if (!theTeam.contains("None")) {
                    //If the user isn't on the team show the request button
                    if (!onTeam) {
                        reqJoin.setVisibility(View.VISIBLE);
                    }
                    //Get Team data and write it to the activity
                    final List<String> playerNames = new ArrayList<>();
                    firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot shotA : snapshot.getChildren()) {
                                if (shotA.child("Info").child("Name").getValue().toString().contains(theTeam)) {
                                    for (DataSnapshot shot : shotA.getChildren()) {
                                        if (shot.getKey() == "Players") {
                                            //Add players to the player list
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                playerNames.add(shot2.getKey());
                                            }
                                        }
                                        //Get the team name
                                        if (shot.getKey() == "Info") {
                                            for (DataSnapshot shot2 : shot.getChildren()) {
                                                team_name.setText(shot2.getValue().toString());
                                            }
                                        }
                                        //Get the team win/loss
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
                                //HERE'S THE IMAGES TO FOR EACH PLAYER ON TEAM
                                Bitmap[] images = new Bitmap[playerNames.size()];
                                player_list.setAdapter(new TeamListAdapter(getActivity(), playerNames.toArray(new String[playerNames.size()]), images));
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError error) {
                        }
                    });
                    //HERES WHERE THE TEAM ICON IS SET
                    team_icon.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //If the team is "NONE" then only show the button to find/create team
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
    public void handleNewTeam(){
        //If you press the find team button then launch search activity
        Intent intent = new Intent(getActivity(),TeamSearchActivity.class);
        startActivityForResult(intent,0);
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.req_button){
            requestJoin();
        }else if(v.getId() == R.id.new_team_button){
            handleNewTeam();
        }
    }



}
