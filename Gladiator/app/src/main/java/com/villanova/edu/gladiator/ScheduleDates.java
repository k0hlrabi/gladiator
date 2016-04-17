package com.villanova.edu.gladiator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/17/2016.
 */
public class ScheduleDates extends Fragment {
    LayoutInflater theInflater;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_schedule_dates, container, false);
        theInflater = inflater = LayoutInflater.from(getActivity());
        ListView list = (ListView)v.findViewById(R.id.schedule_dates_list);
        List<String[]> games = new ArrayList<>();
        games.add(new String[]{"Team A vs. Team B","4/17/15-2:00pm Pavilion"});
        games.add(new String[]{"Team C vs. Team D","4/22/15-8:00pm Field House"});
        games.add(new String[]{"Team A vs. Team D","4/23/15-6:00pm Driscoll Courts"});
        games.add(new String[]{"Team C vs. Team B","4/25/15-3:00pm Stanford Courts"});
        games.add(new String[]{"Team E vs. Team F","5/1/15-3:00pm Pavilion"});
        games.add(new String[]{"Team B vs. Team D","5/1/15-3:00pm Pavilion"});
        String[][] theGames = {{"Team A vs. Team B","4/17/15-2:00pm Pavilion"},{"Team C vs. Team D","4/22/15-8:00pm Field House"},{"Team A vs. Team D","4/23/15-6:00pm Driscoll Courts"},
                {"Team C vs. Team B","4/25/15-3:00pm Stanford Courts"}, {"Team E vs. Team F","5/1/15-3:00pm Pavilion"},{"Team B vs. Team D","5/1/15-3:00pm Pavilion"}};
        DatesAdapter adapter = new DatesAdapter(getActivity(),theGames);
        list.setAdapter(adapter);
        return v;
    }
}
