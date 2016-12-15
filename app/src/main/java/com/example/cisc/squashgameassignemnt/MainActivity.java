package com.example.cisc.squashgameassignemnt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    MediaPlayer music;
    private SoundPool soundpool;
    int sample1 = -1;
    int sample2 = -1;
    Animation wobble;
    TextView textScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wobble = AnimationUtils.loadAnimation(this, R.anim.wobble);

        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);
        buttonPlay.startAnimation(wobble);


        Button buttonS = (Button) findViewById(R.id.buttonS);
        buttonS.setOnClickListener(this);
        buttonS.startAnimation(wobble);


        music = MediaPlayer.create(this,R.raw.startsong);
        music.setLooping(true);
        music.start();

        soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {


            AssetManager assetsManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetsManager.openFd("start.mp3");
            sample1 = soundpool.load(descriptor, 0);

            descriptor = assetsManager.openFd("gunsound.wav");
            sample2 = soundpool.load(descriptor, 0);;

        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence text = "not found";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();


        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        music.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
                soundpool.play(sample1, 1, 1, 0, 0, 1);
                Intent m;
                m = new Intent(this, GameActivity.class);
                startActivity(m);
                break;
            case R.id.buttonS:
                soundpool.play(sample2, 1, 1, 0, 0, 1);
                Intent y;
                y = new Intent(this, Settings.class);
                startActivity(y);
                break;
        }
    }
}