package com.rharriso.minstrel;

import java.io.IOException;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.rharriso.minstrel.models.Track;

public class AudioPlayerService extends Service {
	
	private NotificationManager mNM;
	private int NOTIFICATION = R.string.audio_player_service_started;
	
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
		
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting  
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();		
	}
	
	@Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        
        //say its over
        Toast.makeText(this, "Service Ended", Toast.LENGTH_SHORT).show();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    
    /**
	 * plays current track
	 */
	public void playTrack(Track track){
		try{
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(getApplicationContext(), track.getUri());
			mPlayer.prepare();
			mPlayer.start();        		
		} catch (IOException e) {
			Log.e("minstrel.WelcomActivity", e.toString());
		}
	}
	
	/**
	 * @return current track position
	 */
	public int getCurrentPosition(){
		return mPlayer.getCurrentPosition();
	}
	
	/**
	 * set the current track position to set value
	 */
	public void seekTo(int newPosition){
		mPlayer.seekTo(newPosition);
	}
}
