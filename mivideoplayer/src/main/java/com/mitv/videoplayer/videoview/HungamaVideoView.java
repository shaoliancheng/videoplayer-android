package com.mitv.videoplayer.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.catchmedia.cmsdkCore.managers.CMSDKCoreManager;
//import com.crashlytics.android.Crashlytics;
import com.hungama.movies.sdk.Utils.M3u8MetaData;
import com.hungama.movies.sdk.Utils.MoviesApplication;
import com.hungama.movies.sdk.Utils.PlayUtils;
import com.hungama.movies.sdk.Utils.PlaybackController;
import com.hungama.movies.sdk.Utils.PlaybackControllerCallback;
import com.hungama.movies.sdk.Utils.PlaybackControllerState;
import com.hungama.movies.sdk.Utils.PlaybackState;
import com.hungama.movies.sdk.Utils.PlayerEventsCallback;
import com.hungama.movies.sdk.Utils.VideoPlayingType;
import com.hungama.movies.sdk.Utils.WebServiceError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

//import io.fabric.sdk.android.Fabric;

/**
 * Created by chengsl on 8/4/17.
 */

class HungamaVideoView implements IVideoView {
    private static final String TAG = "HungamaVideoView";
    private final Context mContext;
    private PlaybackController mPlayer;
    private String mContentId;
    private VideoPlayingType mPlayingType;
    private int mOffset;
    private static boolean sInitSdk;

    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public HungamaVideoView(Context context) {
        mContext = context;
        initSdk();
    }

    private void initSdk() {
        if (!sInitSdk) {
            Log.d(TAG, "initSdk");
            sInitSdk = true;
            MoviesApplication.initializeSDK(((Activity)mContext).getApplication());
//            Fabric.with(mContext, new Crashlytics());

            // just workaround for crash
            try {
                CMSDKCoreManager cmm = CMSDKCoreManager.getInstance();
                Field f1 = cmm.getClass().getDeclaredField("mActivityWorkDuration");
                f1.setAccessible(true);
                f1.setInt(cmm, 100);
                f1.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PlayUtils.SetUser(mContext, "117742");
        }
    }

    @Override
    public View getView() {
        return mPlayer != null ? mPlayer.getPlaybackView() : null;
    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {

    }

    @Override
    public void setAdsListener(AdsPlayListener adsPlayListener) {

    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
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
        try {
            JSONObject json = new JSONObject(uri.toString());
            mContentId = json.getString("contentId");
            mPlayingType = VideoPlayingType.fromInteger(json.getInt("playingType"));
            mOffset = json.getInt("offset");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        createPlayer();
        initPlayer();
    }

    private void createPlayer() {
        Log.d(TAG, "createPlayer: " + mContentId + ", " + mPlayingType + ", " + mOffset);
        mPlayer = new PlaybackController((Activity) mContext, mContentId, mPlayingType, mOffset);
    }

    private void initPlayer() {
        mPlayer.setPlayerControlEnable(false);

//        mPlayer.setContentName(contentName);
//        mPlayer.setContentImage(contentImage);
//        mPlayer.setContentImageTile(contentImage);

        mPlayer.setPlaybackControllerCallback(new PlaybackControllerCallback() {

            @Override
            public void onPlayerStateChanged(PlaybackControllerState playbackControllerState) {
                switch (playbackControllerState){
                    case STATE_BUFFERING:
                        Log.d(TAG,"STATE_BUFFERING");
                        break;
                    case STATE_IDLE:
                        Log.d(TAG,"STATE_IDLE");
                        break;
                    case STATE_ENDED:
                        Log.d(TAG,"STATE_ENDED");
                        break;
                    case STATE_READY:
                        Log.d(TAG,"STATE_READY");
                        start();
                        break;
                    case STATE_PREPARING:
                        Log.d(TAG,"STATE_PREPARING");
                        break;

                }
            }
        });

        mPlayer.setPlayerEventsCallback(mPlayerEventsCallback);
    }

    private PlayerEventsCallback mPlayerEventsCallback = new PlayerEventsCallback() {
        @Override
        public void onPlaybackProgress(long l, long l1) {
            Log.d(TAG,"onPlaybackProgress: " + l + ", " + l1);
        }

        @Override
        public void onSeekingStateChanged(boolean b) {
            Log.d(TAG,"onSeekingStateChanged: " + b);
        }

        @Override
        public void onSeekingProgressChanged(int i) {
            Log.d(TAG,"onSeekingProgressChanged: " + i);
        }

        @Override
        public void onPlayerBufferingStateChanged(boolean b) {
            Log.d(TAG,"onPlayerBufferingStateChanged: " + b);
        }

        @Override
        public void onVariantChanged(M3u8MetaData m3u8MetaData) {
            Log.d(TAG,"onVariantChanged: " + m3u8MetaData);
        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG,"onError: " + e);
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(null, 100, 1);
            }
        }

        @Override
        public void onStateChanged(PlaybackState playbackState) {
            Log.d(TAG,"onStateChanged: " + playbackState);
        }

        @Override
        public void changeOrientation(int i) {
            Log.d(TAG,"changeOrientation");
        }

        @Override
        public void showToolBar(boolean b) {
            Log.d(TAG,"showToolBar");
        }

        @Override
        public void onVariantList(ArrayList<M3u8MetaData> arrayList) {
            Log.d(TAG,"onVariantList");
        }

        @Override
        public void onCastConnected() {
            Log.d(TAG,"onCastConnected");
        }

        @Override
        public void onDataConsumption(long l) {
            Log.d(TAG,"onDataConsumption: " + l);
        }

        @Override
        public void onFail(WebServiceError webServiceError) {
            Log.d(TAG,"onFail: " + webServiceError);
        }
    };

    @Override
    public void start() {
        Log.d(TAG, "start");
        if (mPlayer != null) {
            Log.d(TAG, "call start");
            mPlayer.resumeMovie();
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        if (mPlayer != null) {
            Log.d(TAG, "call pause");
            mPlayer.pauseMovie();
        }
    }

    @Override
    public int getDuration() {
        return mPlayer != null ? (int) mPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer != null ? (int) mPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int msec) {
        Log.d(TAG, "seekTo");
        if (mPlayer != null) {
            Log.d(TAG, "call seekTo");
            mPlayer.seekTo(msec);
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPLaying();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public void stopPlayback() {
        Log.d(TAG, "stopPlayback");
        if (mPlayer != null) {
            Log.d(TAG, "call shutdownPlayer");
            mPlayer.shutdownPlayer();
        }
    }

    @Override
    public void setAccountInfo(String accountType, String accountToken) {

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(mPlaybackController!=null)
//            mPlaybackController.onResume();
//
//
//    }
//    @Override
//    public void onPause() {
//        if(mPlaybackController!=null)
//            mPlaybackController.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        if(mPlaybackController!=null)
//            mPlaybackController.onDestroy();
//
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(mPlaybackController!=null && mPlaybackController.onBackPressed())
//            return;
//        else
//            super.onBackPressed();
//    }


}
