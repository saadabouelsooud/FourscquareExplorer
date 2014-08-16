package com.saad.foursquareexplorer;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ila.parser.JSONParser;
import com.saad.objects.Checkin;
import com.saad.sqLiteDB.DatabaseHandler;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener, OnInfoWindowClickListener {

	DatabaseHandler db = new DatabaseHandler(this);
	ProgressDialog ringProgressDialog;
	////////////////////// info window adapter class///////////////////////////
	class MyInfoWindowAdapter implements InfoWindowAdapter{

        private final View myContentsView;
  
  MyInfoWindowAdapter(){
   myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
  }
  
  @Override
  public View getInfoContents(Marker marker) {
	  
	        ImageView image=(ImageView)myContentsView.findViewById(R.id.image);
	        if(!marker.getSnippet().equals("me"))
	        {
	        	File imgFile = new  File(FoursquareExplorer.SdCardImagesUrl+marker.getTitle()+".jpg");
	        	if(imgFile.exists()){
	        	    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
	        	    image.setImageBitmap(myBitmap);

	        	}
	        }
            TextView tvTitle = (TextView)myContentsView.findViewById(R.id.title);
            tvTitle.setText(marker.getTitle());
   
            return myContentsView;
  }

  @Override
  public View getInfoWindow(Marker marker) {
   // TODO Auto-generated method stub
   return null;
  }
  
 }
///////////////////////////////////////////////////////////////////////////////////////////////
	private GoogleMap map;
	private double longitude;
	private double latitude;
	private String mVenueId,mVenueName,mCheckInId;
	String provider;
	GPSTracker gps;


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gps = new GPSTracker(MainActivity.this);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
        // check if GPS enabled       
        if(gps.canGetLocation()){
           
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            LatLng mUserLatLng=new LatLng(latitude,longitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mUserLatLng,12.0f));
    		map.addMarker(new MarkerOptions().snippet("me").position(mUserLatLng).title("Me and my Long = "+longitude+", my Lat = "+latitude));
    		
    		new LoadVenues().execute();
    		map.setInfoWindowAdapter(new MyInfoWindowAdapter());
    		map.setOnInfoWindowClickListener(this);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();   
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
		
	}
//////////////////////// load venues task/////////////////////////////////
	private class LoadVenues extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
           // create loading 
			ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Loading Venues ...", true);
			ringProgressDialog.setCancelable(false);
			if(FoursquareExplorer.mAllVenues.size()>0)
			{
			for (int i = 0; i <  FoursquareExplorer.mAllVenues.size(); i++) {
				LatLng venueLatLng=new LatLng(FoursquareExplorer.mAllVenues.get(i).getLat(),FoursquareExplorer.mAllVenues.get(i).getLng());
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng,12.0f));
				map.addMarker(new MarkerOptions().position(venueLatLng).snippet(FoursquareExplorer.mAllVenues.get(i).getId()).title(FoursquareExplorer.mAllVenues.get(i).getName()));
			}
			}
		}

		@Override
		protected String doInBackground(String... urls) {
            boolean success=(Boolean)JSONParser.getJSONArrFromUrl("https://api.foursquare.com/v2/venues/explore?client_id="+FoursquareExplorer.mClientId+"&client_secret="+FoursquareExplorer.mClientSecret+"&v=20130815&ll="+latitude+","+longitude+"&query=","Get",MainActivity.this);
			// save all venues to local db
			if(FoursquareExplorer.mAllVenues.size()>0 & success)
			{
				db.deleteAllRecords();
			    for (int i = 0; i <  FoursquareExplorer.mAllVenues.size(); i++) 
			      {
				    db.addVenue(FoursquareExplorer.mAllVenues.get(i));
			      }
			}
			if(success)
			return "success";
			else
				return "failed";
		}

		@Override
		protected void onPostExecute(String result) {
			ringProgressDialog.dismiss();
			if(FoursquareExplorer.mAllVenues.size()>0 )
			{
				map.clear();
				LatLng mUserLatLng=new LatLng(latitude,longitude);
	            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mUserLatLng,12.0f));
	    		map.addMarker(new MarkerOptions().snippet("me").position(mUserLatLng).title("Me and my Long = "+longitude+", my Lat = "+latitude));
			for (int i = 0; i <  FoursquareExplorer.mAllVenues.size(); i++) {
				LatLng venueLatLng=new LatLng(FoursquareExplorer.mAllVenues.get(i).getLat(),FoursquareExplorer.mAllVenues.get(i).getLng());
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng,12.0f));
				map.addMarker(new MarkerOptions().position(venueLatLng).snippet(FoursquareExplorer.mAllVenues.get(i).getId()).title(FoursquareExplorer.mAllVenues.get(i).getName()));
			}
		}
			else
				Toast.makeText(getApplicationContext(), "There Are No Venues Loaded", Toast.LENGTH_LONG).show();
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////// venue check-in task/////////////////////////////////////////
	private class VenueCheckIn extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
           // create loading 
			ringProgressDialog.setMessage("Wait For Check-In");
			ringProgressDialog.show();
		}

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		Checkin checkin=(Checkin)JSONParser.getJSONArrFromUrl("https://api.foursquare.com/v2/checkins/add?venueId="+mVenueId+"&oauth_token="+FoursquareExplorer.access_token+"&ll="+latitude+","+longitude+"&v=20130815&m=foursquare", "Post",MainActivity.this);
		mCheckInId=checkin.getId();
		return mCheckInId;
	}

	@Override
	protected void onPostExecute(String id) {
		// TODO Auto-generated method stub
		super.onPostExecute(id);
		ringProgressDialog.dismiss();
		System.out.println("id returned from json object"+id);
		Toast.makeText(getApplicationContext(), "You Successfully Check In with Venue "+mVenueName+" with ID "+mVenueId +" And Check-In Id "+id, Toast.LENGTH_LONG).show();
	}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		mVenueId=marker.getSnippet();
		mVenueName=marker.getTitle();
		new VenueCheckIn().execute();
	}
///////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		new LoadVenues().execute();
		map.setInfoWindowAdapter(new MyInfoWindowAdapter());
		map.setOnInfoWindowClickListener(this);
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Provider Disapled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Provider Enapled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
