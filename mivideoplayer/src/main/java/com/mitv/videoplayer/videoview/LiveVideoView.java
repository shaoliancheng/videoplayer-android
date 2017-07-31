package com.mitv.videoplayer.videoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;

/**
 * Created by chengsl on 7/31/17.
 */

public class LiveVideoView extends FrameLayout implements IVideoView {
    private static final String TAG = "LiveVideoView";
    private InnerSurfaceView mSurfaceView;
    private MediaPlayer mPlayer;
    private AdsPlayListener mAdsPlayListener;
    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private boolean mOnPrepared;

    public LiveVideoView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        mSurfaceView = new InnerSurfaceView(getContext());
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mSurfaceView, 0, layout);
        setBackgroundColor(Color.BLACK);
        mOnPrepared = false;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getVideoWidth() {
        return mPlayer != null ? mPlayer.getVideoWidth() : 0;
    }

    @Override
    public int getVideoHeight() {
        return mPlayer != null ? mPlayer.getVideoHeight() : 0;
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {

    }

    @Override
    public void setAdsListener(AdsPlayListener adsPlayListener) {
        mAdsPlayListener = adsPlayListener;
    }

    @Override
    public void setOnPreparedListener(final OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;
    }

    @Override
    public void setVideoUri(Uri uri, Map<String, String> headers) {
        Log.d(TAG, "setVideoUri: " + uri);
        releaseMediaPlayer(mPlayer);
        MediaPlayer player = createMediaPlayer(uri, headers);
        initMediaPlayer(player);
    }

    private MediaPlayer createMediaPlayer(Uri uri, Map<String, String> headers) {
        Log.d(TAG, "createMediaPlayer: " + uri);
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        try {
            mPlayer.setDataSource(getContext(), uri, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mPlayer;
    }

    private MediaPlayer initMediaPlayer(MediaPlayer mp) {
        Log.d(TAG, "initMediaPlayer: " + mp);
        if (mp != null) {
            if (mSurfaceView != null && mSurfaceView.getSurfaceHolder() != null) {
                mp.setDisplay(mSurfaceView.getSurfaceHolder());
            }
            addPlayerListeners(mp);
            mp.prepareAsync();
        }
        return mp;
    }

    private void addPlayerListeners(MediaPlayer mp) {
        if (mp != null) {
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mOnPrepared = true;
                    if (mPlayer != null && !mPlayer.isPlaying()) {
                        start();
                    }
                    if (mOnPreparedListener != null) {
                        mOnPreparedListener.onPrepared(mPlayer);
                    }
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion");
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mPlayer);
                    }
                }
            });

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "onError: " + what + ", " + extra);
                    if (mOnErrorListener != null) {
                        mOnErrorListener.onError(mPlayer, 0, 0);
                    }
                    return false;
                }
            });

            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.d(TAG, "onSeekComplete");
                    if (mOnSeekCompleteListener != null) {
                        mOnSeekCompleteListener.onSeekComplete(mPlayer);
                    }
                }
            });

            mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "onInfo: " + what + ", " + extra);
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mPlayer, what, extra);
                    }
                    return false;
                }
            });

            mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    Log.d(TAG, "onBufferingUpdate: " + percent);
                    if (mOnBufferingUpdateListener != null) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mPlayer, percent);
                    }
                }
            });

            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int widht, int height) {
                    Log.d(TAG, "onVideoSizeChanged: " + widht + " x " + height);
                    if (mOnVideoSizeChangedListener != null) {
                        mOnVideoSizeChangedListener.onVideoSizeChanged(mPlayer, widht, height);
                    }
                }
            });
        }
    }

    private void releaseMediaPlayer(MediaPlayer mp) {
        Log.d(TAG, "releaseMediaPlayer: " + mp);
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        if (mPlayer != null && mOnPrepared && mSurfaceView.getSurfaceHolder() != null) {
            Log.d(TAG, "call start");
            mPlayer.start();
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        if (mPlayer != null && canPause()) {
            mPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return (mPlayer != null && mOnPrepared) ? mPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer != null ? mPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int msec) {
        Log.d(TAG, "seekTo: " + msec);
        if (mPlayer != null) {
            mPlayer.seekTo(msec);
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public void stopPlayback() {
        Log.d(TAG, "stopPlayback");
        if (mPlayer != null) {
            releaseMediaPlayer(mPlayer);
            mPlayer = null;
        }
    }

    @Override
    public void setAccountInfo(String accountType, String accountToken) {

    }

    private class InnerSurfaceView extends SurfaceView {
        public InnerSurfaceView(Context context) {
            super(context);
            getHolder().addCallback(mCallback);
        }

        public SurfaceHolder getSurfaceHolder() {
            return mSurfaceHolder;
        }

        private SurfaceHolder mSurfaceHolder;
        private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceCreated: " + surfaceHolder);
                mSurfaceHolder = surfaceHolder;
                if (mPlayer != null) {
                    mPlayer.setDisplay(mSurfaceHolder);
                    if (!isPlaying()) {
                        start();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG, "surfaceChanged: " + surfaceHolder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceDestroyed");
                mSurfaceHolder = null;
                if (mPlayer != null) {
                    mPlayer.setDisplay(null);
                }
            }
        };

    }
}
