package com.hcmute.android.project.week9.toeic1234test01.infrastructure.media.concurrency;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayerBackgroundExoPlayer implements AudioPlayerBackground {
    private static final String ANDROID_LOGGING_TAG = "D_AUDIO";
    private static final List<ExoPlayer> listMediaPlayerBackgrounds = new ArrayList<>();
    private ExoPlayer exoPlayer;

    private OnAudioPlayerRunningEvent onAudioPlayerRunningEvent;

    public AudioPlayerBackgroundExoPlayer(Context context) {
        this.exoPlayer = new ExoPlayer.Builder(context).build();

        this.exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (onAudioPlayerRunningEvent != null) {
                    if (playbackState == Player.STATE_READY) {
                        onAudioPlayerRunningEvent.onReady();
                    } else if (playbackState == Player.STATE_ENDED) {
                        onAudioPlayerRunningEvent.onFinished();
                        exoPlayer.setPlayWhenReady(false);
                    } else {
                        onAudioPlayerRunningEvent.onProcessing();
                    }
                }
            }
        });

        this.exoPlayer.setPlayWhenReady(false);
        this.exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        this.exoPlayer.setSeekParameters(SeekParameters.EXACT);

        listMediaPlayerBackgrounds.add(this.exoPlayer);
        Log.d(ANDROID_LOGGING_TAG, "init ok media player " + this.exoPlayer);
        this.initTimer(context);
    }

    private void initTimer(Context context) {
        final int delayTime = 200;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Activity activity = (Activity)context;

                if (activity.isDestroyed()) {
                    if (listMediaPlayerBackgrounds.remove(exoPlayer))
                        Log.d(ANDROID_LOGGING_TAG, "destroy ok " + exoPlayer);
                    else
                        Log.d(ANDROID_LOGGING_TAG, "destroy failed " + exoPlayer);

                    try {
                        exoPlayer.release();
                    }
                    catch (Exception ignored) { }

                    exoPlayer = null;
                    return;
                }

                activity.runOnUiThread(() -> {
                    try {
                        if (exoPlayer.isPlaying()) {
                            if (onAudioPlayerRunningEvent != null) {
                                final long currentTime =  exoPlayer.getCurrentPosition() / 1000;
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
            this.exoPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(file)));
            this.exoPlayer.prepare();
        } catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    @Override
    public long getDuration() {
        try {
            return this.exoPlayer.getDuration() / 1000;
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
            return this.exoPlayer.isPlaying();
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
            return this.exoPlayer.getCurrentPosition() / 1000;
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
            this.exoPlayer.seekTo(seconds * 1000L);
        }
        catch (Exception e) {
            if (this.onAudioPlayerRunningEvent != null) {
                this.onAudioPlayerRunningEvent.onException(e);
            }
        }
    }

    @Override
    public void start(boolean pauseAllAnotherPlayers) {
        Log.d("aaaa", "start!!");
        if (pauseAllAnotherPlayers) {
            pauseAllPlayersExceptMe();
        }

        try {
            if (!this.exoPlayer.isPlaying()) {
                this.exoPlayer.play();
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
            if (this.exoPlayer.isPlaying()) {
                this.exoPlayer.pause();
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

    private static void pauseAllPlayersExcept(ExoPlayer mediaPlayer) {
        for (ExoPlayer mp : listMediaPlayerBackgrounds) {
            if (mediaPlayer == mp)
                continue;
            try {
                if (mp.isPlaying())
                    mp.pause();
            }
            catch (Exception ignored) { }
        }
    }

    private static Map<ExoPlayer, Boolean> isPlayerStored;

    public static void saveAllPlayerStates() {
        isPlayerStored = new HashMap<>();
        for (ExoPlayer mediaPlayer : listMediaPlayerBackgrounds) {
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
        for (Map.Entry<ExoPlayer, Boolean> entry : isPlayerStored.entrySet()) {
            try {
                if (entry.getValue()) {
                    entry.getKey().play();
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
        pauseAllPlayersExcept(this.exoPlayer);
    }
}
