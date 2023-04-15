package com.hcmute.android.project.week9.toeic1234test01.infrastructure.media.concurrency;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayerBackgroundMediaPlayer implements AudioPlayerBackground {
    private static final String ANDROID_LOGGING_TAG = "D_AUDIO";
    private static final List<MediaPlayer> listMediaPlayerBackgrounds = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    private OnAudioPlayerRunningEvent onAudioPlayerRunningEvent;

    public AudioPlayerBackgroundMediaPlayer(Context context) {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.reset();

        listMediaPlayerBackgrounds.add(this.mediaPlayer);
        Log.d(ANDROID_LOGGING_TAG, "init ok media player " + this.mediaPlayer);
        this.initTimer(context);

        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onAudioPlayerRunningEvent != null) {
                    onAudioPlayerRunningEvent.onFinished();
                }
            }
        });
    }

    private void initTimer(Context context) {
        final int delayTime = 200;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Activity activity = (Activity)context;

                if (activity.isDestroyed()) {
                    if (listMediaPlayerBackgrounds.remove(mediaPlayer))
                        Log.d(ANDROID_LOGGING_TAG, "destroy ok " + mediaPlayer);
                    else
                        Log.d(ANDROID_LOGGING_TAG, "destroy failed " + mediaPlayer);

                    try {
                        mediaPlayer.release();
                    }
                    catch (Exception ignored) { }

                    mediaPlayer = null;
                    return;
                }

                activity.runOnUiThread(() -> {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            if (onAudioPlayerRunningEvent != null) {
                                final int currentTime =  mediaPlayer.getCurrentPosition() / 1000;
                                Log.d("ahihi", currentTime + " " + mediaPlayer.getCurrentPosition());
                                onAudioPlayerRunningEvent.playing(currentTime);
                            }
                        }
                    }
                    catch (Exception ignored) { }
                });

                handler.postDelayed(this, delayTime);
            }
        }, delayTime);
    }

    @Override
    public void setOnAudioPlayerRunningEvent(OnAudioPlayerRunningEvent onAudioPlayerRunningEvent) {
        this.onAudioPlayerRunningEvent = onAudioPlayerRunningEvent;
    }

    @Override
    public void prepareAudioFile(File file) {
        try {
            this.mediaPlayer.setDataSource(file.getAbsolutePath());
            this.mediaPlayer.prepareAsync();
            this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (onAudioPlayerRunningEvent != null) {
                        onAudioPlayerRunningEvent.onReady();
                    }
                }
            });
        } catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    @Override
    public long getDuration() {
        try {
            return this.mediaPlayer.getDuration() / 1000;
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return this.mediaPlayer.isPlaying();
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
            return false;
        }
    }

    @Override
    public long getCurrentTime() {
        try {
            return this.mediaPlayer.getCurrentPosition() / 1000;
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
            return 0;
        }
    }

    @Override
    public void seekTo(int seconds) {
        try {
            this.mediaPlayer.seekTo(seconds * 1000);
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    @Override
    public void start(boolean pauseAllAnotherPlayers) {
        if (pauseAllAnotherPlayers) {
            pauseAllPlayersExceptMe();
        }

        try {
            if (!this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.start();
                if (this.onAudioPlayerRunningEvent != null) {
                    this.onAudioPlayerRunningEvent.afterStarted();
                }
            }
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    @Override
    public void pause() {
        try {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.pause();
                if (this.onAudioPlayerRunningEvent != null) {
                    this.onAudioPlayerRunningEvent.afterPaused();
                }
            }
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    private static void pauseAllPlayersExcept(MediaPlayer mediaPlayer) {
        for (MediaPlayer mp : listMediaPlayerBackgrounds) {
            if (mediaPlayer == mp)
                continue;
            try {
                if (mp.isPlaying())
                    mp.pause();
            }
            catch (Exception ignored) { }
        }
    }

    private static Map<MediaPlayer, Boolean> isPlayerStored;

    public static void saveAllPlayerStates() {
        isPlayerStored = new HashMap<>();
        for (MediaPlayer mediaPlayer : listMediaPlayerBackgrounds) {
            try {
                isPlayerStored.put(mediaPlayer, mediaPlayer.isPlaying());
            }
            catch (Exception ignored) { }
        }
    }

    public static void restoreAllPlayerStates() {
        if (isPlayerStored == null) {
            return;
        }
        for (Map.Entry<MediaPlayer, Boolean> entry : isPlayerStored.entrySet()) {
            try {
                if (entry.getValue()) {
                    entry.getKey().start();
                }
            }
            catch (Exception ignored) { }
        }
    }

    public static void pauseAllPlayers() {
        pauseAllPlayersExcept(null);
    }

    @Override
    public void pauseAllPlayersExceptMe() {
        pauseAllPlayersExcept(this.mediaPlayer);
    }
}
