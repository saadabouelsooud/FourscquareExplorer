package com.saad.objects;
import java.util.ArrayList;
import java.util.List;
public class VenueData {

private String id;

private String name;

private Double lng;

private Double lat;

private String photURL,photoSDCardUrl;

public VenueData()
{
	
}
public VenueData(String id,String name,String lng,String lat,String photourl,String photosdcardurl)
{
	this.id=id;
	this.name=name;
	this.lng=Double.parseDouble(lng);
	this.lat=Double.parseDouble(lat);
	this.photURL=photourl;
	this.photoSDCardUrl=photosdcardurl;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getPhotoSDCardUrl() {
	return photoSDCardUrl;
}

public void setPhotoSDCardUrl(String photoSDCardUrl) {
	this.photoSDCardUrl = photoSDCardUrl;
}

public String getPhotURL() {
	return photURL;
}

public void setPhotURL(String photURL) {
	this.photURL = photURL;
}



public Double getLng() {
return lng;
}

public void setLng(Double lng) {
this.lng = lng;
}

public Double getLat() {
return lat;
}

public void setLat(Double lat) {
this.lat = lat;
}
}