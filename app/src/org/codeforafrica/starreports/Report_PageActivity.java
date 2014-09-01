package org.codeforafrica.starreports;

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

import org.codeforafrica.starreports.location.GPSTracker;
import org.codeforafrica.starreports.location.PlaceJSONParser;
import org.holoeverywhere.widget.ProgressBar;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.viewpagerindicator.CirclePageIndicator;

public class Report_PageActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener{
	private SliderLayout mDemoSlider;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    	super.onCreate(savedInstanceState);
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    	getSupportActionBar().setTitle("");
    	initIntroActivityList();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        getSupportMenuInflater().inflate(R.menu.save_report, menu);
       
        menu.findItem(R.id.about).setVisible(false);
        menu.findItem(R.id.menu_add_report).setVisible(false);
        menu.findItem(R.id.menu_sync_reports).setVisible(false);
        menu.findItem(R.id.menu_new_form).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);

        return true;
    }
	@Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	
        
            case android.R.id.home:
            	Intent i = new Intent(Report_PageActivity.this, ReportsFragmentsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(i);
            	finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
private void initIntroActivityList ()
{
  	setContentView(R.layout.report_pageindicator);
  	
	int[] titles1 =
		{(R.string.report_add_title),
			(R.string.report_add_description),
			(R.string.report_add_category),
			(R.string.report_add_location)
			};
	
	int[] messages1 =
		{(R.string.report_add_title_desc),
			(R.string.report_add_description_desc),
			(R.string.report_add_category_desc),
			(R.string.report_add_location_desc)
			};
	

	

	MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), titles1,messages1);
	ViewPager pager = ((ViewPager)findViewById(R.id.pager1));
	
	pager.setId((int)(Math.random()*10000));
	pager.setOffscreenPageLimit(5);
		
	pager.setAdapter(adapter);
		 
	//Bind the title indicator to the adapter
     CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.circles1);
     indicator.setViewPager(pager);
     indicator.setSnap(true);
     indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageSelected(int position) {
             Toast.makeText(getSupportActionBarContext(), "Changed to page " + position, Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             //save the data here
             Toast.makeText(getSupportActionBarContext(), "scroll page " + position, Toast.LENGTH_SHORT).show();

         }

         @Override
         public void onPageScrollStateChanged(int state) {
         }
     });
     
     final float density = getResources().getDisplayMetrics().density;
     
     indicator.setRadius(5 * density);
     indicator.setFillColor(0xFFFF0000);
     indicator.setPageColor(0xFFaaaaaa);
     //indicator.setStrokeColor(0xFF000000);
     //indicator.setStrokeWidth(2 * density);
	    		
     
     //gallery slideshow
     mDemoSlider = (SliderLayout)findViewById(R.id.slider);
     HashMap<String,String> url_maps = new HashMap<String, String>();
     url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
     url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
     url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
     url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

     HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
     file_maps.put("Hannibal",R.drawable.hannibal);
     file_maps.put("Big Bang Theory",R.drawable.bigbang);
     file_maps.put("House of Cards",R.drawable.house);
     file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

     for(String name : file_maps.keySet()){
         TextSliderView textSliderView = new TextSliderView(this);
         // initialize a SliderLayout
         textSliderView
                 .description(name)
                 .image(file_maps.get(name))
                 .setScaleType(BaseSliderView.ScaleType.Fit)
                 .setOnSliderClickListener(this);

         //add your extra information
         textSliderView.getBundle()
                 .putString("extra",name);

        mDemoSlider.addSlider(textSliderView);
     }
     mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
     mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
     mDemoSlider.setCustomAnimation(new DescriptionAnimation());
     mDemoSlider.setDuration(4000);
}
@Override
public void onSliderClick(BaseSliderView slider) {
    Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
}
public class MyAdapter extends FragmentPagerAdapter {
	 
	 int[] mMessages;
	 int[] mTitles;
	 
       public MyAdapter(FragmentManager fm, int[] titles, int[] messages) {
           super(fm);
           mTitles = titles;
           mMessages = messages;
       }

       @Override
       public int getCount() {
           return mMessages.length;
       }

       @Override
       public Fragment getItem(int position) {
       	Bundle bundle = new Bundle();
       	bundle.putString("title",getString(mTitles[position]));
       	bundle.putString("msg", getString(mMessages[position]));
       	
       	Fragment f = new MyFragment();
       	f.setArguments(bundle);
       	
           return f;
       }
   }

public static final class MyFragment extends Fragment {

	String mMessage;
	String mTitle;
	// Google Map
	private GPSTracker gpsT; 
    private double latitude;
    private double longitude;
    private ProgressBar pB;
    private WebView web;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
	 /**
  * When creating, retrieve this instance's number from its arguments.
  */
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);

     mTitle = getArguments().getString("title");
     mMessage = getArguments().getString("msg");
 }

 /**
  * The Fragment's UI is just a simple text view showing its
  * instance number.
  */
 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
	
	 ViewGroup root = null;
   
	 //set visibility depending on section
	 if(mTitle.equals(getResources().getString(R.string.report_add_title))){
		 root = (ViewGroup) inflater.inflate(R.layout.report_pager_edit_title, null);
		 ((EditText)root.findViewById(R.id.add_title)).setHint(mMessage);
		 
     }else if(mTitle.equals(getResources().getString(R.string.report_add_description))){
		 root = (ViewGroup) inflater.inflate(R.layout.report_pager_edit_description, null);
		 ((EditText)root.findViewById(R.id.add_description)).setHint(mMessage);
		 
     }else if(mTitle.equals(getResources().getString(R.string.report_add_category))){
    	 root = (ViewGroup) inflater.inflate(R.layout.report_pager_edit_taxonomy, null);
     }else if(mTitle.equals(getResources().getString(R.string.report_add_location))){
    	 root = (ViewGroup) inflater.inflate(R.layout.report_pager_edit_location, null);
		 //((EditText)root.findViewById(R.id.add_location)).setHint(mMessage);
		  web = (WebView) root.findViewById(R.id.webView);
		 //WebSettings webSettings = myWebView.getSettings();
		 //webSettings.setJavaScriptEnabled(true);
		 
		 	pB = (ProgressBar) root.findViewById(R.id.pBLoadWebView);
			
			web.setWebViewClient(new myWebClient());
			web.getSettings().setJavaScriptEnabled(true);
			
			//get user location
	    		gpsT = new GPSTracker(getActivity()); 
	    		  
	            // check if GPS enabled 
	            if(gpsT.canGetLocation()){ 
	            	
	                latitude = gpsT.getLatitude(); 
	                longitude = gpsT.getLongitude(); 
	                
	                web.loadUrl("http://192.168.1.41/webview_map.php?lat="+latitude+"&long="+longitude);
	            
	            }else{  
	                gpsT.showSettingsAlert(); 
	            } 
	        
	            //autocomplete location 
	            atvPlaces = (AutoCompleteTextView) root.findViewById(R.id.atv_places);
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
		 

     }
     
     
     return root;
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
 
    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>{
 
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";
 
            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyBkJAmtq5SDyK3nAWrmjoHiOLYWXUXw-Uk";
 
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
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
 
            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }
}
}
