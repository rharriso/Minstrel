package com.rharriso.minstrel.models;

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
}
