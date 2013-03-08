package com.rharriso.minstrel.models;

import java.util.ArrayList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Bookmarks")
public class Bookmark extends Model{

	@Column(name = "track_key")
	private String trackKey;
	@Column(name = "position")
	private int position;
	@Column(name = "track_name")
	private String trackName;
	@Column(name = "album_name")
	private String albumName;
	@Column(name = "artist_name")
	private String artistName;
	
	public String getTrackKey() {
		return trackKey;
	}
	public void setTrackKey(String trackKey) {
		this.trackKey = trackKey;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
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
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	
	
	public static ArrayList<Bookmark> getAll(){
		return new Select().from(Bookmark.class).execute();
	}
}