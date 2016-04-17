package com.villanova.edu.gladiator;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

/**
 * Created by wildcat on 4/9/2016.
 */
public class TeamRequestListAdapter extends BaseAdapter implements View.OnClickListener{
    List<String> users;
    List<Bitmap> images;
    Firebase firebase;
    Context context;
    LayoutInflater inflater;
    String team;
    public TeamRequestListAdapter(Context mContext, List<String> Users,List<Bitmap> mImages, Firebase fireContext,String teamName){
        context = mContext;
        users = Users;
        firebase = fireContext;
        images = mImages;
        inflater = LayoutInflater.from(mContext);
        team = teamName;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.positive:
                allowRequest((int)v.getTag());
                break;
            case R.id.negative:
                denyRequest((int)v.getTag());
                break;
        }
    }

    public class holder{
        TextView tv;
        ImageView iv;
        Button posButton;
        Button negButton;
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
            h.posButton = (Button)v.findViewById(R.id.positive);
            h.posButton.setOnClickListener(this);
            h.posButton.setTag(position);
            h.negButton = (Button)v.findViewById(R.id.negative);
            h.negButton.setOnClickListener(this);
            h.negButton.setTag(position);

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

        public void denyRequest(final int position) {
            firebase.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot a : dataSnapshot.getChildren()) {
                        if (a.child("Info").child("Name").getValue().toString().contains(team)) {
                            firebase.child("Teams").child(a.getKey()).child("Requests").child(users.get(position)).removeValue();
                        }
                    }
                    users.remove(position);
                    //  images.remove(position);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });




        }
    public void allowRequest(final int position){
        firebase.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    if (a.child("Info").child("Name").getValue().toString().contains(team)) {
                        firebase.child("Teams").child(a.getKey()).child("Requests").child(users.get(position)).removeValue();
                        firebase.child("Teams").child(a.getKey()).child("Players").child(users.get(position)).child("Captain").setValue(false);
                    }
                }
                // images.remove(position);

                users.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }


}
