package com.villanova.edu.gladiator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wildcat on 4/17/2016.
 */
public class ScheduleBracket extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout        for this fragment

      //  View v = inflater.inflate(R.layout.fragment_schedule_bracket, container, false);

        Firebase.setAndroidContext(getActivity());

        Firebase ref = new Firebase("https://blistering-fire-747.firebaseio.com/");
        BracketView v = new BracketView(getActivity(),ref);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.topMargin = 210;
        RelativeLayout layout = new RelativeLayout(getActivity());
        layout.addView(v,params);
        return  layout;
    }
}

 class BracketView extends SurfaceView implements SurfaceHolder.Callback{

    List<String> teams;
     List<Rect> textRects = new ArrayList<>();
     Context context;
     Paint p;
     Bitmap bracketImage;
     Firebase ref;
     int cameraOffsetX = 0;
     int cameraOffsetY = 0;
    public  BracketView(Context mContext, Firebase inputRef){
        super(mContext);
        context = mContext;
        teams = new ArrayList<>();
         p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(100);
        bracketImage = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.truebracket);
        ref = inputRef;
        setWillNotDraw(false);

        getTeams();

    }


     public void generateRects() {
         int xOffset = 0;
         int yOffset = 155;
         int defSpacing = 125;
         int bigSpacing = 123;
         int ySpacing = bigSpacing;
         boolean a = false;
         Rect aTextRect =new Rect();
         p.getTextBounds("TeamA", 0, "TeamA".length() - 1, aTextRect);
         int ypos = yOffset;
         for (int x = 0; x < teams.size(); x++) {
             if (x < 8) {
                xOffset = 0;
                 a = true;
             }else if(x< 12){
                if(a){
                    xOffset = 750;
                    yOffset = 150 + 125;
                    bigSpacing = 300;
                    defSpacing = 300;
                    ypos = yOffset;
                    a = false;
                }
             }else if(x<14){
                 if(!a){
                     xOffset = 1200;
                     yOffset = 155 + 125*2;
                     bigSpacing = 750;
                     defSpacing = 750;
                     ypos = yOffset;
                     a = true;
                 }
             }else{
                 xOffset = 1700;
                 yOffset = 155 + 125*11/2;
                 ypos = yOffset;
             }
             Rect textRect = new Rect();
             textRect.top = ypos;
             textRect.left = xOffset;
             textRects.add(textRect);
             if(x%2 == 0){
                 ySpacing = defSpacing;
             }else{
                 ySpacing = bigSpacing;
             }
             ypos += (aTextRect.height() + ySpacing);
         }
     }

     private VelocityTracker mVelocityTracker = null;
     @Override
     public boolean onTouchEvent(MotionEvent event) {
         int index = event.getActionIndex();
         int action = event.getActionMasked();
         int pointerId = event.getPointerId(index);

         switch(action) {
             case MotionEvent.ACTION_DOWN:
                 if(mVelocityTracker == null) {
                     // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                     mVelocityTracker = VelocityTracker.obtain();
                 }
                 else {
                     // Reset the velocity tracker back to its initial state.
                     mVelocityTracker.clear();
                 }
                 // Add a user's movement to the tracker.
                 mVelocityTracker.addMovement(event);
                 break;
             case MotionEvent.ACTION_MOVE:
                 mVelocityTracker.addMovement(event);
                 // When you want to determine the velocity, call
                 // computeCurrentVelocity(). Then call getXVelocity()
                 // and getYVelocity() to retrieve the velocity for each pointer ID.
                 mVelocityTracker.computeCurrentVelocity(1000);
                 // Log velocity of pixels per second
                 // Best practice to use VelocityTrackerCompat where possible.
                 Log.d("", "X velocity: " +
                         VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                 pointerId));
                 Log.d("", "Y velocity: " +
                         VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                 pointerId));

                 cameraOffsetX += (int)VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                         pointerId)/100;
                 cameraOffsetY += (int)VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                         pointerId)/100;
                 break;
             case MotionEvent.ACTION_UP:
             case MotionEvent.ACTION_CANCEL:
                 // Return a VelocityTracker object back to be re-used by others.
                 mVelocityTracker.recycle();
                 break;
         }

         postInvalidate();
         return true;
     }



     public void getTeams(){

         ref.child("Bracket").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot sshot: dataSnapshot.getChildren()){
                     teams.add((String) sshot.getValue());
                 }
                 Log.d("CANVAS","Got " + teams.size());
                 generateRects();
                 postInvalidate();


             }
             @Override
             public void onCancelled(FirebaseError firebaseError) {

             }
         });



     }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

     @Override
     public void onDraw(Canvas canvas){
         Log.d("CANVAS","DRAWING");
         canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bracketImage, cameraOffsetX, cameraOffsetY, new Paint(Color.WHITE));
         for(int x = 0; x< teams.size(); x++) {
             canvas.drawText(teams.get(x),textRects.get(x).left + cameraOffsetX,textRects.get(x).top+ cameraOffsetY,p);
         }
     }
}
