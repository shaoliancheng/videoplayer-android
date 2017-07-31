package com.mitv.videoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mitv.videoplayer.videoview.IVideoView;

/**
 * Created by chengsl on 7/28/17.
 */

public class TvControlsView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "TvControlsView";
    private IVideoView mVideoView;
    private ProgressBar mProgressBar;
    private Button mPlayButton;
    private Button mPauseButton;

    public TvControlsView(Context context) {
        this(context, null);
    }

    public TvControlsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    @TargetApi(21)
    public TvControlsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tv_controls_view, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(0);
        mPlayButton = (Button) findViewById(R.id.play);
        mPlayButton.setOnClickListener(this);
        mPauseButton = (Button) findViewById(R.id.pause);
        mPauseButton.setOnClickListener(this);
    }

    public void setVideoView(IVideoView videoView) {
        mVideoView = videoView;
        int duration = mVideoView.getDuration() / 1000;
        Log.d(TAG, "setVideoView: " + videoView + ", " + duration);
        mProgressBar.setMax(duration);
        updateProgress();
    }

    public void pause() {
        Log.d(TAG, "pause");
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.pause();
        }
    }

    public void resume() {
        Log.d(TAG, "resume");
        if (mVideoView != null && !mVideoView.isPlaying()) {
            mVideoView.start();
            updateProgress();
        }
    }

    private void updateProgress() {
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                post(mUpdateProgress);
            } else {
                postDelayed(mUpdateProgress, 1000);
            }
        }
    }

    private Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
//            if (mVideoView != null && mVideoView.isPlaying()) {
                if (mProgressBar.getMax() == 0) {
                    int duration = mVideoView.getDuration() / 1000;
                    Log.d(TAG, "mUpdateProgress: duration " + duration);
                    mProgressBar.setMax(duration);
                }
                int progress = mVideoView.getCurrentPosition() / 1000;
//                Log.d(TAG, "mUpdateProgress: " + progress);
                mProgressBar.setProgress(progress);
                postDelayed(mUpdateProgress, 1000);
//            }
        }
    };

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view);
        switch (view.getId()) {
            case R.id.play:
                resume();
                break;
            case R.id.pause:
                pause();
                break;
        }
    }
}
