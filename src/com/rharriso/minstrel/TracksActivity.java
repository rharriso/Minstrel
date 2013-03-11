package com.rharriso.minstrel;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rharriso.minstrel.models.ModelListItem;
import com.rharriso.minstrel.models.Track;

public class TracksActivity extends Activity implements OnItemClickListener{

	private ArrayList<ModelListItem> mTrackList = new ArrayList<ModelListItem>();
	private ListView mTrackListView;
	private long mAlbumId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracks);
		
		//load tracks
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) mAlbumId = extras.getLong("album_id");
		loadTracks();
		//set up list
		mTrackListView = (ListView) findViewById(R.id.track_list);
		ModelListAdapter adapter = new ModelListAdapter(this, mTrackList);
		mTrackListView.setAdapter(adapter);
		mTrackListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tracks, menu);
		return true;
	}

	/**
	 * load albums from media store
	 */
	private void loadTracks(){
		String selectStr = null;
		String[] selectArgs = {""};
		
		if( mAlbumId > 0 ){
			selectStr = MediaStore.Audio.Media.ALBUM_ID+" = ?";
			selectArgs[0] = Long.toString(mAlbumId);
		}else{
			selectArgs = null;
		}
		
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
			HashSet<Long> trackIds = new HashSet<Long>();
			
			do{
				//if this has already been added skip this round
				Long id = cursor.getLong(idCol);
				if(trackIds.contains(id)) continue;
				
				trackIds.add(id);				
				
				Track t = new Track();
				t.setId(id);
				t.setTitle(cursor.getString(titleCol));
				t.setTitleKey(cursor.getString(titleKeyCol));
				t.setDuration(cursor.getLong(durationCol));
				
				mTrackList.add(t);
				
			}while(cursor.moveToNext());
			
			Log.d("FUCKING ALBUM", mTrackList.toString());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		Track track = (Track) mTrackList.get(position);
		
		Intent serviceIntent = new Intent(this, AudioPlayerService.class);
		serviceIntent.putExtra("track_key", track.getTitleKey());
		sendBroadcast(serviceIntent);
		
		Intent intent = new Intent();
		intent.setClass(this, PlayerActivity.class);
		startActivity(intent);
	}
}
