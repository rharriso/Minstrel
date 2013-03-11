package com.rharriso.minstrel;

import java.io.IOException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.rharriso.minstrel.models.Track;

public class AudioPlayerService extends Service {
	
	private Track mTrack;	
	private MediaPlayer mPlayer = new MediaPlayer();
			
	public AudioPlayerService() {
	}

	/*
	 * Class for clients to access
	 */
	public class LocalBinder extends Binder {
		AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
	
	@Override
	public void onCreate() {
		super.onCreate();			
	}
	
	// This is the object that receives interactions from clients.  See
    private final IBinder mBinder = new LocalBinder();
    
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	@Override
    public void onDestroy() {
		mPlayer.release();
		mPlayer = new MediaPlayer();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * Load initial track
		 */
		if(intent != null){
			Bundle extras = intent.getExtras();
			loadTrack(extras.getString("track_key"));
			
			playTrack(mTrack);
			int startPosition = extras.getInt("track_position",-1);
			if(startPosition >= 0) seekTo(startPosition);
		}		
		
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
		return START_STICKY;
    }
    
	/**
	 * @category player interaction.
	 */
	
    /**
	 * @return current track position
	 */
	public int getCurrentPosition(){
		return mPlayer.getCurrentPosition();
	}
	
	/**
	 * @return current track
	 */
	public Track getCurrentTrack(){
		return mTrack;
	}
	
	/**
	 * set the current track position to set value
	 */
	public void seekTo(int newPosition){
		mPlayer.seekTo(newPosition);
	}
	

	/**
	 * toggles between play and pause on the mediaPlayer
	 */
	public void playToggle(){
		if(mPlayer.isPlaying())
			mPlayer.pause();
		else
			mPlayer.start();
			
	}
			
	/**
	 * @param track - track to be played
	 */
	public void playTrack(Track track){
		try{
			if(mPlayer.isPlaying()){
				mPlayer.release();
				mPlayer = new MediaPlayer();
				
			}
			
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(getApplicationContext(), track.getUri());
			mPlayer.prepare();
			mPlayer.start();        		
		} catch (IOException e) {
			Log.e("minstrel.WelcomActivity", e.toString());
		}
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
			mTrack.setAlbumName(cursor.getString(albumCol));
			mTrack.setAlbumName(cursor.getString(artistCol));
		}
	}
}
