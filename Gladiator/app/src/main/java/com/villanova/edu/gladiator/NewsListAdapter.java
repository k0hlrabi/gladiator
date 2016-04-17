package com.villanova.edu.gladiator;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/16/2016.
 */
public class NewsListAdapter extends BaseAdapter {
    LayoutInflater inflater;

    List<Integer> images = new ArrayList<>();
    List<String> headlines = new ArrayList<>();


    public NewsListAdapter(Context mContext,List<Integer> imgs, List<String> text){
        inflater = LayoutInflater.from(mContext);
        images = imgs;
        headlines = text;

    }
    @Override
    public int getCount() {
        return headlines.size();
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
        ImageView iv;
        TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        holder h;
        if(v == null) {
            h = new holder();
            v = inflater.inflate(R.layout.news_item, null);
            h.iv = (ImageView)v.findViewById(R.id.news_image);
            h.tv = (TextView)v.findViewById(R.id.news_headline);
            v.setTag(h);
        }else{
            h = (holder) convertView.getTag();
        }
       h.iv.setImageResource(images.get(position));
        h.tv.setText(headlines.get(position));
        return v;
    }




}
