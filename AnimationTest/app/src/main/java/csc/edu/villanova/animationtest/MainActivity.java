package csc.edu.villanova.animationtest;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView theTxt;
    boolean visible = true;
    Animation slideIn;
    Animation slideOut;
    float originalXPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        theTxt = (TextView)findViewById(R.id.coolText);
        originalXPosition = theTxt.getX();
        slideOut = AnimationUtils.loadAnimation(this, R.anim.out_slide_right);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.in_slide_left);
        slideIn.setFillAfter(false);

        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                theTxt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateText(View v) {
        if (!visible) {

            theTxt.setX(-theTxt.getWidth());
            theTxt.startAnimation(slideIn);


        }else{
            theTxt.startAnimation(slideOut);
        }
        visible = !visible;
    }
}
