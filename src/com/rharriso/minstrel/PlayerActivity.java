package com.rharriso.minstrel;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

import com.rharriso.minstrel.models.Track;

public class PlayerActivity extends Activity {

	private MediaPlayer mPlayer = new MediaPlayer();
	private Track mTrack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		
		
		/*
		 * Load initialize track
		 */
		Bundle extras = getIntent().getExtras();
		loadTrack(extras.getLong("track_id"));
		playTrack();
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
	private void loadTrack(long trackId){
		mTrack = new Track();
		
		String selectStr = MediaStore.Audio.Media._ID+" = ?";
		String[] selectArgs = { Long.toString(trackId) };
		
		//get album name and ids
		String[] projection = { MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.TITLE,
								MediaStore.Audio.Media.TITLE_KEY,
								MediaStore.Audio.Media.DURATION };
				
		//search for albums for all artists or just the passed on
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
												projection, selectStr, selectArgs, MediaStore.Audio.Media.TRACK);
				
		if(cursor != null && cursor.moveToFirst()){
			int idCol		= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int titleCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int titleKeyCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE_KEY);
			int durationCol	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE_KEY);
			
			mTrack.setId(cursor.getLong(idCol));
			mTrack.setTitle(cursor.getString(titleCol));
			mTrack.setTitleKey(cursor.getString(titleKeyCol));
			mTrack.setDuration(cursor.getLong(durationCol));
		}
	}
	
	/**
	 * plays current track
	 */
	private void playTrack(){
		try{
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(getApplicationContext(), mTrack.getUri());
			mPlayer.prepare();
			mPlayer.start();        		
		} catch (IOException e) {
			Log.e("minstrel.WelcomActivity", e.toString());
		}
	}
}
