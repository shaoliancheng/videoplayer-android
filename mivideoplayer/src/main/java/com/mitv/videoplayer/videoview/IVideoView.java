package com.mitv.videoplayer.videoview;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import java.util.Map;

/**
 * Created by chengsl on 7/28/17.
 */

public interface IVideoView {
    View getView();
    /**
     * Returns the width of the video.
     *
     * @return the width of the video, or 0 if there is no video,
     * no display surface was set, or the width has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {#setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener)}
     * to provide a notification when the width is available.
     */
    int getVideoWidth();

    /**
     * Returns the height of the video.
     *
     * @return the height of the video, or 0 if there is no video,
     * no display surface was set, or the height has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {#setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener)}
     * to provide a notification when the height is available.
     */
    int getVideoHeight();


    /**
     *Stretch the video to full screen size or keep its original size according to the parameter
     *'forceFullScreen'
     *
     * @param forceFullScreen boolean, if set to true, stretch the video to full screen size.
     * Otherwise, set the video to its original size.
     */
    void setForceFullScreen(boolean forceFullScreen);

    /**
     * Register a callback to be invoked when the advertisement playing.
     *
     * @param adsPlayListener the callback that will be run
     */
    void setAdsListener(AdsPlayListener adsPlayListener);

    /**
     * Register a callback to be invoked when the video is loaded and ready to go.
     *
     * @param listener The callback that will be run
     */
    void setOnPreparedListener(OnPreparedListener listener);

    /**
     *Register a callback to be invoked the end of the media file has been reached during
     *playback.
     *
     * @param listener The callback that will be run
     */
    void setOnCompletionListener(OnCompletionListener listener);

    /**
     *Register a callback to be invoked when an error occurs during playback or setup.
     *
     * @param listener the callback that will be run
     */
    void setOnErrorListener(OnErrorListener listener);

    /**
     * Register a callback to be invoked when a seek operation has been completed.
     *
     * @param listener the callback that will be run
     */
    void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    /**
     * Register a callback to be invoked when an info/warning is available.
     *
     * @param listener the callback that will be run
     */
    void setOnInfoListener(OnInfoListener listener);

    /**
     * Register a callback to be invoked when the status of the network stream's buffer has changed.
     *
     * @param listener the callback that will be run.
     */
    void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

    /**
     *Register a callback to be invoked when the video size is known or upated.
     *
     * @param listener the callback that will be run
     */
    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);

    /**
     * Set video URI using specific headers.
     *
     * @param uri the Uri of the video. The content of Uri is specified by Content Providers.
     * @param headers the headers for the URI request.
     */
    void setVideoUri(Uri uri, Map<String, String> headers);

    /**
     * Start the video playback
     */
    void start();

    /**
     * Pause video playback
     */
    void pause();

    /**
     * Get the duration of the video
     *
     * @return the duration in milliseconds, if no duration is available
     * (for example, if streaming live content), -1 is returned.
     */
    int getDuration();

    /**
     *Gets the current playback position.
     * @return the current position in milliseconds
     */
    int getCurrentPosition();

    /**
     *Seeks to specified time position.
     *
     * @param msec the offset in milliseconds from the start to seek to
     */
    void seekTo(int msec);

    /**
     *Check whether the player is playing
     *
     * @return true if currently playing, false otherwise
     */
    boolean isPlaying();

    /**
     * Check whether the video can be paused.
     * @return true if the video can be paused, false otherwise
     */
    boolean canPause();

    /**
     * Check whether the video can seek backward
     *
     * @return true if the video can be seek backward, false otherwise
     */
    boolean canSeekBackward();

    /**
     * Check whether the video can seek forward
     *
     * @return true if the video can be seek forward, false otherwise
     */
    boolean canSeekForward();

    /**
     *Stop playback. Release media player and other resources.
     */
    void stopPlayback();

    /**
     * Set the current account information used to do VIP authentication.
     *
     * @param accountType the type of the account, e.g. xiaomi, google or facebook, etc.
     * @param accountToken the string represents other information about the account. The format and detailed
     * information is as per Content Provider's need. Json string is recommended
     */
    void setAccountInfo(String accountType, String accountToken);

    interface AdsPlayListener {
        /**
         * callback for ads play started
         */
        void onAdsPlayStart();

        /**
         * callback for ads play ends.
         */
        void onAdsPlayEnd();

        /**
         * callback to notify the remaining time of ads
         * @param leftSeconds specify the remaining ads time in seconds
         */
        void onAdsTimeUpdate(int leftSeconds);
    }

    interface OnPreparedListener {
        /**
         * Called when the media is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        void onPrepared(MediaPlayer mp);
    }

    interface OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        void onCompletion(MediaPlayer mp);
    }

    interface OnErrorListener {
        /**
         * Called to indicate an error.
         *
         * @param mp      the MediaPlayer the error pertains to
         * @param what    the type of error that has occurred:
         * @param extra   an extra code, specific to the error. Typically
         * implementation dependent.
         *
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the OnCompletionListener to be called.
         */
        boolean onError(MediaPlayer mp, int what, int extra);
    }

    interface OnSeekCompleteListener {
        /**
         * Called to indicate the completion of a seek operation.
         *
         * @param mp the MediaPlayer that issued the seek operation
         */
        void onSeekComplete(MediaPlayer mp);
    }

    interface OnInfoListener {
        /**
         * Called to indicate an info or a warning.
         *
         * @param mp      the MediaPlayer the info pertains to.
         * @param what    the type of info or warning. The following 3 info must be indicated:
         *   MEDIA_INFO_BUFFERING_START : MediaPlayer is temporarily pausing playback internally
         *                in order to buffer more data
         *   MEDIA_INFO_BUFFERING_END : MediaPlayer is resuming playback after filling buffers.
         *   MEDIA_INFO_VIDEO_RENDERING_START: MediaPlayer just pushed the very first video frame for rendering.
         *
         * @param extra an extra code, specific to the info. Typically
         * implementation dependent.
         *
         * @return True if the method handled the info, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the info to be discarded.
         */
        boolean onInfo(MediaPlayer mp, int what, int extra);
    }

    interface OnBufferingUpdateListener {
        /**
         * Called to update status in buffering a media stream received through
         * progressive HTTP download. The received buffering percentage
         * indicates how much of the content has been buffered or played.
         * For example a buffering update of 80 percent when half the content
         * has already been played indicates that the next 30 percent of the
         * content to play has been buffered.
         *
         * @param mp      the MediaPlayer the update pertains to
         * @param percent the percentage (0-100) of the content
         *                that has been buffered or played thus far
         */
        void onBufferingUpdate(MediaPlayer mp, int percent);
    }

    interface OnVideoSizeChangedListener {
        /**
         * Called to indicate the video size
         *
         * The video size (width and height) could be 0 if there was no video,
         * no display surface was set, or the value was not determined yet.
         *
         * @param mp        the MediaPlayer associated with this callback
         * @param width     the width of the video
         * @param height    the height of the video
         */
        void onVideoSizeChanged(MediaPlayer mp, int width , int height);
    }
}
