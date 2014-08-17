package com.saad.foursquareexplorer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.saad.objects.Checkin;
import com.saad.objects.VenueData;
import com.saad.sqLiteDB.DatabaseHandler;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.Settings;
import android.text.GetChars;
import android.util.Log;
import android.widget.Toast;

public class FoursquareExplorer extends Application {

	DatabaseHandler db = new DatabaseHandler(this);
	public static List<VenueData> mAllVenues = new ArrayList<VenueData>();
	public static List<Bitmap> mAllVenuesPhotos = new ArrayList<Bitmap>();
	public static List<Checkin> mAllCheckIns = new ArrayList<Checkin>();
	public static String FSAuth = "https://foursquare.com/oauth2/authenticate?client_id=JP4M34YGIKJPIQZG4UUIDJ4WBMBUF2P1WLMNBADBFG3OLXQD"
			+ "&response_type=code&redirect_uri=http://foursquareexplorer.com/redirect_uri";

	public static String mClientId = "JP4M34YGIKJPIQZG4UUIDJ4WBMBUF2P1WLMNBADBFG3OLXQD";
	public static String mClientSecret = "S4KKS0JMVQQH00HGNFA5RFWQHF22JUIEY05QDNCBKEPBHWYL";
	public static String access_token, access_code;
	private static FileOutputStream fos;
	public static String SdCardImagesUrl;
	private static boolean sIsDiskCacheAvailable = false;
	private static File sRootDir = null;

	public FoursquareExplorer() {
		// TODO Auto-generated method stub
	}

	public FoursquareExplorer(Context c) {
		// TODO Auto-generated method stub
		super.onCreate();
		initializeCacheDir(c);
		SdCardImagesUrl = sRootDir.getAbsolutePath() + File.separator;
		// + File.separator + "FoursquareExplorer" + File.separator;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (mAllVenues != null & db.getAllVenues().size() > 0) {
			mAllVenues = db.getAllVenues();
		}
	}

	public static void initializeCacheDir(Context context) {
		Context appContext = context.getApplicationContext();

		File rootDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// SD card is mounted, use it for the cache
			rootDir = appContext.getExternalCacheDir();
		} else {
			// SD card is unavailable, fall back to internal cache
			rootDir = appContext.getCacheDir();

			if (rootDir == null) {
				sIsDiskCacheAvailable = false;
				return;
			}
		}

		sRootDir = rootDir;

		// If the app doesn't yet have a cache dir, create it
		if (sRootDir.mkdirs()) {
			// Create the '.nomedia' file, to prevent the mediastore from
			// scanning your temp files
			File nomedia = new File(sRootDir.getAbsolutePath(), ".nomedia");
			try {
				nomedia.createNewFile();
			} catch (Exception e) {
				Log.e("File Ex ", "Failed creating .nomedia file!", e);
			}
		}

		sIsDiskCacheAvailable = sRootDir.exists();

		if (!sIsDiskCacheAvailable) {
			Log.w("File Ex ", "Failed creating disk cache directory "
					+ sRootDir.getAbsolutePath());
		} else {
			Log.d("File Ex ",
					"Caching enabled in: " + sRootDir.getAbsolutePath());

			// The cache dir is created, you can use it to store files
		}
	}

	public static boolean loadVenuesPhotosFromMemory() {
		// TODO Auto-generated method stub
		if (mAllVenues.size() > 0) {
			String imgPath = null;
			mAllVenuesPhotos = new ArrayList<Bitmap>();
			for (int i = 0; i < mAllVenues.size(); i++) {
				try {
					imgPath = mAllVenues.get(i).getPhotoSDCardUrl();
				} catch (NullPointerException e) {
					return false;
				}
				if (imgPath != null) {
					File imgFile = new File(imgPath);
					if (imgFile.exists()) {
						Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
								.getAbsolutePath());
						mAllVenuesPhotos.add(myBitmap);
					}
				}
			}
		}
		if (mAllVenues.size() == mAllVenuesPhotos.size()
				& mAllVenues.size() != 0)
			return true;
		else
			return false;
	}

	public static Bitmap getBitmapFromURL(String link, String venueName,
			Context c) {
		/*--- this method downloads an Image from the given URL, 
		 *  then decodes and returns a Bitmap object
		 ---*/
		FoursquareExplorer app = new FoursquareExplorer(c);
		File file = new File(FoursquareExplorer.SdCardImagesUrl + venueName
				+ ".jpg");
		if(!file.exists()){
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			
			app.saveImageToSD(venueName, myBitmap, c);
			return myBitmap;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("getBmpFromUrl error: ", e.getMessage().toString());
			return null;
		}
	}
		else 
		return null;
	}

	public static boolean saveImageToSD(String venueName, Bitmap bmp, Context c) {

		/*--- this method will save your downloaded image to SD card ---*/

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		/*--- you can select your preferred CompressFormat and quality. 
		 * I'm going to use JPEG and 100% quality ---*/
		bmp.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
		/*--- create a new file on SD card ---*/
		File file = new File(FoursquareExplorer.SdCardImagesUrl + venueName
				+ ".jpg");
		try {
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		/*--- create a new FileOutputStream and write bytes to file ---*/
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		try {
			fos.write(bytes.toByteArray());
			fos.close();
			// Toast.makeText(c, "Image saved", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		mAllVenuesPhotos.add(bmp);
		return true;
	}
	
}
