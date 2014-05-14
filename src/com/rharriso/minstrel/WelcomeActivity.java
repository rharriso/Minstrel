package com.rharriso.minstrel;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {

	MediaPlayer mPlayer = new MediaPlayer();
	Button mArtistsBtn = null;
	Button mAlbumsBtn = null;
    Button mBookmarksBtn = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.welcome);
        
        mArtistsBtn = (Button)findViewById(R.id.artists_btn);
        mArtistsBtn.setOnClickListener((View v) ->{
            loadActivity(ArtistsActivity.class);
        });
        mAlbumsBtn = (Button)findViewById(R.id.albums_btn);
        mAlbumsBtn.setOnClickListener((View v) ->{
            loadActivity(AlbumsActivity.class);
        });
        mBookmarksBtn = (Button)findViewById(R.id.bookmarks_btn);
        mBookmarksBtn.setOnClickListener((View v) ->{
            loadActivity(BookmarkListActivity.class);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    /**
     * Loads an activity into the mainview or whatever
     * @param activityClass
     */
    private void loadActivity(Class<?> activityClass){
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }
    
}
