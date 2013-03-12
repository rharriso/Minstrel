package com.rharriso.minstrel;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.rharriso.minstrel.models.Track;

public class AudioPlayerService extends Service{
	
	private Track mTrack;
	private Track mNextTrack;
	private MediaPlayer mPlayer;
			
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
		initMediaPlayer();
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
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * Load initial track
		 */
		if(intent != null){
			Bundle extras = intent.getExtras();
			
			//play track if key passed
			String trackKey = extras.getString("track_key");
			if(trackKey != null) playTrack(Track.findWithKey(this, trackKey));
						
			//go to track position if passed
			int startPosition = extras.getInt("track_position",-1);
			if(startPosition >= 0) 
				seekTo(startPosition);			
			
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
	 * @return current track position
	 */
	public int getDuration(){
		return mPlayer.getDuration();
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
		mTrack = track;
		mNextTrack = mTrack.nextTrack(this);
		
		try{
			if(mPlayer.isPlaying()){
				mPlayer.release();
				initMediaPlayer();
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
	 * initializes the media player
	 */
	private void initMediaPlayer(){
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(mNextTrack != null)
					playTrack(mNextTrack);				
			}
		});
	}
}
