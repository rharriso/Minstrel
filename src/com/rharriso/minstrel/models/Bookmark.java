package com.rharriso.minstrel.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.rharriso.minstrel.util.TimeFormatter;

import java.util.ArrayList;

@Table(name = "Bookmarks")
public class Bookmark extends Model implements ModelListItem{

	@Column(name = "track_key")
	private String trackKey;
	@Column(name = "track_name")
	private String trackName;
	@Column(name = "position")
	private int position;	
	@Column(name = "album_name")
	private String albumName;
    @Column(name = "album_id")
    private Long albumId;
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
    public Long getAlbumId() {
        return albumId;
    }
    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	
	/*
	 * @returns ArrayList<Bookmark> of all Bookmarks in the database 
	 */
	public static ArrayList<Bookmark> getAll(){
		return new Select().from(Bookmark.class).execute();
	}

	/*
		List stuffs
	*/
	public String getListTitle(){
		return artistName+" ("+albumName+")"+trackName+" - "+TimeFormatter.MillesecondsToTimestamp(position);
	}
}
