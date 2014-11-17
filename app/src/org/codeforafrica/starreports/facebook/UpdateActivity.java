package org.codeforafrica.starreports.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.DefaultsActivity;
import org.codeforafrica.starreports.HomePanelsActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.api.APIFunctions;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import org.codeforafrica.starreports.location.GPSTracker;
import org.codeforafrica.starreports.location.PlaceJSONParser;
import org.codeforafrica.starreports.server.ConnectionDetector;
 
public class UpdateActivity extends BaseActivity {
	// Connection detector class
    ConnectionDetector cd;
    //flag for Internet connection status
    Boolean isInternetPresent = false;
    
	EditText registerUsername;
	EditText first_name;
	EditText last_name;
	EditText email;
	EditText phone_number;	
	
	//location
	AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    String mapurl;
    private ProgressBar pB;
    private WebView web;
    private GPSTracker gpsT; 
	private String fineLocation;
	
	private ProgressDialog pDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        
		//get Internet status
        isInternetPresent = cd.isConnectingToInternet();
                
        first_name = (EditText)findViewById(R.id.first_name);
        last_name = (EditText)findViewById(R.id.last_name);
        email = (EditText)findViewById(R.id.email);
        phone_number = (EditText)findViewById(R.id.phone_number);
        atvPlaces = (AutoCompleteTextView)findViewById(R.id.location);
        
        mapurl = DefaultsActivity.map_url;
        
        web = (WebView) findViewById(R.id.webView);
   	 	//WebSettings webSettings = myWebView.getSettings();
   	 	//webSettings.setJavaScriptEnabled(true);
   	 
   	 	pB = (ProgressBar) findViewById(R.id.pBLoadWebView);
   		
   		web.setWebViewClient(new myWebClient());
   		web.getSettings().setJavaScriptEnabled(true);
   		
      //autocomplete location 
        atvPlaces.setThreshold(1);
 
        atvPlaces.addTextChangedListener(new TextWatcher() {
 
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }
 
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
                // TODO Auto-generated method stub
            }
 
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        
        atvPlaces.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                    long id) {
            	
                  web.loadUrl(mapurl + "?address=" + atvPlaces.getText().toString());

            }
        });
        
        if(isInternetPresent){
        	new getValues().execute();
        }else{
        	Toast.makeText(getApplicationContext(), "Check your connection!", Toast.LENGTH_LONG).show();
        }
        
        
    }
    
    class getValues extends AsyncTask<JSONObject, JSONObject, JSONObject> {
    	
	   	 	@Override
	        protected void onPreExecute() {
		   		super.onPreExecute();
		   		pDialog = new ProgressDialog(UpdateActivity.this);
				pDialog.setMessage("Retrieving profile...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
	        }
	        protected JSONObject doInBackground(JSONObject... args) {
	        	
	        	APIFunctions userFunction = new APIFunctions();
        		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    		       
                String user_id = settings.getString("user_id","0");
                
                JSONObject json = userFunction.getUser(user_id);
                JSONObject user = null;
    				try {
    						String res = json.getString("result"); 
    						
    						if(res.equals("OK")){
    							
    							user = new JSONObject(json.getString("user"));
        						
    						}else{
    							
    						}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
	        	return user;
	        }
	        protected void onPostExecute(JSONObject user){
	        	
	        	try {
						first_name.setText("" + user.get("first_name"));
						last_name.setText("" + user.get("last_name"));
				        email.setText("" + user.get("email"));
				        phone_number.setText("" + user.get("phone_number"));
				        atvPlaces.setText("" + user.get("address"));
		        
				        web.loadUrl(mapurl + "?address=" + atvPlaces.getText().toString());
				        
				} catch (JSONException e) {
					e.printStackTrace();
				} 
	        	
	        	pDialog.dismiss();
	        }
	        
    }
    class updateValues extends AsyncTask<String, String, String> {
    	
   	 	@Override
        protected void onPreExecute() {
	   		super.onPreExecute();
	   		pDialog = new ProgressDialog(UpdateActivity.this);
			pDialog.setMessage("Updating profile...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
        }
        protected String doInBackground(String... args) {
        	
            fineLocation = gpsT.getLatitude() + ", " + gpsT.getLongitude();
                
        	APIFunctions userFunction = new APIFunctions();
    		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		       
            String Vuser_id = settings.getString("user_id","0");
            
    		String Vemail = "" + email.getText().toString();
    		String Vfirst_name = "" + first_name.getText().toString();
    		String Vlast_name = "" + last_name.getText().toString();
    		String Vphone_number = "" + phone_number.getText().toString();
    		String Vaddress = "" + atvPlaces.getText().toString();

            JSONObject json = userFunction.updateUser(Vemail, Vfirst_name, Vlast_name, fineLocation, Vphone_number, Vaddress, Vuser_id);
			String message = "";
            try {
						String res = json.getString("result"); 
						if(res.equals("OK")){
							message = json.getString("message");
						}else{
							message = json.getString("error");
						}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
            
            
        	return message;
        }
        protected void onPostExecute(String message){
        	
        	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        	
        	pDialog.dismiss();
        	
        }
        
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        getSupportMenuInflater().inflate(R.menu.save_profile, menu);
        
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.menu_sync_reports).setVisible(false);
        menu.findItem(R.id.menu_new_form).setVisible(false);

        return true;
    }
    
  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	
            case android.R.id.home:
            	Intent i = new Intent(UpdateActivity.this, HomePanelsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(i);
            	break;
            case R.id.menu_save:
            	if(isInternetPresent){
            		//get user location
            		gpsT = new GPSTracker(UpdateActivity.this);
            		// check if GPS enabled 
                    if(gpsT.canGetLocation()){ 
                    	new updateValues().execute();
                    }else{
                		gpsT.showSettingsAlert();	
                    }
                }else{
                	Toast.makeText(getApplicationContext(), "Check your connection!", Toast.LENGTH_LONG).show();
                }	
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
//Fetches all places from GooglePlaces AutoComplete Web Service
  private class PlacesTask extends AsyncTask<String, Void, String>{

      @Override
      protected String doInBackground(String... place) {
          // For storing data from web service
          String data = "";

          // Obtain browser key from https://code.google.com/apis/console
          String key = "key=" + DefaultsActivity.G_API_Key;

          String input="";

          try {
              input = "input=" + URLEncoder.encode(place[0], "utf-8");
          } catch (UnsupportedEncodingException e1) {
              e1.printStackTrace();
          }

          // place type to be searched
          String types = "types=geocode";

          // Sensor enabled
          String sensor = "sensor=false";

          // Building the parameters to the web service
          String parameters = input+"&"+types+"&"+sensor+"&"+key;

          // Output format
          String output = "json";

          // Building the url to the web service
          String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

          try{
              // Fetching the data from we service
              data = downloadUrl(url);
          }catch(Exception e){
              Log.d("Background Task",e.toString());
          }
          return data;
      }

      @Override
      protected void onPostExecute(String result) {
          super.onPostExecute(result);

          // Creating ParserTask
          parserTask = new ParserTask();

          // Starting Parsing the JSON string returned by Web Service
          parserTask.execute(result);
      }
  }
  /** A class to parse the Google Places in JSON format */
  private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

      JSONObject jObject;

      @Override
      protected List<HashMap<String, String>> doInBackground(String... jsonData) {

          List<HashMap<String, String>> places = null;

          PlaceJSONParser placeJsonParser = new PlaceJSONParser();

          try{
              jObject = new JSONObject(jsonData[0]);
              
              // Getting the parsed data as a List construct
              places = placeJsonParser.parse(jObject);

          }catch(Exception e){
              Log.d("Exception",e.toString());
          }
          return places;
      }

      @Override
      protected void onPostExecute(List<HashMap<String, String>> result) {

          String[] from = new String[] { "description"};
          int[] to = new int[] { android.R.id.text1 };

          // Creating a SimpleAdapter for the AutoCompleteTextView
          SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

          // Setting the adapter
          atvPlaces.setAdapter(adapter);
      }
  }
  /** A method to download json data from url */
  private String downloadUrl(String strUrl) throws IOException{
      String data = "";
      InputStream iStream = null;
      HttpURLConnection urlConnection = null;
      try{
          URL url = new URL(strUrl);

          // Creating an http connection to communicate with url
          urlConnection = (HttpURLConnection) url.openConnection();

          // Connecting to url
          urlConnection.connect();

          // Reading data from url
          iStream = urlConnection.getInputStream();

          BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

          StringBuffer sb = new StringBuffer();

          String line = "";
          while( ( line = br.readLine()) != null){
              sb.append(line);
          }

          data = sb.toString();

          br.close();

      }catch(Exception e){
          Log.d("Exception while downloading url", e.toString());
      }finally{
          iStream.close();
          urlConnection.disconnect();
      }
      return data;
  }
  public class myWebClient extends WebViewClient
	{

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			pB.setVisibility(View.GONE);
			web.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
		
	}
}