package com.villanova.edu.gladiator;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeamSearchActivity extends AppCompatActivity {
    EditText searchBox;
    ListView searchResults;
    Firebase firebaseRef;
    String output_team = "None";

    final List<String> teamList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_search);
        searchBox = (EditText)findViewById(R.id.team_search_box);
        searchResults = (ListView)findViewById(R.id.search_results);
        firebaseRef = new Firebase("https://blistering-fire-747.firebaseio.com/");
        firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    teamList.add(shot.child("Info").child("Name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        searchResults.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, teamList));

        teamList.clear();



        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                teamList.clear();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                teamList.clear();
                firebaseRef.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot shot : dataSnapshot.getChildren()) {
                            if (shot.child("Info").child("Name").getValue().toString().toLowerCase().contains(searchBox.getText().toString().toLowerCase())) {
                                teamList.add(shot.child("Info").child("Name").getValue().toString());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                searchResults.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,teamList));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                output_team = ((TextView)view).getText().toString();
                setTeam();
            }
        });
    }

    public void onButtonClick(View v){
        //Open create new team activity

        final Dialog a = new Dialog(this);
        a.setContentView(R.layout.new_team_dialog);
        final EditText nameInput = (EditText)a.findViewById(R.id.new_team_name);
        nameInput.setText(searchBox.getText().toString());
        Button positive = (Button)a.findViewById(R.id.new_team_positive);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            makeTeam(nameInput.getText().toString());

            }
        });
        Button negative = (Button)a.findViewById(R.id.new_team_negative);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.dismiss();
            }
        });

    a.setTitle("Create New Team");
    a.show();


    }

    public void makeTeam(String teamName){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         String username = prefs.getString("User", "error");
        firebaseRef.child("Teams").child(teamName).child("Players").child(username).child("Captain").setValue(true);
        firebaseRef.child("Teams").child(teamName).child("Info").child("Name").setValue(teamName);
        firebaseRef.child("Teams").child(teamName).child("Stats").child("Wins").setValue(0);
        firebaseRef.child("Teams").child(teamName).child("Stats").child("Losses").setValue(0);
        output_team = teamName;
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("Team",teamName);
        edit.apply();
        setTeam();
    }


    public void onResume(){
        super.onResume();

    }

    public void setTeam(){
        Intent i = this.getIntent();
        i.putExtra("OUTPUT_TEAM",output_team);
        setResult(1,i);
        finish();

    }


}
