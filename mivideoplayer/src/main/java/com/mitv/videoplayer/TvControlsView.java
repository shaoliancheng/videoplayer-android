package com.mitv.videoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.mitv.videoplayer.videoview.IVideoView;

/**
 * Created by chengsl on 7/28/17.
 */

public class TvControlsView extends RelativeLayout {
    private IVideoView mVideoView;

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
    }

    public void setVideoView(IVideoView videoView) {
        mVideoView = videoView;
    }

    public void pause() {

    }

    public void resume() {

    }
}
