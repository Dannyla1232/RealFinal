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
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class GameOverActivity extends Activity implements View.OnClickListener {

    MediaPlayer music;
    private SoundPool soundpool;
    int sample1 = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over1);

        music = MediaPlayer.create(this,R.raw.sad);
        music.setLooping(true);
        music.start();

        Button buttonRetry = (Button) findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(this);

        Button buttonTitle = (Button) findViewById((R.id.buttonTitle));
        buttonTitle.setOnClickListener(this);

        soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {


            AssetManager assetsManager = getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetsManager.openFd("gunsound.wav");
            sample1 = soundpool.load(descriptor, 0);
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
            case R.id.buttonRetry:
                soundpool.play(sample1, 1, 1, 0, 0, 1);
                Intent m;
                m = new Intent(this, GameActivity.class);
                startActivity(m);
                break;
            case R.id.buttonTitle:
                soundpool.play(sample1, 1, 1, 0, 0, 1);
                Intent y;
                y = new Intent(this, MainActivity.class);
                startActivity(y);
                break;
        }
    }
}
