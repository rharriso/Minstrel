package com.rharriso.minstrel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.rharriso.minstrel.models.Album;
import com.rharriso.minstrel.models.ModelListItem;
import com.rharriso.minstrel.models.Track;

import java.util.ArrayList;

public class TracksActivity extends Activity implements OnItemClickListener {

    private ArrayList<ModelListItem> mTrackList;
    private ListView mTrackListView;
    private long mAlbumId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        // load tracks
        Bundle extras = getIntent().getExtras();
        if (extras != null) mAlbumId = extras.getLong("album_id");
        mTrackList = Album.findTrackListWithKey(this, mAlbumId);

        // add play all track
        Track playAll = new Track();
        playAll.setId((-1L));
        playAll.setTitle("Play All");
        playAll.setTitleKey(null);
        mTrackList.add(0, playAll);

        // set up list
        mTrackListView = (ListView) findViewById(R.id.track_list);
        ModelListAdapter adapter = new ModelListAdapter(this, R.layout.image_list_item, mTrackList);
        mTrackListView.setAdapter(adapter);
        mTrackListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_tracks, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        Track track = (Track) mTrackList.get(position);

        Intent serviceIntent = new Intent(this, AudioPlayerService.class);
        serviceIntent.putExtra("track_key", track.getTitleKey());
        serviceIntent.putExtra("album_id", mAlbumId);
        startService(serviceIntent);

        Intent intent = new Intent();
        intent.setClass(this, PlayerActivity.class);
        startActivity(intent);
    }
}
