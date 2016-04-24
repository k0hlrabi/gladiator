package com.villanova.edu.gladiator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/20/2016.
 */
public class StatsFragment extends Fragment {


    final List<String> names = new ArrayList<>();
    final List<Integer> wins = new ArrayList<>();
    final List<Integer> Losses = new ArrayList<>();
     ListView statsList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View out = inflater.inflate(R.layout.fragment_stats, container, false);
        statsList = (ListView)out.findViewById(R.id.stats_list);

        Spinner menu = (Spinner)out.findViewById(R.id.stats_list_type);
        String[] selections = new String[]{"Wins","Losses"};
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,selections);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapt);
        menu.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        setupListView(statsList,names,wins,Losses,true);
                        break;
                    case 1:
                        setupListView(statsList,names,wins,Losses,false);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));
        Firebase.setAndroidContext(getActivity());
        Firebase ref = new Firebase("https://blistering-fire-747.firebaseio.com/");

        ref.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    if(s.child("Info").child("Name").getValue() != null){
                        names.add((String)s.child("Info").child("Name").getValue());
                    }else{
                        names.add("Error");
                    }
                    if(s.child("Stats").child("Losses").getValue() != null){
                        Losses.add(((Long)s.child("Stats").child("Losses").getValue()).intValue());
                    }else{
                        Losses.add(0);
                    }
                    if(s.child("Stats").child("Wins").getValue() != null){
                        wins.add(((Long)s.child("Stats").child("Wins").getValue()).intValue());
                    }else{
                        wins.add(0);
                    }

                    setupListView(statsList,names,wins,Losses,true);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return out;
    }


    public void setupListView(ListView a,List<String> mNames, List<Integer> wins, List<Integer> aLosses, boolean sortByWins){
        if(sortByWins){
        for(int x = 0; x< mNames.size();x++) {
            for (int y = x; y < mNames.size(); y++) {
                    if(wins.get(x) < wins.get(y)){
                        String nameA = mNames.get(x);
                        String nameB = mNames.get(y);
                        mNames.set(x,nameB);
                        mNames.set(y,nameA);
                        int winsA = wins.get(x);
                        int winsB = wins.get(y);
                        wins.set(x,winsB);
                        wins.set(y,winsA);
                        int lossA = aLosses.get(x);
                        int lossB = aLosses.get(y);
                        aLosses.set(x,lossB);
                        aLosses.set(y,lossA);
                    }
            }
        }
        }else{
            for(int x = 0; x< mNames.size();x++) {
                for (int y = x; y < mNames.size(); y++) {
                    if(aLosses.get(x) < aLosses.get(y)){
                        String nameA = mNames.get(x);
                        String nameB = mNames.get(y);
                        mNames.set(x,nameB);
                        mNames.set(y,nameA);
                        int winsA = wins.get(x);
                        int winsB = wins.get(y);
                        wins.set(x,winsB);
                        wins.set(y,winsA);
                        int lossA = aLosses.get(x);
                        int lossB = aLosses.get(y);
                        aLosses.set(x,lossB);
                        aLosses.set(y,lossA);
                    }
                }
            }
        }


        List<String[]> out = new ArrayList<>();
        for(int x = 0 ;x<mNames.size(); x++){
            String name = mNames.get(x);
            String bottom = wins.get(x) + "-" + aLosses.get(x);
            out.add(new String[]{name,bottom});
        }
        DatesAdapter adapter = new DatesAdapter(getActivity(),out.toArray(new String[out.size()][2]));
        a.setAdapter(adapter);
    }
}
