package com.villanova.edu.gladiator;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    final List<String> teamList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_search);
        searchBox = (EditText)findViewById(R.id.team_search_box);
        searchResults = (ListView)findViewById(R.id.search_results);
        firebaseRef = new Firebase("https://blistering-fire-747.firebaseio.com/");
        firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot shot: dataSnapshot.getChildren()){
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
                firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
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
                Intent i = new Intent(getBaseContext(),TeamActivity.class);
                i.putExtra("team",teamList.get(position));
                i.putExtra("onTeam",false);
                startActivity(i);
            }
        });
    }

    public void onButtonClick(View v){
        //Open create new team activity
    }


    public void onResume(){
        super.onResume();

        teamList.clear();
        firebaseRef.child("Teams").addValueEventListener(new ValueEventListener() {
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
    }



}
