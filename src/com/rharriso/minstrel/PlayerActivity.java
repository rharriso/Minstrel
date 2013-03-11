package com.rharriso.minstrel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.rharriso.minstrel.models.Bookmark;
import com.rharriso.minstrel.models.Track;

public class PlayerActivity extends Activity implements OnClickListener, OnSeekBarChangeListener{

	private AudioPlayerService mPlayerService;
	private Boolean mIsBound = false;
	
	private Button mBookmarkButton;
	private Button mPausePlayButton;
	private SeekBar mTrackSeekBar;
	
	private Track mTrack;
	private String mArtistName;
	private String mAlbumName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		/*
		 * Create audio service binding and play first tracks
		 */
		//load audio service
		doBindService();

		
		/*
		 * Add button actions
		 */
		mBookmarkButton = (Button)findViewById(R.id.bookmark_btn);
		mBookmarkButton.setOnClickListener(this);
		mPausePlayButton = (Button)findViewById(R.id.pause_play_btn);
		mPausePlayButton.setOnClickListener(this);

		/*
		 * Seek bar action
		 */
		mTrackSeekBar = (SeekBar)findViewById(R.id.track_seek_bar);
		mTrackSeekBar.setOnSeekBarChangeListener(this);
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_player, menu);
		return true;
	}	
	
	/**
	 * @category service connection binding
	 */
	
	private ServiceConnection mConnection = new ServiceConnection(){
		public void onServiceConnected(ComponentName className, IBinder service) {
			mPlayerService = ((AudioPlayerService.LocalBinder)service).getService();
			Log.d("FUUUUUUCK", "service bound "+service.toString());
		}
		
		public void onServiceDisconnected(ComponentName className) {
			 
		}
	};
	
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
	}

	/**
	 * @category UI Events
	 */
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mBookmarkButton){
			Bookmark bookmark = new Bookmark();
			bookmark.setTrackName(mTrack.getTitle());
			bookmark.setTrackKey(mTrack.getTitleKey());
			bookmark.setAlbumName(mAlbumName);
			bookmark.setArtistName(mArtistName);
			bookmark.setPosition(mPlayerService.getCurrentPosition());
			bookmark.save();			
		
		}else if(v == mPausePlayButton){
			mPlayerService.playToggle();
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.d("Progress Bar", Integer.toString(progress));		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
