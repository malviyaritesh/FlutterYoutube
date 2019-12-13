package io.github.ponnamkarthik.flutteryoutube;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import 	java.lang.Exception;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer youTubePlayer;

    private String API_KEY = "";
    private String videoId = "";
    private boolean isFullScreen = false;
    private boolean autoPlay = false;
    private boolean goFullScreen = false;
    private boolean showFullScreenButton = true;
    private int appBarColor;
    private int backgroundColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        appBarColor = getIntent().getIntExtra("appBarColor" , 0xFF424242);
        backgroundColor = getIntent().getIntExtra("backgroundColor" , 0xFF1b1b1b);

        try {
            ActionBar actionBar = getActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(appBarColor));

            final RelativeLayout rootView = (RelativeLayout) findViewById(R.id.root_view);
            rootView.setBackgroundColor(backgroundColor);

            getWindow().getDecorView().setBackgroundColor(backgroundColor);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch(Exception e) {
            e.printStackTrace();
        }

        API_KEY = getIntent().getStringExtra("api");
        videoId = getIntent().getStringExtra("videoId");
        goFullScreen = getIntent().getBooleanExtra("fullScreen", false);
        autoPlay = getIntent().getBooleanExtra("autoPlay", false);
        showFullScreenButton = getIntent().getBooleanExtra("showFullScreenButton", true);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(Provider provider, final YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer = player;

            if(autoPlay) {
                player.loadVideo(videoId);
            } else {
                player.cueVideo(videoId);
            }

            player.setManageAudioFocus(true);

            player.setFullscreen(goFullScreen);

            if(goFullScreen) {
                isFullScreen = goFullScreen;
            }

            player.setShowFullscreenButton(showFullScreenButton);

            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                    isFullScreen = b;
                }
            });

            player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                @Override
                public void onPlaying() {

                }

                @Override
                public void onPaused() {

                }

                @Override
                public void onStopped() {

                }

                @Override
                public void onBuffering(boolean b) {

                }

                @Override
                public void onSeekTo(int i) {

                }
            });

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {
                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    Intent intent = new Intent();
                    intent.putExtra("done", 0);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });

        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(isFullScreen && youTubePlayer != null) {
                youTubePlayer.setFullscreen(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
