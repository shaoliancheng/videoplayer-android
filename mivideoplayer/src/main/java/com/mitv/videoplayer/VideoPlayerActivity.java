package com.mitv.videoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hungama.movies.sdk.Utils.VideoPlayingType;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.mock.MockMediaProvider;
import com.mitv.videoplayer.videoview.VideoViewWrapper;

import org.json.JSONException;
import org.json.JSONObject;

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

//        startPlaykitPlaying();
//        startLivePlaying();
        startHungamaPlaying();
    }

    private void startHungamaPlaying() {
        JSONObject json = new JSONObject();
        try {
            json.put("contentId", "18261833");
            json.put("playingType", VideoPlayingType.MOVIE.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        onMediaLoaded("hungama", json.toString(), null);
    }

    private void startLivePlaying() {
        String uri = "http://kuai.xl.ptxl.gitv.tv/30/30/3030BE7A7DC0EADE2116E4892B38E6F4.mp4?timestamp=1501486582&sign=5d1f65eb41c5b5e80aa0aeb31bd3dee6";
        onMediaLoaded("live", uri, null);
    }

    private void startPlaykitPlaying() {
        MockMediaProvider mediaProvider = new MockMediaProvider("mock/entries.playkit.json", getApplicationContext(), "hls");
        mediaProvider.load(new OnMediaLoadCompletion() {
            @Override
            public void onComplete(final ResultElement<PKMediaEntry> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            onMediaLoaded("playkit", null, response.getResponse());
                        } else {

                            Toast.makeText(VideoPlayerActivity.this, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""));
                        }
                    }
                });
            }
        });
    }
    
    private void onMediaLoaded(String cp, String uri, PKMediaEntry mediaEntry) {
        mVideoView = new VideoViewWrapper(this, cp);
        switch (cp) {
            case "playkit":
                mVideoView.setMediaEntry(mediaEntry);
                break;
            case "hungama":
            case "live":
                mVideoView.setVideoUri(Uri.parse(uri), null);
                break;
        }

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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }
}
