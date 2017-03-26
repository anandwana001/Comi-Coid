package com.krishna.jsk.comicroid.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.VideoView;

import com.krishna.jsk.comicroid.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash extends AppCompatActivity {

    private static final String endpoint = "https://gateway.marvel.com/v1/public/characters?";
    private  String url , hash;
    private  String ts;
    private long timeStamp;

    private static final long SPLASH_TIME = 12000; //13 seconds
    Handler mHandler;
    Runnable mJumpRunnable;
    VideoView videoHolder;
    Typeface typeface;

    TextView txtMessage;
    // Animation
    Animation animFadein;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtMessage = (TextView) findViewById(R.id.txtMessage);
        typeface = Typeface.createFromAsset(getAssets(),"avenger.ttf");
        txtMessage.setTypeface(typeface);


        Long tsLong = System.currentTimeMillis()/1000;
        ts = tsLong.toString();
        final String toHash = ts + "179e29e3f6447cedbb508940761ff0abc35d1884" + "b136328c81adf02eeb8cbfce3e0ef2a7";
        url = "" + "b136328c81adf02eeb8cbfce3e0ef2a7";
        hash = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashe = md.digest(toHash.getBytes("UTF-8"));
            StringBuffer hex = new StringBuffer(2*hashe.length);
            for (byte b : hashe) {
                hex.append(String.format("%02x", b&0xff));
            }
            hash = hex.toString();
        }
        catch(NoSuchAlgorithmException e) {
        }
        catch(UnsupportedEncodingException e) {
        }


        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        // start the animation
        txtMessage.startAnimation(animFadein);
        mJumpRunnable = new Runnable() {

            public void run() {
                jump();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mJumpRunnable, SPLASH_TIME);
        try{
             videoHolder =(VideoView) findViewById(R.id.video);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.splash);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    jump();
                }
            });
            videoHolder.start();
        } catch(Exception ex) {
            jump();
        }
    }

    private void jump() {
        if(isFinishing())
            return;
        startActivity(new Intent(this, Main2Activity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
