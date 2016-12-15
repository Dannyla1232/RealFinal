package com.example.cisc.squashgameassignemnt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

public class GameActivity extends Activity {

    MediaPlayer music;
    Canvas canvas;
    SquashCourtView squashCourtView;

    //Sound
    //initialize sound variables
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    //Used for getting display details like the number of pixels
    Display display;
    Point size;
    int screenWidth;
    int screenHeight;
    int halfScreenHeight;
    int halfScreenWidth;

    //!!Candidates for class!!!
    //Game objects
    int racketWidth, racket2Width;
    int racketHeight, racket2Height;
    Point racketPosition, racket2Position;
    int halfWidth, halfHeight, half2Width, half2Height;

    Point ballPosition;
    int ballWidth;

    //for ball movement
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    //for racket movement
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    boolean racket2IsMovingLeft;
    boolean racket2IsMovingRight;

    //stats
    long lastFrameTime;
    int fps;
    int score;
    int lives;
    int lives2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        squashCourtView = new SquashCourtView(this);
        setContentView(squashCourtView);


        //backgroundMusic
        music = MediaPlayer.create(this,R.raw.christmas);
        music.setLooping(true);
        music.start();

        //Sound code
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            //Create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor, 0);


            descriptor = assetManager.openFd("paddlehit.wav");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor, 0);


        } catch (IOException e) {
            //catch exceptions here
        }


        //Get the screen size in pixels
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        halfScreenHeight = screenHeight / 2;
        halfScreenWidth = screenWidth / 2;


        //The game objects
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth / 8;
        racketHeight = 10;
        halfWidth = screenWidth/8;
        halfHeight = screenHeight/100;

        racket2Position = new Point();
        racket2Position.x = screenWidth / 2;
        racket2Position.y = +50;
        racket2Width = screenWidth / 8;
        racket2Height = 10;
        half2Width = screenWidth/8;
        half2Height = screenHeight/100;

        ballWidth = screenWidth / 35;
        ballPosition = new Point();
        ballPosition.x = screenWidth / 2;
        ballPosition.y = screenHeight / 2;



        lives = 1;
        lives2 = 1;

    }

    class SquashCourtView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash;
        Paint paint;

        public SquashCourtView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            ballIsMovingDown = true;

            Random randomNumber = new Random();
            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection) {
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;

                case 1:
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                    break;

                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }


        }

        @Override
        public void run() {
            while (playingSquash) {
                updateCourt();
                drawCourt();
                controlFPS();

            }

        }


        public void updateCourt() {
            if (racketIsMovingRight && racketPosition.x + halfWidth < screenWidth)
                racketPosition.x = racketPosition.x + 20;

            if (racketIsMovingLeft && racketPosition.x - halfWidth > 0 )
                racketPosition.x = racketPosition.x - 20;

            if (racket2IsMovingRight && racket2Position.x + half2Width < screenWidth)
                racket2Position.x = racket2Position.x + 20;

            if (racket2IsMovingLeft && racket2Position.x - half2Width > 0)
                racket2Position.x = racket2Position.x - 20;


            //detect collisions

            //hit right of screen
            if (ballPosition.x + ballWidth > screenWidth) {
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            //hit left of screen
            if (ballPosition.x < 0) {
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            //Edge of ball has hit bottom of screen
            if (ballPosition.y > screenHeight - ballWidth || ballPosition.y < 0) {

                if (ballPosition.y < 0)
                {
                    lives2 = lives2 - 1;
                    if (lives2 == 0)
                    {
                        Intent i;
                        i = new Intent (GameActivity.this,WinnerActivity.class);
                                startActivity(i);
                    }
                }

                if (ballPosition.y > screenHeight - ballWidth) {
                    lives = lives - 1;
                    if (lives == 0) {
                        Intent i;
                        i = new Intent(GameActivity.this, GameOverActivity.class);
                        startActivity(i);
                    }
                }

                ballPosition.y = screenHeight / 2;//back to top of screen


                //what horizontal direction should we use
                //for the next falling ball
                Random randomNumber = new Random();
                int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;

                int ballDirection = randomNumber.nextInt(3);
                switch (ballDirection) {
                    case 0:
                        ballIsMovingLeft = true;
                        ballIsMovingRight = false;
                        break;

                    case 1:
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                        break;

                    case 2:
                        ballIsMovingLeft = false;
                        ballIsMovingRight = false;
                        break;
                }
            }


            //depending upon the two directions we should be
            //moving in adjust our x any positions
            if (ballIsMovingDown) {
                ballPosition.y += 6;
            }

            if (ballIsMovingUp) {
                ballPosition.y -= 10;
            }

            if (ballIsMovingLeft) {
                ballPosition.x -= 12;
            }

            if (ballIsMovingRight) {
                ballPosition.x += 12;
            }

            //Has ball hit racket
            if (ballPosition.y + ballWidth >= (racketPosition.y - racketHeight / 2)) {
                int halfRacket = racketWidth / 2;
                if (ballPosition.x + ballWidth > (racketPosition.x - halfRacket)
                        && ballPosition.x - ballWidth < (racketPosition.x + halfRacket)) {
                    //rebound the ball and play a sound
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    //now decide how to rebound the ball
                    if (ballPosition.x > racketPosition.x) {
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;

                    } else {
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }

                }
            }
//COLLSION racket #2
            if (ballPosition.y - ballWidth <= (racket2Position.y + racket2Height / 2)) {
                int halfRacket2 = racket2Width / 2;
                if (ballPosition.x + ballWidth > (racket2Position.x - halfRacket2)
                        && ballPosition.x - ballWidth < (racket2Position.x + halfRacket2)) {
                    //rebound the ball and play a sound
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    score++;
                    ballIsMovingUp = false;
                    ballIsMovingDown = true;
                    //now decide how to rebound the ball
                    if (ballPosition.x > racket2Position.x) {
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;

                    } else {
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }

                }
            }

        }


        public void drawCourt() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);//the background
                paint.setColor(Color.argb(255, 51, 255, 255));
                paint.setTextSize(45);
                canvas.drawText("Score:" + score + " Lives:" + lives + " fps:" + fps, 20, 1000, paint);


                //Draw the squash racket
                canvas.drawRect(racketPosition.x - (racketWidth / 2),
                        racketPosition.y - (racketHeight / 2), racketPosition.x + (racketWidth / 2),
                        racketPosition.y + racketHeight, paint);

                canvas.drawRect(racket2Position.x - (racket2Width / 2),
                        racket2Position.y - (racket2Height / 2), racket2Position.x + (racket2Width / 2),
                        racket2Position.y + racket2Height, paint);

                //Draw the ball
                canvas.drawRect(ballPosition.x, ballPosition.y,
                        ballPosition.x + ballWidth, ballPosition.y + ballWidth, paint);


                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / timeThisFrame);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }

            }

            lastFrameTime = System.currentTimeMillis();
        }


        public void pause() {
            playingSquash = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }

        }

        public void resume() {
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        }


        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    if (motionEvent.getY() > halfScreenHeight)
                    {
                        if (motionEvent.getX() > halfScreenWidth) {
                            racketIsMovingRight = true;
                            racketIsMovingLeft = false;
                        } else {
                            racketIsMovingLeft = true;
                            racketIsMovingRight = false;
                        }

                    } else {
                        if (motionEvent.getX() >= halfScreenWidth) {
                            racket2IsMovingRight= true;
                            racket2IsMovingLeft = false;
                        } else {
                            racket2IsMovingLeft = true;
                            racket2IsMovingRight = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    racketIsMovingRight = racketIsMovingLeft = racket2IsMovingLeft = racket2IsMovingRight = false;
                    break;
            }
            return true;

        }//On touch
    }//Squash runnable

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            squashCourtView.pause();
            break;
        }

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        squashCourtView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        music.release();
        squashCourtView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }


}
