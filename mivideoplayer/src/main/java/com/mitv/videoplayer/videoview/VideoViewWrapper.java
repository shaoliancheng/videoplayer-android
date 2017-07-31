package com.mitv.videoplayer.videoview;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.kaltura.playkit.PKMediaEntry;

import java.util.Map;

/**
 * Created by chengsl on 7/28/17.
 */

public class VideoViewWrapper implements IVideoView, ILifeCycle{
    private final Context mContext;
    private String mCp;
    private IVideoView mImp;

    public VideoViewWrapper(Context context, String cp) {
        mContext = context;
        mCp = cp;
        mImp = createVideoViewImp(cp);
    }

    private IVideoView createVideoViewImp(String cp) {
        switch (cp) {
            case "voot":
                return new PlaykitVideoView(mContext);
            default:
                return null;
        }
    }

    @Override
    public View getView() {
        return mImp != null ? mImp.getView() : null;
    }

    @Override
    public int getVideoWidth() {
        return mImp != null ? mImp.getVideoWidth() : 0;
    }

    @Override
    public int getVideoHeight() {
        return mImp != null ? mImp.getVideoHeight() : 0;
    }

    @Override
    public void setForceFullScreen(boolean forceFullScreen) {
        if (mImp != null) {
            mImp.setForceFullScreen(forceFullScreen);
        }
    }

    @Override
    public void setAdsListener(AdsPlayListener adsPlayListener) {
        if (mImp != null) {
            mImp.setAdsListener(adsPlayListener);
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        if (mImp != null) {
            mImp.setOnPreparedListener(listener);
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        if (mImp != null) {
            mImp.setOnCompletionListener(listener);
        }
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        if (mImp != null) {
            mImp.setOnErrorListener(listener);
        }
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        if (mImp != null) {
            mImp.setOnSeekCompleteListener(listener);
        }
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        if (mImp != null) {
            mImp.setOnInfoListener(listener);
        }
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        if (mImp != null) {
            mImp.setOnBufferingUpdateListener(listener);
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        if (mImp != null) {
            mImp.setOnVideoSizeChangedListener(listener);
        }
    }

    // for voot sdk
    public void setMediaEntry(PKMediaEntry mediaEntry) {
        if (mImp != null && mImp instanceof PlaykitVideoView) {
            ((PlaykitVideoView) mImp).setMediaEntry(mediaEntry);
        }
    }

    @Override
    public void setVideoUri(Uri uri, Map<String, String> headers) {
        if (mImp != null) {
            mImp.setVideoUri(uri, headers);
        }
    }

    @Override
    public void start() {
        if (mImp != null) {
            mImp.start();
        }
    }

    @Override
    public void pause() {
        if (mImp != null) {
            mImp.pause();
        }
    }

    @Override
    public int getDuration() {
        return mImp != null ? mImp.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mImp != null ? mImp.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int msec) {
        if (mImp != null) {
            mImp.seekTo(msec);
        }
    }

    @Override
    public boolean isPlaying() {
        return mImp != null && mImp.isPlaying();
    }

    @Override
    public boolean canPause() {
        return mImp != null && mImp.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return mImp != null && mImp.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return mImp != null && mImp.canSeekForward();
    }

    @Override
    public void stopPlayback() {
        if (mImp != null) {
            mImp.stopPlayback();
        }
    }

    @Override
    public void setAccountInfo(String accountType, String accountToken) {
        if (mImp != null) {
            mImp.setAccountInfo(accountType, accountToken);
        }
    }

    @Override
    public void onApplicationPaused() {
        if (mImp instanceof PlaykitVideoView && ((PlaykitVideoView) mImp).getPlayer() != null) {
            ((PlaykitVideoView) mImp).getPlayer().onApplicationPaused();
        }
    }

    @Override
    public void onApplicationResumed() {
        if (mImp instanceof PlaykitVideoView && ((PlaykitVideoView) mImp).getPlayer() != null) {
            ((PlaykitVideoView) mImp).getPlayer().onApplicationResumed();
        }
    }
}
