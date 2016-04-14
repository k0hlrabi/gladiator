package com.villanova.edu.gladiator;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by wildcat on 4/9/2016.
 */
public class TeamListAdapter extends BaseAdapter {
    String[] teamNames;
    Context context;
    Bitmap[] teamImages;
     LayoutInflater inflater=null;

    public TeamListAdapter(Context mContext, String[] names,Bitmap[] images){
        context = mContext;
        teamImages = images;
        teamNames = names;
        inflater = LayoutInflater.from(mContext);
    }



    @Override
    public int getCount() {
        return teamNames.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class holder{
        TextView tv;
        ImageView iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        holder h;
        if(v == null){
            v = inflater.inflate(R.layout.team_list_item, null);
            h = new holder();
            h.tv = (TextView)v.findViewById(R.id.player_name);
            h.iv = (ImageView)v.findViewById(R.id.player_icon);
            v.setTag(h);
        }else {
             h = (holder) convertView.getTag();;
        }
        if(teamNames[position] != null){
            h.tv.setText(teamNames[position]);

        }else{
            h.tv.setText("ERROR");
        }

        if(teamImages[position]!= null) {
            h.iv.setImageBitmap(teamImages[position]);
        }else {
            h.iv.setImageResource(R.mipmap.ic_launcher);
        }
        return v;
    }
}
