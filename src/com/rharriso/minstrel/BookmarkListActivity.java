package com.rharriso.minstrel;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rharriso.minstrel.models.Bookmark;
import com.rharriso.minstrel.models.ModelListItem;


public class BookmarkListActivity extends Activity implements OnItemClickListener{

    private ListView mBookMarkListView;
    private ArrayList<ModelListItem> mBookMarks;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //load bookmarks;
        loadBookMarks();
        //set up list
        setContentView(R.layout.activity_bookmark_list);
        mBookMarkListView = (ListView)findViewById(R.id.bookmark_list);
        ModelListAdapter adapter = new ModelListAdapter(this, mBookMarks);
        mBookMarkListView.setAdapter(adapter);
        mBookMarkListView.setOnItemClickListener(this);                
    }

    private void loadBookMarks(){
       ArrayList<Bookmark> bookmarks = Bookmark.getAll();
       mBookMarks = new ArrayList<ModelListItem>();
       
       for (Bookmark bookmark : bookmarks) {
    	   mBookMarks.add(bookmark);
       }		
	}       
     
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		Bookmark bookmark = (Bookmark)mBookMarks.get(position);
	
		Intent intent = new Intent();
		intent.setClass(this, PlayerActivity.class);		
		intent.putExtra("track_key", bookmark.getTrackKey());
		intent.putExtra("track_position", bookmark.getPosition());
		
		startActivity(intent);		
	}	
}
