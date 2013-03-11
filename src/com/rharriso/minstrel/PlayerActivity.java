package com.rharriso.minstrel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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
	private SeekBar mTrackSeekBar;
	
	private Track mTrack;
	private String mArtistName;
	private String mAlbumName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		/*
		 * Load initialize track
		 */
		Bundle extras = getIntent().getExtras();
		loadTrack(extras.getString("track_key"));
		
		/*
		 * Create audio service binding and play first tracks
		 */
		//load audio service
		doBindService();
		//play somethign on it
		mPlayerService.playTrack(mTrack);
		int startPosition = extras.getInt("track_position",-1);
		if(startPosition >= 0) mPlayerService.seekTo(startPosition);
		
		/*
		 * Add button actions
		 */
		mBookmarkButton = (Button)findViewById(R.id.bookmark_btn);
		mBookmarkButton.setOnClickListener(this);

		/*
		 * Seek bar action
		 */
		mTrackSeekBar = (SeekBar)findViewById(R.id.track_seek_bar);
		mTrackSeekBar.setOnSeekBarChangeListener(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_player, menu);
		return true;
	}

	/**
	 * @return track from id
	 */
	private void loadTrack(String trackKey){
		mTrack = new Track();
		
		String selectStr = MediaStore.Audio.Media.TITLE_KEY+" = ?";
		String[] selectArgs = { trackKey };
		
		//get album name and ids
		String[] projection = { MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.TITLE,
								MediaStore.Audio.Media.TITLE_KEY,
								MediaStore.Audio.Media.ARTIST,
								MediaStore.Audio.Media.ALBUM,
								MediaStore.Audio.Media.DURATION };
				
		//search for albums for all artists or just the passed on
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
												projection, selectStr, selectArgs, MediaStore.Audio.Media.TRACK);
				
		if(cursor != null && cursor.moveToFirst()){
			int idCol		= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int titleCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int titleKeyCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE_KEY);
			int durationCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			int artistCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int albumCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			
			mTrack.setId(cursor.getLong(idCol));
			mTrack.setTitle(cursor.getString(titleCol));
			mTrack.setTitleKey(cursor.getString(titleKeyCol));
			mTrack.setDuration(cursor.getLong(durationCol));
			mAlbumName = cursor.getString(albumCol);
			mArtistName = cursor.getString(artistCol);
		}
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
		
	    bindService(new Intent(this, 
	            AudioPlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
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
