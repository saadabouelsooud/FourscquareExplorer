package com.saad.objects;

import java.util.ArrayList;
import java.util.List;


public class Checkin {

	
	private String id;
	
	private String createdAt;
	
	private String type;
	
	private Integer timeZoneOffset;
	
	private String user;
	
	private String venue;
	
	private Integer distance;
		
	private Boolean like;
	
	private String comments;
	
	private String score;
	
	private List<java.lang.Object> objectLeaderboards = new ArrayList<java.lang.Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public void setTimeZoneOffset(Integer timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Boolean getLike() {
		return like;
	}

	public void setLike(Boolean like) {
		this.like = like;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public List<java.lang.Object> getObjectLeaderboards() {
		return objectLeaderboards;
	}

	public void setObjectLeaderboards(List<java.lang.Object> objectLeaderboards) {
		this.objectLeaderboards = objectLeaderboards;
	}

}
