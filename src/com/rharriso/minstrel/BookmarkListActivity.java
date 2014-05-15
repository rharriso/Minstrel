package com.rharriso.minstrel;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.rharriso.minstrel.models.Bookmark;
import com.rharriso.minstrel.models.ModelListItem;

import java.util.ArrayList;


public class BookmarkListActivity extends Activity implements OnItemClickListener {

    private ListView mBookMarkListView;
    private ArrayList<ModelListItem> mBookMarks;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load bookmarks;
        loadBookMarks();
        //set up list
        setContentView(R.layout.activity_bookmark_list);
        mBookMarkListView = (ListView) findViewById(R.id.bookmark_list);
        ModelListAdapter adapter = new ModelListAdapter(this, mBookMarks);
        mBookMarkListView.setAdapter(adapter);
        mBookMarkListView.setOnItemClickListener(this);
    }

    private void loadBookMarks() {
        ArrayList<Bookmark> bookmarks = Bookmark.getAll();
        mBookMarks = new ArrayList<ModelListItem>();

        for (Bookmark bookmark : bookmarks) {
            mBookMarks.add(bookmark);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Bookmark bookmark = (Bookmark) mBookMarks.get(position);

        Intent serviceIntent = new Intent(this, AudioPlayerService.class);
        serviceIntent.putExtra("track_key", bookmark.getTrackKey());
        serviceIntent.putExtra("album_id", bookmark.getAlbumId());
        serviceIntent.putExtra("track_position", bookmark.getPosition());
        startService(serviceIntent);

        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
}
