package com.rharriso.minstrel;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements OnClickListener {

	MediaPlayer mPlayer = new MediaPlayer();
	Button mAddBookmarkBtn = null;
	TextView mBookMarkList = null;
	String mCurrTrackTitle = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.welcome);
        
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
        										null, null, null, null);
        
        mBookMarkList   = (TextView)findViewById(R.id.bookmark_list);
        mAddBookmarkBtn = (Button)findViewById(R.id.add_bookmark_btn);
        mAddBookmarkBtn.setOnClickListener(this);
        
        //fuck null shit
        Log.d("FUCK", "THAT SHIT");
        if(cursor == null || !cursor.moveToFirst()){
        	Log.d("FUCK", "THAT SHIT");        	
        }else{
        	int titleCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
        	int idCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
        	
        	cursor.move(188);
        	
        	mCurrTrackTitle = cursor.getString(titleCol);
    		
    		long id= cursor.getLong(idCol);
    		Uri uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    		Log.d("URI", uri.toString());
    	    	
    		try {
    			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    			mPlayer.setDataSource(getApplicationContext(), uri);
    			mPlayer.prepare();
    			mPlayer.start();        		
			} catch (IOException e) {
				Log.e("minstrel.WelcomActivity", e.toString());
			}
    		        	
        }
        
     
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		String entry = mCurrTrackTitle+": "+mPlayer.getCurrentPosition()+" / "+mPlayer.getDuration();
		mBookMarkList.append(entry+"\n");		
	}
    
}
