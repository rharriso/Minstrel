package com.rharriso.minstrel;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rharriso.minstrel.models.Album;
import com.rharriso.minstrel.models.ModelListItem;

public class AlbumsActivity extends Activity implements OnItemClickListener{

	private ArrayList<ModelListItem> mAlbumList = new ArrayList<ModelListItem>();
	private ListView mAlbumListView;
	private long mArtistId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albums);
		
		//load albums
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) mArtistId = extras.getLong("artist_id");
		loadAlbums();
		//set up list
		mAlbumListView = (ListView) findViewById(R.id.album_list);
		ModelListAdapter adapter = new ModelListAdapter(this, R.layout.image_list_item, mAlbumList);
		mAlbumListView.setAdapter(adapter);
		mAlbumListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_albums, menu);
		return true;
	}

	/**
	 * load albums from media store
	 */
	private void loadAlbums(){
		String selectStr = null;
		String[] selectArgs = {""};
		
		if( mArtistId > 0 ){
			selectStr = MediaStore.Audio.Media.ARTIST_ID+" = ?";
			selectArgs[0] = Long.toString(mArtistId);
		}else{
			selectArgs = null;
		}
		
		//get album name and ids
		String[] projection = { MediaStore.Audio.Media.ALBUM_ID,
								MediaStore.Audio.Media.ALBUM };
		
		//search for albums for all artists or just the passed on
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							projection, selectStr, selectArgs, projection[1]);
		
		if(cursor != null && cursor.moveToFirst()){
			int albumCol 	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			int albumIdCol  = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
			HashSet<Long> albumIds = new HashSet<Long>();
			
			do{
				//if this has already been added skip this round
				Long albumId = cursor.getLong(albumIdCol);
				if(albumIds.contains(albumId)) continue;
				
				albumIds.add(albumId);				
				
				Album a = new Album();
				a.setId(albumId);
				a.setName(cursor.getString(albumCol));
				
				mAlbumList.add(a);
				
			}while(cursor.moveToNext());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Album album = (Album)mAlbumList.get(position);
		
		Intent intent = new Intent();
		intent.setClass(this, TracksActivity.class);		
		intent.putExtra("album_id", album.getId());
		
		startActivity(intent);		
	}
}
