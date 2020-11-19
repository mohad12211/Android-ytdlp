package com.downloader.my;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;

public class AudioPlayer extends DialogFragment implements View.OnClickListener {
    String name;
    TextView textView;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ImageButton play;
    private Handler mHandler = new Handler();
    boolean playing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_player, container, false);
        textView = view.findViewById(R.id.player_name);
        textView.setText(name.substring(0, name.lastIndexOf('.')));
        seekBar = view.findViewById(R.id.seekBar);
        play = view.findViewById(R.id.play_pause);
        play.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getContext().getExternalFilesDir(null).getAbsolutePath() + "/MyFiles/" + name);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                play.setBackgroundDrawable(getActivity().getDrawable(R.drawable.play_icon));
                playing = false;
                seekBar.setProgress(0);
                mediaPlayer.seekTo(0);
            }
        });

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser && progress != seekBar.getMax()) {
                    mediaPlayer.seekTo(progress);
                }
            }
        });
        return view;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.release();
        mediaPlayer = null;
        dismissAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = getDialog().getWindow().getAttributes().height;
        int width = displayMetrics.widthPixels;

        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onClick(View view) {
        if (playing) {
            mediaPlayer.pause();
            play.setBackgroundDrawable(getActivity().getDrawable(R.drawable.play_icon));
            playing = false;
        } else {
            playing = true;
            mediaPlayer.start();
            play.setBackgroundDrawable(getActivity().getDrawable(R.drawable.pause_icon));
        }
    }
}
