package com.rharriso.minstrel;

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
import com.rharriso.minstrel.models.Artist;
import com.rharriso.minstrel.models.ModelListItem;

import java.util.ArrayList;
import java.util.HashSet;

public class ArtistsActivity extends Activity implements OnItemClickListener {

    private ArrayList<ModelListItem> mArtistList = new ArrayList<ModelListItem>();
    private ListView mArtistListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        //load artists
        loadArtists();
        //set up list
        mArtistListView = (ListView) findViewById(R.id.artist_list);
        ModelListAdapter adapter = new ModelListAdapter(this, R.layout.image_list_item, mArtistList);
        mArtistListView.setAdapter(adapter);
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
    private void loadArtists() {

        //get artists ids and names
        String[] projection = {MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST};

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, projection[1]);

        if (cursor != null && cursor.moveToFirst()) {
            int artistCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int artistIdCol = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST_ID);
            HashSet<Long> artistIds = new HashSet<Long>();

            do {
                //if this id has already been added, skip this round
                Long artistId = cursor.getLong(artistIdCol);
                if (artistIds.contains(artistId)) continue;

                artistIds.add(artistId);

                Artist a = new Artist();
                a.setId(artistId);
                a.setName(cursor.getString(artistCol));

                mArtistList.add(a);

            } while (cursor.moveToNext());
        }
    }

    /**
     * responds to artist list click
     */
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Artist artist = (Artist) mArtistList.get(position);

        Intent intent = new Intent();
        intent.setClass(this, AlbumsActivity.class);
        intent.putExtra("artist_id", artist.getId());

        startActivity(intent);
    }


}