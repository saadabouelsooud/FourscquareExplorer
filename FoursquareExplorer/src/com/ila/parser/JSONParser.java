package com.ila.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.saad.foursquareexplorer.FoursquareExplorer;
import com.saad.objects.Checkin;
import com.saad.objects.VenueData;

import android.content.Context;
import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static JSONObject venue = null;
	static JSONArray venues = null;
	static JSONObject checkIn = null, photo;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	public static Object getJSONArrFromUrl(String url, String method, Context c) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// //////////////// load venues get///////////////////////////////////
		if (method.equalsIgnoreCase("get")) {
			try {
                System.out.println("venues url = "+url);
				doHttpGetRequest(url, httpClient);

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			responseToString();
			// try parse the string to a JSON object
			try {
				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
				return false;
			}

			venueJsonParsing(c);
	        FoursquareExplorer.loadVenuesPhotosFromMemory();
				return true;
		}
		// /////// check-in post
		// ////////////////////////////////////////////////
		else {
			try {

				doHttpPostRequest(url, httpClient);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			responseToString();

			return chekInJsonParsing();
		}
	}

	private static Checkin chekInJsonParsing() {
		// try parse the string to a JSON object
		Checkin lastCheckIn=null;
		try {
			jObj = new JSONObject(json);
			checkIn = jObj.getJSONObject("response").getJSONObject("checkin");
			System.out.println(checkIn.length());
			for (int i = 0; i < checkIn.length(); i++) {
				JSONObject checkInObject = venues.getJSONObject(i);
				lastCheckIn = new Checkin();
				lastCheckIn.setId(checkInObject.getString("referralId"));
				FoursquareExplorer.mAllCheckIns.add(lastCheckIn);
			}
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return null;
		}
		return lastCheckIn;
	}

	private static void doHttpPostRequest(String url,
			DefaultHttpClient httpClient) throws IOException,
			ClientProtocolException {
		HttpPost httpPost;
		HttpResponse httpResponse;
		httpPost = new HttpPost(url);
		httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
	}

	private static boolean venueJsonParsing(Context c) {
		try {
			// make new object from venues list to clear old and save updated
			// ones
			FoursquareExplorer.mAllVenues = new ArrayList<VenueData>();
			venues = jObj.getJSONObject("response").getJSONArray("groups");
			venues = venues.getJSONObject(0).getJSONArray("items");
			System.out.println(venues.length());
			for (int i = 0; i < venues.length(); i++) {
				JSONObject venueObject = venues.getJSONObject(i).getJSONObject(
						"venue");
				VenueData venue = new VenueData();
				JSONObject locationData = new JSONObject(
						venueObject.getString("location"));
				venue.setId(venueObject.getString("id"));
				venue.setName(venueObject.getString("name"));
				String iconURL = getVenuePhoto(
						"https://api.foursquare.com/v2/venues/"
								+ venueObject.getString("id")
								+ "/photos?oauth_token="
								+ FoursquareExplorer.access_token
								+ "&v=20140814&limit=1",
						venueObject.getString("name"), c);
				if (!iconURL.equals("false")) {
					venue.setPhotURL(iconURL);
					venue.setPhotoSDCardUrl(FoursquareExplorer.SdCardImagesUrl
							+ venueObject.getString("name") + ".jpg");
				}
				venue.setLat(Double.parseDouble(locationData.getString("lat")));
				venue.setLng(Double.parseDouble(locationData.getString("lng")));
				FoursquareExplorer.mAllVenues.add(venue);
			}
		} catch (JSONException e) {
			// TODO Auenerated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void doHttpGetRequest(String url,
			DefaultHttpClient httpClient) throws IOException,
			ClientProtocolException {
		HttpGet httpGet;
		HttpResponse httpResponse;
		httpGet = new HttpGet(url);
		httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
	}

	private static String getVenuePhoto(String url, String venueName, Context c) {
		// TODO Auto-generated method stub
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String photoUrl = null;
		try {

			doHttpGetRequest(url, httpClient);

		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}

		responseToString();

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return "false";
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
			photo = jObj.getJSONObject("response").getJSONObject("photos");
			JSONObject photoSource = photo.getJSONArray("items").getJSONObject(
					0);
			photoUrl = photoSource.getString("prefix")
					+ photoSource.getString("width") + "x"
					+ photoSource.getString("height")
					+ photoSource.getString("suffix");
			System.out.println("photoUrl = " + photoUrl);
			if(FoursquareExplorer.getBitmapFromURL(photoUrl, venueName, c)!=null)
				return "true";
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return "false";
		}
		return photoUrl;
	}

	private static void responseToString() {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
	}
}
