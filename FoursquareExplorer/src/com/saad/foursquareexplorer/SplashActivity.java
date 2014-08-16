package com.saad.foursquareexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


public class SplashActivity extends Activity {
	Handler mHandler = new Handler();
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			// load venues photos from memory
			if(FoursquareExplorer.loadVenuesPhotosFromMemory())
				Toast.makeText(getApplicationContext(), "There Are No Old Venues", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(SplashActivity.this,LoginActivity.class));
			SplashActivity.this.finish();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		loadPhotos(2000);
		
	}
	private void loadPhotos(int milliseconds) {
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable,milliseconds);
	}
}
