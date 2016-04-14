package com.villanova.edu.gladiator;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by wildcat on 4/9/2016.
 */
public class TeamRequestListAdapter extends BaseAdapter {
    List<String> users;
    List<Bitmap> images;
    Firebase firebase;
    Context context;
    LayoutInflater inflater;
    public TeamRequestListAdapter(Context mContext, List<String> Users,List<Bitmap> mImages, Firebase fireContext){
        context = mContext;
        users = Users;
        firebase = fireContext;
        images = mImages;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return users.size();
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
            v = inflater.inflate(R.layout.team_request_item, null);
            h = new holder();
            h.tv = (TextView)v.findViewById(R.id.req_user);
            h.iv = (ImageView)v.findViewById(R.id.req_player_icon);
            v.setTag(h);
        }else {
            h = (holder) convertView.getTag();;
        }
        if(users.get(position) != null){
            h.tv.setText(users.get(position));
        }else{
            h.tv.setText("ERROR");
        }
            h.iv.setImageResource(R.mipmap.ic_launcher);
        return v;
    }
}
