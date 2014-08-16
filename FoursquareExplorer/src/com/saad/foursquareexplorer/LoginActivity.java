package com.saad.foursquareexplorer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.ila.parser.JSONParser;
import com.saad.objects.FoursquareSession;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	final String url = "https://www.foursquare.com/oauth2/authenticate?client_id="
			+ FoursquareExplorer.mClientId
			+ "&response_type=code"
			+ "&redirect_uri=http://foursquareexplorer.com/redirect_uri";
	ProgressDialog ringProgressDialog;
	// UI references.
	private WebView loginView;
	Intent intent;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginView = (WebView) findViewById(R.id.loginView);
		FourSquareLogin(loginView);
		loginView.loadUrl(url);
	}

	private void FourSquareLogin(WebView webview) {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			@SuppressWarnings("unused")
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// Here put your code
				Log.d("My Webview", url);
				String accessTokenFragment = "access_token=";
				String accessCodeFragment = "code=";

				if (url.contains(accessTokenFragment)) {
					// the GET request contains directly the token
					String accessToken = url.substring(url
							.indexOf(accessTokenFragment));
					System.out.println("access token = " + accessToken);

				} else if (url.contains(accessCodeFragment)) {
					// the GET request contains an authorization code
					String accessCode = url.substring(url
							.indexOf(accessCodeFragment));
					final String newAccessCode = accessCode.toString()
							.substring(5, accessCode.toString().length());
					System.out.println("access code = " + newAccessCode);
					FoursquareExplorer.access_code = newAccessCode;
					if (accessCode != null) {

						LoginTask(accessCode, newAccessCode);

					} else if (accessCode != null) {
						Toast.makeText(LoginActivity.this, accessCode,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(LoginActivity.this,
								"Unknown login error", Toast.LENGTH_SHORT)
								.show();
					}
				}
				// return true; //Indicates WebView to NOT load the url;
				return false; // Allow WebView to load url
			}

			private void LoginTask(String accessCode, final String newAccessCode) {
				new AsyncTask<Object, Object, Object>() {

					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
						ringProgressDialog = ProgressDialog.show(
								LoginActivity.this, "Please wait ...",
								"Login To Foursquare ...", true);
						ringProgressDialog.setCancelable(false);
					}

					@Override
					protected Object doInBackground(Object... params) {
						try {
							// Call Foursquare again to get the access token
							JSONObject tokenJson = executeHttpGet("https://foursquare.com/oauth2/access_token"
									+ "?client_id="
									+ FoursquareExplorer.mClientId
									+ "&client_secret="
									+ FoursquareExplorer.mClientSecret
									+ "&grant_type=authorization_code"
									+ "&redirect_uri=http://foursquareexplorer.com/redirect_uri"
									+ "&code=" + newAccessCode);

							String token = tokenJson.getString("access_token");
							System.out.println("access_token = " + token);
							// TODO save token
							FoursquareExplorer.access_token = token;
//							// Get userdata of myself
//							JSONObject userJson = executeHttpGet("https://api.foursquare.com/v2/"
//									+ "users/self" + "?oauth_token=" + token);
//
//							// Get return code
//							int returnCode = Integer.parseInt(userJson
//									.getJSONObject("meta").getString("code"));
//							System.out.println("returnCode = " + returnCode);
//							// 200 = OK
//							if (returnCode == 200) {
//								// output data
//								Log.i("LoginTest",
//										userJson.getJSONObject("response")
//												.getJSONObject("user")
//												.toString());
//								return true;
//							} else {
//								Log.e("LoginTest", "Wrong return code: "
//										+ params[0]);
//								return false;
//							}
							return true;
						} catch (Exception exp) {
							Log.e("LoginTest", "Login to Foursquare failed",
									exp);
							return false;
						}
					}

					@Override
					protected void onPostExecute(Object result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						ringProgressDialog.dismiss();
						if ((boolean) result.equals(true)) {
							Intent in = new Intent(LoginActivity.this,
									MainActivity.class);
							startActivity(in);
							LoginActivity.this.finish();
						} else {
							Toast.makeText(getApplicationContext(),
									"Unknown login error", Toast.LENGTH_SHORT)
									.show();
						}

					}
				}.execute(accessCode);
			}
		});
	}

	// Calls a URI and returns the answer as a JSON object
	private JSONObject executeHttpGet(String uri) throws Exception {
		HttpGet req = new HttpGet(uri);

		HttpClient client = new DefaultHttpClient();
		HttpResponse resLogin = client.execute(req);
		BufferedReader r = new BufferedReader(new InputStreamReader(resLogin
				.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = r.readLine()) != null) {
			sb.append(s);
		}

		return new JSONObject(sb.toString());
	}
}
