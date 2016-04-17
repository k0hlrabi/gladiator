package com.villanova.edu.gladiator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by wildcat on 4/17/2016.
 */
public class DatesAdapter extends BaseAdapter {

    String[][] text;
    Context context;
    LayoutInflater inflater;
    public DatesAdapter(Context mContext, String[][] data){
        context = mContext;
        text = data;
        inflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class holder{
    TextView a;
        TextView b;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        holder h;
        if(v == null){
            h = new holder();
            v = inflater.inflate(R.layout.dates_item,null);;
            h.a = (TextView)v.findViewById(R.id.textA);
            h.b = (TextView)v.findViewById(R.id.textB);
            v.setTag(h);
        }else {
            h = (holder) convertView.getTag();;
        }

        h.a.setText(text[position][0]);
        h.b.setText(text[position][1]);

        return v;
}
}
