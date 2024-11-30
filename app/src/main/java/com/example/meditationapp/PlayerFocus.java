package com.example.meditationapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;

public class PlayerFocus extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ImageButton btn_back, btn_play;
    SeekBar seekBar;
    Timer timer;
    boolean isPlaying = true;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_focus);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_back = findViewById(R.id.btn_back);
        btn_play = findViewById(R.id.btn_play);
        seekBar = findViewById(R.id.seekbar);

        mediaPlayer = MediaPlayer.create(this, R.raw.sleep);
        if (mediaPlayer == null) {
            throw new RuntimeException("MediaPlayer failed to load the file.");
        }
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        updateSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBarTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.post(updateSeekBarTask);
            }
        });

        btn_play.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btn_play.setImageResource(R.drawable.pausebtn);
                Log.d("PlayerSleep", "Paused");
            } else {
                mediaPlayer.start();
                Log.d("PlayerSleep", "Playing");
                btn_play.setImageResource(R.drawable.playbtn);
            }
        });


        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerFocus.this, Menu.class);
            startActivity(intent);
            mediaPlayer.stop();
            finish();
        });
    }

    private void updateSeekBar() {
        handler.post(updateSeekBarTask);
    }

    private final Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBarTask);
    }
}
