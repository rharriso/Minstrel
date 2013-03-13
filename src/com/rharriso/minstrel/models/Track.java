package com.rharriso.minstrel.models;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Track implements ModelListItem{

	private String title;
	private String titleKey;
	private String albumName;
	private String artistName;
	private Long id;
	private Uri uri;
	private Long duration;
	
	/*
	 * Getters and setters
	 */
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleKey() {
		return titleKey;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setAtristName(String aristName) {
		this.artistName = aristName;
	}
	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
		this.uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
	}
	public Uri getUri() {
		return uri;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}	
	
	/**
	 * @return string description of Artist
	 */
	public String toString(){
		return Long.toString(this.id)+": "+this.title;
	}	
	
	/**
	 * @return true if other object has the same id
	 */
	public Boolean equals(Artist otherArtist){
		return this.id == otherArtist.getId();
	}
	
	public String getListTitle(){
		return this.title;
	}
	
	/**
	 * finds next track in album 
	 * @return Track or null
	 */
	public Track nextTrack(Context ctx){
		Track nextTrack = null;
		
		//set up selection
		String selectStr = MediaStore.Audio.Media.ALBUM+" = ?";
		String[] selectArgs = { albumName };
		
		//search for albums for all artists or just the passed on		
		Cursor cursor = Track.queryTracks(ctx, selectStr, selectArgs);
		
		if(cursor.moveToFirst()){
			int keyCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY);
			do{
				//compare title keys for match 
				if( titleKey.equals(cursor.getString(keyCol)) )
					if(cursor.moveToNext())
						nextTrack = trackFromCursor(cursor);
					else 
						return null;
				
			}while(cursor.moveToNext());
		}
		
		return nextTrack;
	}
	
	/**
	 * finds track that matches passed key
	 * @return Track or null 
	 */
	public static Track findWithKey(Context ctx, String trackKey){
		Track track = null;
		
		//set up selection
		String selectStr = MediaStore.Audio.Media.TITLE_KEY+" = ?";
		String[] selectArgs = { trackKey };
		
		//search for albums for all artists or just the passed on		
		Cursor cursor = queryTracks(ctx, selectStr, selectArgs);
		
		if(cursor != null && cursor.moveToFirst())
			track = trackFromCursor(cursor);
		
		return track;
	}
	
	private static Cursor queryTracks(Context ctx, String selectStr, String[] selectArgs){
		//get album name and ids
		String[] projection = { MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.TITLE,
								MediaStore.Audio.Media.TITLE_KEY,
								MediaStore.Audio.Media.ARTIST,
								MediaStore.Audio.Media.ALBUM,
								MediaStore.Audio.Media.DURATION };
		
		ContentResolver contentResolver = ctx.getContentResolver();
		return contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection, selectStr, selectArgs, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	}
	
	/**
	 * @param cursor
	 * @return Track filled with values from the cursor position
	 */
	private static Track trackFromCursor(Cursor cursor){
		int idCol		= cursor.getColumnIndex(MediaStore.Audio.Media._ID);
		int titleCol	= cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int titleKeyCol	= cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY);
		int durationCol	= cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
		int artistCol	= cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		int albumCol	= cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
		
		Track track = new Track();
		track.setId(cursor.getLong(idCol));
		track.setTitle(cursor.getString(titleCol));
		track.setTitleKey(cursor.getString(titleKeyCol));
		track.setDuration(cursor.getLong(durationCol));
		track.setAlbumName(cursor.getString(albumCol));
		track.setAtristName(cursor.getString(artistCol));
		
		return track;
	}
}
