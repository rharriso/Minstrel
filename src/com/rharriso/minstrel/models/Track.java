package com.rharriso.minstrel.models;

import android.content.ContentUris;
import android.net.Uri;

public class Track implements ModelListItem{

	private String title;
	private String titleKey;
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
}
