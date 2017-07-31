package com.mitv.videoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.mock.MockMediaProvider;
import com.mitv.videoplayer.videoview.VideoViewWrapper;

/**
 * Created by chengsl on 7/28/17.
 */

public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";
    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private TvControlsView controlsView;
    private VideoViewWrapper mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Please tap ALLOW", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        OnMediaLoadCompletion playLoadedEntry = new OnMediaLoadCompletion() {
            @Override
            public void onComplete(final ResultElement<PKMediaEntry> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            onMediaLoaded(response.getResponse());
                        } else {

                            Toast.makeText(VideoPlayerActivity.this, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""));
                        }
                    }
                });
            }
        };

        startMockMediaLoading(playLoadedEntry);
    }

    private void startMockMediaLoading(OnMediaLoadCompletion completion) {
        MockMediaProvider mediaProvider = new MockMediaProvider("mock/entries.playkit.json", getApplicationContext(), "hls");
        mediaProvider.load(completion);
    }
    
    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        mVideoView = new VideoViewWrapper(getApplicationContext(), "voot");
        mVideoView.setMediaEntry(mediaEntry);

        LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
        layout.addView(mVideoView.getView());

        controlsView = (TvControlsView) this.findViewById(R.id.playerControls);
        controlsView.setVideoView(mVideoView);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (controlsView != null) {
            controlsView.pause();
        }
        if (mVideoView != null) {
            mVideoView.onApplicationPaused();
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onApplicationResumed();
            if (mVideoView.isPlaying() && AUTO_PLAY_ON_RESUME) {
                mVideoView.start();
            }
        }
        if (controlsView != null) {
            controlsView.resume();
        }
    }

}
