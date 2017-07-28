package com.mitv.videoplayer.videoview;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.SamplePlugin;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;
import com.kaltura.playkit.plugins.ads.ima.IMAPlugin;
import com.kaltura.playkit.plugins.playback.KalturaPlaybackRequestAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by chengsl on 7/28/17.
 */

class PlaykitVideoView implements IVideoView {
    private static final String TAG = "PlaykitVideoView";
    private Context mContext;
    private Player mPlayer;
    private AdsPlayListener AdsPlayListener;
    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    public PlaykitVideoView(Context context) {
        mContext = context;
        registerPlugins();
    }

    private void registerPlugins() {
        PlayKitManager.registerPlugins(null, SamplePlugin.factory);
        PlayKitManager.registerPlugins(null, IMAPlugin.factory);
        //PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);
        //PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);
    }

    @Override
    public View getView() {
        return mPlayer != null ? mPlayer.getView() : null;
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
        AdsPlayListener = adsPlayListener;
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

    private void configurePlugins(PKPluginConfigs config) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("delay", 1200);
        config.setPluginConfig("Sample", jsonObject);
        addIMAPluginConfig(config);
        //addPhoenixAnalyticsPluginConfig(config);
        //addTVPAPIAnalyticsPluginConfig(config);
        //config.setPluginConfig("IMASimplePlugin", jsonObject);
        //config.setPluginConfig("KalturaStatistics", jsonObject);
        //config.setPluginConfig("PhoenixAnalytics", jsonObject);
        //config.setPluginConfig("Youbora", jsonObject);
    }

//    private void addPhoenixAnalyticsPluginConfig(PKPluginConfigs config) {
//        String ks = "djJ8MTk4fHFftqeAPxdlLVzZBk0Et03Vb8on1wLsKp7cbOwzNwfOvpgmOGnEI_KZDhRWTS-76jEY7pDONjKTvbWyIJb5RsP4NL4Ng5xuw6L__BeMfLGAktkVliaGNZq9SXF5n2cMYX-sqsXLSmWXF9XN89io7-k=";
//        PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(198, "http://api-preprod.ott.kaltura.com/v4_2/api_v3/", ks, 30);
//        config.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(),phoenixAnalyticsConfig);
//    }
//
//    private void addTVPAPIAnalyticsPluginConfig(PKPluginConfigs config) {
//        TVPAPILocale locale = new TVPAPILocale("","","", "");
//        TVPAPIInitObject tvpapiInitObject = new TVPAPIInitObject(716158, "tvpapi_198", 354531, "e8aa934c-eae4-314f-b6a0-f55e96498786", "11111", "Cellular", locale);
//        TVPAPIAnalyticsConfig tvpapiAnalyticsConfig = new TVPAPIAnalyticsConfig("http://tvpapi-preprod.ott.kaltura.com/v4_2/gateways/jsonpostgw.aspx?", 30, tvpapiInitObject);
//        config.setPluginConfig(TVPAPIAnalyticsPlugin.factory.getName(),tvpapiAnalyticsConfig);
//    }

    private void addIMAPluginConfig(PKPluginConfigs config) {
        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        List<String> videoMimeTypes = new ArrayList<>();
        //videoMimeTypes.add(MimeTypes.APPLICATION_MP4);
        //videoMimeTypes.add(MimeTypes.APPLICATION_M3U8);
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");

        IMAConfig adsConfig = new IMAConfig().setAdTagURL(adTagUrl);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig.toJSONObject());

    }

    private void addPlayerListeners() {
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//                log.d("AD_CONTENT_PAUSE_REQUESTED");
//            }
//        }, AdEvent.Type.CONTENT_PAUSE_REQUESTED);
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//                AdEvent.AdCuePointsUpdateEvent cuePointsList = (AdEvent.AdCuePointsUpdateEvent) event;
//                AdCuePoints adCuePoints = cuePointsList.cuePoints;
//                if (adCuePoints != null) {
//                    log.d("Has Postroll = " + adCuePoints.hasPostRoll());
//                }
//            }
//        }, AdEvent.Type.CUEPOINTS_CHANGED);

        mPlayer.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "AD_STARTED");
                if (AdsPlayListener != null) {
                    AdsPlayListener.onAdsPlayStart();
                }
            }
        }, AdEvent.Type.STARTED);
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//                log.d("Ad Event AD_RESUMED");
//            }
//        }, AdEvent.Type.RESUMED);
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//            }
//        }, AdEvent.Type.SKIPPED);
        mPlayer.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "AD_ALL_ADS_COMPLETED");
                if (AdsPlayListener != null) {
                    AdsPlayListener.onAdsPlayEnd();
                }
            }
        }, AdEvent.Type.ALL_ADS_COMPLETED);

//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//            }
//        }, PlayerEvent.Type.PLAY);
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//            }
//        }, PlayerEvent.Type.PAUSE);


        mPlayer.addStateChangeListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "StateChanged: " + event);
                if (event instanceof PlayerEvent.StateChanged) {
                    PlayerEvent.StateChanged stateChanged = (PlayerEvent.StateChanged) event;
                    Log.d(TAG, "State changed from " + stateChanged.oldState + " to " + stateChanged.newState);
                    switch (stateChanged.newState) {
                        case IDLE:
                            break;
                        case LOADING:
                            break;
                        case READY:
                            if (mOnPreparedListener != null) {
                                mOnPreparedListener.onPrepared(null);
                            }
                            break;
                        case BUFFERING:
                            if (mOnInfoListener != null) {
                                mOnInfoListener.onInfo(null, 0, 0);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
//        mPlayer.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//                //When the track data available, this event occurs. It brings the info object with it.
//                PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;
//            }
//        }, PlayerEvent.Type.TRACKS_AVAILABLE);

        mPlayer.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "SEEKED");
                if (mOnSeekCompleteListener != null) {
                    mOnSeekCompleteListener.onSeekComplete(null);
                }
            }
        }, PlayerEvent.Type.SEEKED);
        mPlayer.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "ENDED");
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(null);
                }
            }
        }, PlayerEvent.Type.ENDED);
        mPlayer.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                Log.d(TAG, "ERROR");
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(null, 0, 0);
                }
            }
        }, PlayerEvent.Type.ERROR);

    }

    public void setMediaEntry(PKMediaEntry mediaEntry) {
        Log.d(TAG, "setMediaEntry: " + mediaEntry);
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (mPlayer == null) {
            configurePlugins(pluginConfig);

            mPlayer = PlayKitManager.loadPlayer(mContext, pluginConfig);
            KalturaPlaybackRequestAdapter.install(mPlayer, "myApp"); // in case app developer wants to give customized referrer instead the default referrer in the playmanifest

            Log.d(TAG, "Player: " + mPlayer.getClass());
            addPlayerListeners();
        }
        mPlayer.prepare(mediaConfig);
        mPlayer.play();
    }

    private PKMediaEntry simpleMediaEntry(String id, String contentUrl, String licenseUrl, PKDrmParams.Scheme scheme) {
        return new PKMediaEntry()
                .setSources(Collections.singletonList(new PKMediaSource()
                        .setUrl(contentUrl)
                        .setDrmData(Collections.singletonList(
                                new PKDrmParams(licenseUrl, scheme)
                                )
                        )))
                .setId(id);
    }

    @Override
    public void setVideoUri(Uri uri, Map<String, String> headers) {
        // todo
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        if (mPlayer != null) {
            mPlayer.play();
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        if (mPlayer != null) {
            mPlayer.pause();
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
            mPlayer.stop();
            mPlayer.destroy();
        }
    }

    @Override
    public void setAccountInfo(String accountType, String accountToken) {

    }
}
