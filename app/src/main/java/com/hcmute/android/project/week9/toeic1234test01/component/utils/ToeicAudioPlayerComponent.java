package com.hcmute.android.project.week9.toeic1234test01.component.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.hcmute.android.project.week9.toeic1234test01.R;
import com.hcmute.android.project.week9.toeic1234test01.infrastructure.media.concurrency.AudioPlayerBackground;
import com.hcmute.android.project.week9.toeic1234test01.infrastructure.media.concurrency.AudioPlayerBackgroundFactory;
import com.hcmute.android.project.week9.toeic1234test01.infrastructure.media.concurrency.AudioPlayerBackgroundMediaPlayer;

import java.io.File;

public class ToeicAudioPlayerComponent extends LinearLayout {
    // Default constant
    private static final AudioPlayerBackgroundFactory.AudioMediaPlayerLibrary DEFAULT_AUDIO_LIBRARY
            = AudioPlayerBackgroundFactory.AudioMediaPlayerLibrary.GOOGLE_EXO_PLAYER;

    // Component
    private AppCompatImageButton btnTogglePlayPauseAudio;
    private SeekBar seekBar;
    private TextView txtCurrentTime;
    private TextView txtTotalTime;
    private File audioFileSource;

    // Variable & Model
    private long totalTime = 0;
    private long currentTime = 0;
    private AudioPlayerBackground audioPlayerBackgroundMediaPlayer;
    private boolean isAudioEngineInitialized = false;
    private boolean playAudioAfterInitialized = false;

    public ToeicAudioPlayerComponent(Context context) {
        super(context);
        this.initComponent();
    }

    public ToeicAudioPlayerComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponent();
    }

    private void initComponent() {
        inflate(this.getContext(), R.layout.component_toeic_android_player, this);

        if (isInEditMode())
            return;

        this.audioPlayerBackgroundMediaPlayer = AudioPlayerBackgroundFactory.createInstance(DEFAULT_AUDIO_LIBRARY, this.getContext());

        this.btnTogglePlayPauseAudio = findViewById(R.id.component_toeic_android_player_btn_play_pause);
        this.seekBar = findViewById(R.id.component_toeic_android_player_seek_bar);
        this.txtCurrentTime = findViewById(R.id.component_toeic_android_player_current_time);
        this.txtTotalTime = findViewById(R.id.component_toeic_android_player_total_time);

        assert this.btnTogglePlayPauseAudio != null;
        assert this.seekBar != null;
        assert this.txtCurrentTime != null;
        assert this.txtTotalTime != null;

        this.setStartAudioButton();

        this.setCurrentTime(0);
        this.setTotalTime(0);

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;

                audioPlayerBackgroundMediaPlayer.start(true);
                setCurrentTime(progress);
                audioPlayerBackgroundMediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                audioPlayerBackgroundMediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audioPlayerBackgroundMediaPlayer.start(true);
            }
        });

        this.btnTogglePlayPauseAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPlayerBackgroundMediaPlayer.isPlaying()) {
                    audioPlayerBackgroundMediaPlayer.pause();
                } else {
                    audioPlayerBackgroundMediaPlayer.start(true);
                }
            }
        });

        this.setEnabled(false);

        this.audioPlayerBackgroundMediaPlayer.setOnAudioPlayerRunningEvent(new AudioPlayerBackgroundMediaPlayer.OnAudioPlayerRunningEvent() {
            @Override
            public void afterStarted() {
                setPauseAudioButton();
            }

            @Override
            public void afterPaused() {
                setStartAudioButton();
            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void playing(long currentTime) {
                setCurrentTime(currentTime);
            }

            @Override
            public void onFinished() {
                setStartAudioButton();
                audioPlayerBackgroundMediaPlayer.seekTo(0);
                audioPlayerBackgroundMediaPlayer.pause();
                seekBar.setProgress(0);
            }

            @Override
            public void onReady() {
                setEnabled(true);
                setTotalTime(audioPlayerBackgroundMediaPlayer.getDuration());
                if (!isAudioEngineInitialized) {
                    isAudioEngineInitialized = true;
                    if (playAudioAfterInitialized) {
                        audioPlayerBackgroundMediaPlayer.start(true);
                    }
                }
            }

            @Override
            public void onProcessing() {
                setEnabled(false);
            }
        });
    }

    private void setPauseAudioButton() {
        btnTogglePlayPauseAudio.setImageResource(R.drawable.pause_01);
        btnTogglePlayPauseAudio.setScaleX(0.7f);
        btnTogglePlayPauseAudio.setScaleY(0.7f);
    }

    private void setStartAudioButton() {
        btnTogglePlayPauseAudio.setImageResource(R.drawable.player_01);
        btnTogglePlayPauseAudio.setScaleX(0.7f);
        btnTogglePlayPauseAudio.setScaleY(0.7f);
    }

    public long getTotalTime() {
        return totalTime;
    }


    public long getCurrentTime() {
        return currentTime;
    }

    public void setAudioFileSource(File audioFileSource) {
        assert audioFileSource.exists();

        this.audioFileSource = audioFileSource;

        audioPlayerBackgroundMediaPlayer.prepareAudioFile(audioFileSource);
    }

    public void playAudio() {
        if (!this.isAudioEngineInitialized) {
            this.playAudioAfterInitialized = true;
        }
        else {
            this.audioPlayerBackgroundMediaPlayer.start(true);
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public void setTotalTime(long seconds) {
        assert this.currentTime <= this.totalTime;

        final String fmtTime = this.formatTime(seconds);
        this.txtTotalTime.setText(fmtTime);
        this.totalTime = seconds;
        this.seekBar.setMax((int) seconds);
    }

    public void setCurrentTime(long seconds) {
        final String fmtTime = this.formatTime(seconds);
        this.txtCurrentTime.setText(fmtTime);
        this.currentTime = seconds;
        this.seekBar.setProgress((int) seconds);
    }
}
