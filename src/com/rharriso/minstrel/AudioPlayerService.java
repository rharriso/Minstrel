package com.rharriso.minstrel;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.rharriso.minstrel.models.Album;
import com.rharriso.minstrel.models.ModelListItem;
import com.rharriso.minstrel.models.Track;

public class AudioPlayerService extends Service{

    private long mAlbumId;
    private ArrayList<ModelListItem> mTrackList;
	private Track mTrack;
	private Track mNextTrack;
	private MediaPlayer mPlayer;
    private int currTrackIndex;
			
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
			
			// get tack key
			String trackKey = extras.getString("track_key");
            // get tracks for passed album
            mAlbumId = extras.getLong("album_id", -1);
            if(mAlbumId < 0){
               throw new Error("Invalid album id passed");
            }

            // load list and play track
            mTrackList = Album.findTrackListWithKey(this, mAlbumId);
            playTrack(getTrackIndex(trackKey));

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
     *  @return Long integer of album id
     */
    public Long getCurrentAlbumId(){
        return mAlbumId;
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
     * plays the next track
     */
    public void playNext(){
        playTrack(currTrackIndex+1);
    }

    /**
     * plays the next track
     */
    public void playPrev(){
        playTrack(currTrackIndex-1);
    }

    /**
     * @param titleKey
     * @return integer of index of track with key passed
     */
    private int getTrackIndex(String titleKey){
        int index = 0;
        for( ModelListItem track : mTrackList ){
            String trackKey = ((Track) track).getTitleKey();
            if(titleKey.equals(trackKey))
                break;
            else
                index++;
        }
        return index;
    }

	/**
	 * @param index - index of track to be played
	 */
	private void playTrack(int index){
        // don't go out of bounds
        if(index < 0 || index > mTrackList.size()-1) return;

        currTrackIndex = index;
        mTrack = (Track) mTrackList.get(currTrackIndex);
		
		try{
			if(mPlayer != null){
				mPlayer.release();
				initMediaPlayer();
			}
			
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(getApplicationContext(), mTrack.getUri());
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
		mPlayer.setLooping(false);
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(mNextTrack != null)
					playTrack(currTrackIndex+1);
			}
		});
	}
}
