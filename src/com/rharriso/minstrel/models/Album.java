package com.rharriso.minstrel.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashSet;

public class Album implements ModelListItem{

	private String name;
	private Long id;
	
	/*
	 * Getters and setters
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return string description of Artist
	 */
	public String toString(){
		return Long.toString(this.id)+": "+this.name;
	}	
	
	/**
	 * @return true if other object has the same id
	 */
	public Boolean equals(Artist otherArtist){
		return this.id == otherArtist.getId();
	}
	
	public String getListTitle(){
		return this.name;
	}

    /**
     * @return ArrayList of tracks in album
     */
    public static ArrayList<ModelListItem> findTrackListWithKey(Context ctx, long albumId){
        ArrayList<ModelListItem> trackList = new ArrayList<ModelListItem>();

        String selectStr = null;
        String[] selectArgs = {""};

        if( albumId > 0 ){
            selectStr = MediaStore.Audio.Media.ALBUM_ID+" = ?";
            selectArgs[0] = Long.toString(albumId);
        }else{
            selectArgs = null;
        }

        //get album name and ids
        String[] projection = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TITLE_KEY,
                MediaStore.Audio.Media.DURATION };

        //search for albums for all artists or just the passed on
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selectStr, selectArgs, MediaStore.Audio.Media.TRACK);

        // create tracks from all the results
        if(cursor != null && cursor.moveToFirst()){
            int idCol		= cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleCol	= cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int titleKeyCol	= cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY);
            int durationCol	= cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY);
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

                trackList.add(t);

            }while(cursor.moveToNext());
        }

        return trackList;
    }

}
