package com.villanova.edu.gladiator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER





    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View v = inflater.inflate(R.layout.fragment_news, container, false);

        ListView list = (ListView) v.findViewById(R.id.news_headline_list);
        List<String> headline = new ArrayList<>();
        headline.add("The tournament Bracket has been released!");
        headline.add("All intramural games canceled");
        headline.add("Get Ready For March!");
        headline.add("Sign Ups Closing Soon!");
        List<Integer> imgs = new ArrayList<>();

        imgs.add(R.drawable.bracket);
        imgs.add(R.drawable.church);
        imgs.add(R.drawable.dunk);
        imgs.add(R.drawable.courta);

        NewsListAdapter adapter = new NewsListAdapter(getActivity(),imgs,headline);
        list.setAdapter(adapter);

        return v;
    }



}
