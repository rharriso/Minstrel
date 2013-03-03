package com.rharriso.minstrel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rharriso.minstrel.models.Artist;

public class ArtistsActivity extends Activity implements OnItemClickListener{

	ArrayList<Artist> mArtistList = new ArrayList<Artist>();
	ListView mArtistListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists);
		
		//load artists
		loadArtists();		
		//set up list
		mArtistListView = (ListView)findViewById(R.id.artist_list);
		mArtistListView.setAdapter(new ArtistsAdapter(this, R.layout.artist_list_row, mArtistList));
		mArtistListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_artists, menu);
		return true;
	}
	
	/**
	 * loads artists from MediaStore
	 */
	private void loadArtists(){

		//get artists ids and names
		String[] projection = { MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ARTIST_ID};
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(	MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
										projection, null, null, projection[0]	);
		
		if(cursor != null && cursor.moveToFirst()){
			int artistCol 	= cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int artistIdCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST_ID);
			HashSet<Long> artist_ids = new HashSet<Long>();
			
			do{
				//if this id has already been added, skip this round
				Long artist_id = cursor.getLong(artistIdCol);
				if(artist_ids.contains(artist_id)) continue;
				
				artist_ids.add(artist_id);
					
				Artist a = new Artist();
				a.setId(artist_id);				
				a.setName(cursor.getString(artistCol));
								
				mArtistList.add(a);
				
			}while(cursor.moveToNext());						
		}
	}
	
	/**
	 * responds to artist list click
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Toast.makeText(this, mArtistList.get(position).getName(), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * put those artists in a list
	 */
	private class ArtistsAdapter extends ArrayAdapter<Artist>{
		Context mContext;
		int mLayoutResourceId;
		List<Artist> mArtistList;
		
		public ArtistsAdapter(Context context, int layoutResourceId, List<Artist> artists) {
			super(context, layoutResourceId, artists);
			
			mContext 			= context;
			mLayoutResourceId	= layoutResourceId;
			mArtistList			= artists;
			
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View row = convertView;
			
			if (row == null){
				LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
				row = inflater.inflate(mLayoutResourceId, parent, false);
			}
			
			Artist artist = mArtistList.get(position);
			
			if(artist != null){
				TextView artistsNameTxt = (TextView)row.findViewById(R.id.artist_name);
				if(artistsNameTxt != null) artistsNameTxt.setText(artist.getName());
			}
			
			return row;
		}
	}
}