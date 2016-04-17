package com.villanova.edu.gladiator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/16/2016.
 */
public class ScheduleFragment extends Fragment {
    FragmentTabHost mTabHost;



    public ScheduleFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        mTabHost = (FragmentTabHost)v.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.schedule_frame_layout);
        mTabHost.addTab(mTabHost.newTabSpec("dates").setIndicator("Dates"),
                ScheduleDates.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("bracket").setIndicator("Bracket"),
                ScheduleBracket.class, null);

        return v;
    }

}
