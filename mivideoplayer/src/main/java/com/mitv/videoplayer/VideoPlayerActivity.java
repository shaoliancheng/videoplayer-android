package com.mitv.videoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.MediaEntryProvider;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.api.phoenix.APIDefines;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.mock.MockMediaProvider;
import com.kaltura.playkit.mediaproviders.ott.PhoenixMediaProvider;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.BaseTrack;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.utils.Consts;
import com.mitv.videoplayer.videoview.VideoViewWrapper;

import java.util.List;


public class VideoPlayerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private static final PKLog log = PKLog.get("VideoPlayerActivity");

    private MediaEntryProvider mediaProvider;
    private PlaybackControlsView controlsView;
    private boolean nowPlaying;
    ProgressBar progressBar;

    private Spinner videoSpinner, audioSpinner, textSpinner;
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

        log.i("PlayKitManager: " + PlayKitManager.CLIENT_TAG);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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
                            log.e("failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""));
                        }
                    }
                });
            }
        };

        startMockMediaLoading(playLoadedEntry);
//      startOvpMediaLoading(playLoadedEntry);
//      startOttMediaLoading(playLoadedEntry);
//      startSimpleOvpMediaLoading(playLoadedEntry);
//      LocalAssets.start(this, playLoadedEntry);

    }

    private void startSimpleOvpMediaLoading(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleOvpSessionProvider("https://cdnapisec.kaltura.com", 2222401, null))
                .setEntryId("1_f93tepsn")
                .load(completion);
    }

    private void startMockMediaLoading(OnMediaLoadCompletion completion) {

        mediaProvider = new MockMediaProvider("mock/entries.playkit.json", getApplicationContext(), "hls");

        mediaProvider.load(completion);
    }
    
    private void startOttMediaLoading(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                return MockParams.PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(MockParams.PnxKS));
                }
            }

            @Override
            public int partnerId() {
                return MockParams.OttPartnerId;
            }
        };

        mediaProvider = new PhoenixMediaProvider()
                .setSessionProvider(ksSessionProvider)
                .setAssetId(MockParams.SingMediaId) //bunny no horses id = "485380"
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setFormats(MockParams.Format_SD_Dash, MockParams.Format_HD_Dash, MockParams.Format, MockParams.Format2);

        mediaProvider.load(completion);
    }

    private void startOvpMediaLoading(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                return MockParams.OvpBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(MockParams.OvpUserKS));
                }
            }

            @Override
            public int partnerId() {
                return MockParams.OvpPartnerId;
            }
        };

        mediaProvider = new KalturaOvpMediaProvider().setSessionProvider(ksSessionProvider).setEntryId(MockParams.DRMEntryIdAnm);
        mediaProvider.load(completion);
    }

    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        mVideoView = new VideoViewWrapper(getApplicationContext(), "voot");
        mVideoView.setMediaEntry(mediaEntry);

        LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
        layout.addView(mVideoView.getView());

        controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
//        controlsView.setPlayer(player);
        initSpinners();

    }

    private void initSpinners() {
        videoSpinner = (Spinner) this.findViewById(R.id.videoSpinner);
        audioSpinner = (Spinner) this.findViewById(R.id.audioSpinner);
        textSpinner = (Spinner) this.findViewById(R.id.subtitleSpinner);

        textSpinner.setOnItemSelectedListener(this);
        audioSpinner.setOnItemSelectedListener(this);
        videoSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (controlsView != null) {
            controlsView.release();
        }
        if (mVideoView != null) {
//            mVideoView.onApplicationPaused();
        }
    }


    @Override
    protected void onResume() {
        log.d("Ad Event onResume");
        super.onResume();
        if (mVideoView != null) {
//            mVideoView.onApplicationResumed();
            if (nowPlaying && AUTO_PLAY_ON_RESUME) {
                mVideoView.start();
            }
        }
        if (controlsView != null) {
            controlsView.resume();
        }
    }

    /**
     * populating spinners with track info.
     *
     * @param tracksInfo - the track info.
     */
    private void populateSpinnersWithTrackInfo(PKTracks tracksInfo) {

        //Retrieve info that describes available tracks.(video/audio/subtitle).
        TrackItem[] videoTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_VIDEO, tracksInfo.getVideoTracks());
        //populate spinner with this info.
        applyAdapterOnSpinner(videoSpinner, videoTrackItems);

        TrackItem[] audioTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_AUDIO, tracksInfo.getAudioTracks());
        applyAdapterOnSpinner(audioSpinner, audioTrackItems);


        TrackItem[] subtitlesTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_TEXT, tracksInfo.getTextTracks());
        applyAdapterOnSpinner(textSpinner, subtitlesTrackItems);
    }


    /**
     * Obtain info that user is interested in.
     * For example if user want to display in UI bitrate of the available tracks,
     * he can do it, by obtaining the tackType of video, and getting the getBitrate() from videoTrackInfo.
     *
     * @param trackType  - tyoe of the track you are interested in.
     * @param trackInfos - all availables tracks.
     * @return
     */
    private TrackItem[] obtainRelevantTrackInfo(int trackType, List<? extends BaseTrack> trackInfos) {
        TrackItem[] trackItems = new TrackItem[trackInfos.size()];
        switch (trackType) {
            case Consts.TRACK_TYPE_VIDEO:
                TextView tvVideo = (TextView) this.findViewById(R.id.tvVideo);
                changeSpinnerVisibility(videoSpinner, tvVideo, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {
                    VideoTrack videoTrackInfo = (VideoTrack) trackInfos.get(i);
                    if(videoTrackInfo.isAdaptive()){
                        trackItems[i] = new TrackItem("Auto", videoTrackInfo.getUniqueId());
                    }else{
                        trackItems[i] = new TrackItem(String.valueOf(videoTrackInfo.getBitrate()), videoTrackInfo.getUniqueId());
                    }
                }

                break;
            case Consts.TRACK_TYPE_AUDIO:
                TextView tvAudio = (TextView) this.findViewById(R.id.tvAudio);
                changeSpinnerVisibility(audioSpinner, tvAudio, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {
                    AudioTrack audioTrackInfo = (AudioTrack) trackInfos.get(i);
                    if(audioTrackInfo.isAdaptive()){
                        trackItems[i] = new TrackItem("Auto", audioTrackInfo.getUniqueId());
                    }else{
                        String label = audioTrackInfo.getLanguage() != null ? audioTrackInfo.getLanguage() : audioTrackInfo.getLabel();
                        String bitrate = (audioTrackInfo.getBitrate() >  0)? "" + audioTrackInfo.getBitrate() : "";
                        trackItems[i] = new TrackItem(label + " " + bitrate, audioTrackInfo.getUniqueId());
                    }
                }
                break;
            case Consts.TRACK_TYPE_TEXT:
                TextView tvSubtitle = (TextView) this.findViewById(R.id.tvText);
                changeSpinnerVisibility(textSpinner, tvSubtitle, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {

                    TextTrack textTrackInfo = (TextTrack) trackInfos.get(i);
                    String lang = (textTrackInfo.getLanguage() != null) ? textTrackInfo.getLanguage() : "unknown";
                    trackItems[i] = new TrackItem(lang, textTrackInfo.getUniqueId());
                }
                break;
        }
        return trackItems;
    }

    private void changeSpinnerVisibility(Spinner spinner, TextView textView, List<? extends BaseTrack> trackInfos) {
        //hide spinner if no data available.
        if (trackInfos.isEmpty()) {
            textView.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }
    }

    private void applyAdapterOnSpinner(Spinner spinner, TrackItem[] trackInfo) {
        TrackItemAdapter trackItemAdapter = new TrackItemAdapter(this, R.layout.track_items_list_row, trackInfo);
        spinner.setAdapter(trackItemAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TrackItem trackItem = (TrackItem) parent.getItemAtPosition(position);
        //tell to the player, to switch track based on the user selection.
//        mVideoView.changeTrack(trackItem.getUniqueId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
