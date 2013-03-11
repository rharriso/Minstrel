package com.rharriso.minstrel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity implements OnClickListener {

	MediaPlayer mPlayer = new MediaPlayer();
	Button mArtistsBtn = null;
	Button mAlbumsBtn = null;
    Button mBookmarksBtn = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.welcome);
        
        mArtistsBtn = (Button)findViewById(R.id.artists_btn);
        mArtistsBtn.setOnClickListener(this);
        mAlbumsBtn = (Button)findViewById(R.id.albums_btn);
        mAlbumsBtn.setOnClickListener(this);
        mBookmarksBtn = (Button)findViewById(R.id.bookmarks_btn);
        mBookmarksBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

	@Override
	public void onClick(View v) {		
		Intent intent = new Intent();
		Class<?> activityClass = null;	
		
		if(v == mArtistsBtn)	activityClass = ArtistsActivity.class;
		if(v == mAlbumsBtn)		activityClass = AlbumsActivity.class;
        if(v == mBookmarksBtn)  activityClass = BookmarkListActivity.class;
		
        if(activityClass != null){
        	intent.setClass(this, activityClass);
        	startActivity(intent);
        }		
	}
    
}
