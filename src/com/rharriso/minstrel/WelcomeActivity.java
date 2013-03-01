package com.rharriso.minstrel;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        MediaPlayer player = new MediaPlayer();
        
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
        										null, null, null, null);
        
        //fuck null shit
        Log.d("FUCK", "THAT SHIT");
        if(cursor == null || !cursor.moveToFirst()){
        	Log.d("FUCK", "THAT SHIT");        	
        }else{
        	int titleCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
        	int idCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
        	do{
        		String title = cursor.getString(titleCol);
        		Log.d("Title:", title);
        		
        		long id= cursor.getLong(idCol);
        		Log.d("Id:", Long.toString(id));
        		
        		Uri uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        		Log.d("URI", uri.toString());
        		
        	}while(cursor.moveToNext());
        }
        	
        
        
        
        setContentView(R.layout.welcome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }
    
}
