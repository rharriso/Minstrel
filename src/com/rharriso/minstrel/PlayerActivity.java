package com.rharriso.minstrel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.rharriso.minstrel.models.Bookmark;
import com.rharriso.minstrel.models.Track;
import com.rharriso.minstrel.util.TimeFormatter;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends Activity implements OnSeekBarChangeListener {

    private AudioPlayerService mPlayerService = null;
    /**
     * @category service connection binding
     */

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlayerService = ((AudioPlayerService.LocalBinder) service).getService();
            mTrackSeekBar.setMax(mPlayerService.getDuration());
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };
    private Boolean mIsBound = false;
    private Timer mTimer = null;
    private Button mBookmarkButton,
            mPausePlayButton,
            mPrevButton,
            mNextButton;
    private SeekBar mTrackSeekBar;
    private TextView mTimeStamp,
            mTrackTitle;
    private Boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

		/*
         * Create audio service binding and play first tracks
		 */
        //load audio service
        doBindService();

        // buttons
        // bookmark
        mBookmarkButton = (Button) findViewById(R.id.bookmark_btn);
        mBookmarkButton.setOnClickListener((View v) -> this.bookmarkTrack());
        // pause play button
        mPausePlayButton = (Button) findViewById(R.id.pause_play_btn);
        mPausePlayButton.setOnClickListener((View v) -> mPlayerService.playToggle());
        // next button
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener((View v) -> mPlayerService.playNext());
        // prev button
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener((View v) -> mPlayerService.playPrev());

        // seek bar
        mTrackSeekBar = (SeekBar) findViewById(R.id.track_seek_bar);
        mTrackSeekBar.setOnSeekBarChangeListener(this);

        //text views
        mTimeStamp = (TextView) findViewById(R.id.time_stamp);
        mTrackTitle = (TextView) findViewById(R.id.track_title);

        //check the time relatively often
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTrackInfo();
                    }
                });
            }
        }, 0, 1000);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_player, menu);
        return true;
    }

    void doBindService() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        if (mTimer != null) mTimer.cancel();
        mTimer.purge();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isSeeking) mPlayerService.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeeking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeeking = false;
    }

    /**
     * Pull track info from service
     */
    protected void updateTrackInfo() {
        if (mPlayerService == null) return;

        int currentPosition = mPlayerService.getCurrentPosition();
        mTimeStamp.setText(TimeFormatter.MillesecondsToTimestamp(currentPosition));
        mTrackSeekBar.setProgress(currentPosition);

        Track currentTrack = mPlayerService.getCurrentTrack();
        StringBuffer titleBuffer = new StringBuffer();
        titleBuffer.append(currentTrack.getAlbumName());
        titleBuffer.append(": ");
        titleBuffer.append(currentTrack.getTitle());
        mTrackTitle.setText(titleBuffer.toString());

    }

    /**
     * create book make of current track position
     */
    private void bookmarkTrack() {
        Track track = mPlayerService.getCurrentTrack();
        Bookmark bookmark = new Bookmark();
        bookmark.setTrackName(track.getTitle());
        bookmark.setTrackKey(track.getTitleKey());
        bookmark.setAlbumName(track.getAlbumName());
        bookmark.setArtistName(track.getArtistName());
        bookmark.setAlbumId(mPlayerService.getCurrentAlbumId());
        bookmark.setPosition(mPlayerService.getCurrentPosition());
        bookmark.save();
    }
}
