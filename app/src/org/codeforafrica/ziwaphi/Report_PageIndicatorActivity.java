package org.codeforafrica.ziwaphi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;

import org.codeforafrica.ziwaphi.R;
import org.codeforafrica.ziwaphi.api.XMLRPCSyncService;
import org.codeforafrica.ziwaphi.assignments.AssignmentsActivity;
import org.codeforafrica.ziwaphi.encryption.Encryption;
import org.codeforafrica.ziwaphi.location.GPSTracker;
import org.codeforafrica.ziwaphi.location.PlaceJSONParser;
import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.model.Report;
import org.codeforafrica.ziwaphi.server.ServerManager;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONObject;

import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

public class Report_PageIndicatorActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener{
	private SliderLayout gallerySlider;
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
    
    EditText editTextTitle;
    TextView assignmentInfo;
    EditText editTextDescription;
    Spinner spinnerCategories;
    private Dialog dialog;
    private Dialog dialog_save;
    private Dialog dialog_publish;
    
    public boolean new_report = true;
    int resultMode;
    int assignmentID = 0;
    String mediaTypes;
    int rid;
	String title;
	String category;
	String description;
	String location;
    int story_mode;
    String mapurl;

    ProgressDialog pDialog;
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
        menu.findItem(R.id.menu_sync_reports).setVisible(false);
        menu.findItem(R.id.menu_new_form).setVisible(false);
        
        //if assignment
        
        if((getIntent().hasExtra("assignmentTitle"))){
        	mediaTypes = "" + getIntent().getStringExtra("mediaTypes");

	        if(!(mediaTypes.contains("video"))){
	    		menu.findItem(R.id.add_video).setVisible(false);
	    	}
	    	if(!(mediaTypes.contains("audio"))){
	    		menu.findItem(R.id.add_audio).setVisible(false);
	
	    	}
	    	if(!(mediaTypes.contains("image"))){
	    		menu.findItem(R.id.add_picture).setVisible(false);
	
	    	}
        }
        
        //if editing assignment
        if(rid!=-1){ 
        	Report r = Report.get(this, rid);            
            if(!r.getAssignment().equals("0")){
            	mediaTypes = r.getAssignmentMediaTypes();
            	if(!(mediaTypes.contains("video"))){
    	    		menu.findItem(R.id.add_video).setVisible(false);
    	    	}
    	    	if(!(mediaTypes.contains("audio"))){
    	    		menu.findItem(R.id.add_audio).setVisible(false);
    	
    	    	}
    	    	if(!(mediaTypes.contains("image"))){
    	    		menu.findItem(R.id.add_picture).setVisible(false);
    	
    	    	}
            }
        }
        return true;
    }
	@Override
    public void finish() {
        super.finish();
        Intent i = null;
        
        if(getIntent().hasExtra("assignmentTitle")){
        	i = new Intent(getBaseContext(), AssignmentsActivity.class);
        }else{
        	i = new Intent(getBaseContext(), HomePanelsActivity.class);
        } 
        
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	if(new_report){
	            		if(something_changed()){
	            			showSaveAlert();
	            		}else{
	            			do_report_close();
	            		}
	            	}else{
	            		//old report
	            		if(something_changed_db()){
	            			showSaveAlert();
	            		}else{
	            			do_report_close();
	            		}
	            	}
	            	break;
	            case R.id.add_picture:
	            	story_mode = 2;
					resultMode = Project.STORY_TYPE_PHOTO;
					launchProject(editTextTitle.getText().toString(), 0,spinnerCategories.getSelectedItemPosition(),"",editTextDescription.getText().toString(),atvPlaces.getText().toString(), false, false);		
					break;
	            case R.id.add_video:
	            	story_mode = 2;
					resultMode = Project.STORY_TYPE_VIDEO;
					launchProject(editTextTitle.getText().toString(), 0,spinnerCategories.getSelectedItemPosition(),"",editTextDescription.getText().toString(),atvPlaces.getText().toString(), false, false);		
					break;
	            case R.id.add_audio:
	            	story_mode = 2;
					resultMode = Project.STORY_TYPE_AUDIO;
					launchProject(editTextTitle.getText().toString(), 0,spinnerCategories.getSelectedItemPosition(),"",editTextDescription.getText().toString(),atvPlaces.getText().toString(), false, false);		
					break;
	            case R.id.import_from_gallery:
	            	launchProject(editTextTitle.getText().toString(), 0,spinnerCategories.getSelectedItemPosition(),"",editTextDescription.getText().toString(),atvPlaces.getText().toString(), false, true);		
	            	break;
	            case R.id.menu_save:
	            	report_save();
	            	break;
	            case R.id.menu_discard:
	            	delete_report();
	            	break;
	        }
	        return super.onOptionsItemSelected(item);
	    }
private void initIntroActivityList ()
{
  	setContentView(R.layout.report_pageindicator);
  	
     //gallery slideshow
     gallerySlider = (SliderLayout)findViewById(R.id.slider);  
     
     mapurl = DefaultsActivity.map_url;
     
     web = (WebView) findViewById(R.id.webView);
	 //WebSettings webSettings = myWebView.getSettings();
	 //webSettings.setJavaScriptEnabled(true);
	 
	 pB = (ProgressBar) findViewById(R.id.pBLoadWebView);
		
		web.setWebViewClient(new myWebClient());
		web.getSettings().setJavaScriptEnabled(true);
		
		//get user location
    		gpsT = new GPSTracker(this); 
    		  
            // check if GPS enabled 
            if(gpsT.canGetLocation()){ 
            	
                latitude = gpsT.getLatitude(); 
                longitude = gpsT.getLongitude(); 
                
                web.loadUrl(mapurl + "?lat="+latitude+"&long="+longitude);
            
            }else{  
                gpsT.showSettingsAlert(); 
            } 
        
            //autocomplete location 
            atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
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
            
            //txtNewStoryDesc = (TextView)findViewById(R.id.txtNewStoryDesc);
            editTextTitle = (EditText)findViewById(R.id.add_title);
            
            spinnerCategories = (Spinner)findViewById(R.id.add_category);  
            setCategories();
            
            editTextDescription = (EditText)findViewById(R.id.add_description);
            
            assignmentInfo = (TextView)findViewById(R.id.assignmentInfo);
            
            initializeReport();
}

public void initializeReport(){
	Intent i = getIntent();
    rid = i.getIntExtra("rid", -1);
    
    if(rid!=-1){ 
    	getSupportActionBar().setTitle("Edit Report");
    	Report r = Report.get(this, rid);
    	
    	location = r.getLocation();
    	title = r.getTitle();
        category = r.getSector();
        description = r.getDescription();
        
        
        editTextTitle.setText(title);
        spinnerCategories.setSelection(Integer.parseInt(category));
       
        editTextDescription.setText(description);
        atvPlaces.setText(location);
        
        if(!r.getAssignment().equals("0")){
        	assignmentID = Integer.parseInt(r.getAssignment());
        	
        	mediaTypes = r.getAssignmentMediaTypes();
        	        	
        	
			

        }
        new_report = false;
        
        loadSlider(r);
    }else{
    	    	
    	setLocation();

    	getSupportActionBar().setTitle("Add Report");

    	loadSlider(null);
    	
    }
    
    if(getIntent().hasExtra("assignmentTitle")){
		
		String assignmentTitle = "" + getIntent().getStringExtra("assignmentTitle");
		
		assignmentInfo.setVisibility(View.VISIBLE);
		
    	assignmentInfo.setText("Assignment: " + assignmentTitle);
    	
    	mediaTypes = "" + getIntent().getStringExtra("mediaTypes");
    	
    	hide_none_required_buttons();
	}
}

public void hide_none_required_buttons(){
	//disable buttons not included
	
}
public void loadSlider(Report r){
	int totalClips =  0;
	HashMap<String,File> file_maps = new HashMap<String, File>();
	
	if(r!=null){
		ArrayList<Project> mListProjects = Project.getAllAsList(getApplicationContext(), rid);
	 	for (int j = 0; j < mListProjects.size(); j++) {
	 		Project project = mListProjects.get(j);
	 		
	 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
	 	
		 	for (Media media: mediaList){
	
		 		if(media!=null){
		 			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 	    	String mt = dateFormat.format(media.getCreatedAt());
		 			File thumb = new File(Environment.getExternalStorageDirectory() + "/" + AppConstants.TAG + "/.thumbs/" + mt + ".jpg");
		 			if(thumb.exists()){
		 				
		 				totalClips++;
		 				file_maps.put(project.getTitle(),thumb);
		 				constructSlider2(file_maps);
		 			}
		 		}
		 	}
	 	}
	}
	
	if(totalClips == 0){
		HashMap<String,Integer> file_maps2 = new HashMap<String, Integer>();
		file_maps2.put("No media added yet!",R.drawable.gallery_slider);
		constructSlider(file_maps2);
	}
}

private void constructSlider2(HashMap<String, File> file_maps) {
	for(String name : file_maps.keySet()){
        TextSliderView textSliderView = new TextSliderView(this);
        // initialize a SliderLayout
        textSliderView
                .description(name)
                .image(file_maps.get(name))
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(this);

        //add your extra information
        textSliderView.getBundle()
                .putString("extra",name);

    gallerySlider.addSlider(textSliderView);
    }
    gallerySlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
    gallerySlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
    gallerySlider.setCustomAnimation(new DescriptionAnimation());
    gallerySlider.setDuration(4000);
    if(file_maps.size()<2){
    	gallerySlider.stopAutoCycle();
    }
}
public void constructSlider(HashMap<String,Integer> file_maps){

    for(String name : file_maps.keySet()){
        TextSliderView textSliderView = new TextSliderView(this);
        // initialize a SliderLayout
        textSliderView
                .description(name)
                .image(file_maps.get(name))
                .setScaleType(BaseSliderView.ScaleType.CenterInside)
                .setOnSliderClickListener(this);

        //add your extra information
        textSliderView.getBundle()
                .putString("extra",name);

        gallerySlider.addSlider(textSliderView);
    
    }
	    gallerySlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
	    gallerySlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
	    gallerySlider.setCustomAnimation(new DescriptionAnimation());
	    gallerySlider.setDuration(4000);
	    
	    if(file_maps.size()<2){
	    	gallerySlider.stopAutoCycle();
	    }
	    
}

@Override
public void onSliderClick(BaseSliderView slider) {
    //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
}
public void setSelectedItem(Spinner spinner,String string){
	int index = 0;
	for (int i = 0; i < spinner.getAdapter().getCount(); i++){
		if (spinner.getItemAtPosition(i).equals(string)){
			index = i;
		}
	}
		spinner.setSelection(index);
}
private void launchProject(String title, int pIssue, int pSector, String pEntity, String pDesc, String pLocation, boolean update, boolean importing) {
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String currentdate = dateFormat.format(new Date());
	
	
	if (title == null || title.length() == 0)
	{
		title = "Captured at "+currentdate;
	}
	
	
	Report report;
    if(rid==-1){
    	
    	report = new Report (this, 0, title, String.valueOf(pSector), String.valueOf(pIssue), pEntity, pDesc, pLocation, "0", currentdate, "0", "0", "0", "");
    	if(getIntent().hasExtra("assignmentID")){
        	report.setAssignment(String.valueOf(getIntent().getIntExtra("assignmentID", 0)));
        	report.setAssignmentMediaTypes(getIntent().getStringExtra("mediaTypes"));
        }
    	
    }else{
    	
    	report = Report.get(this, rid);
    	report.setTitle(title);
    	report.setDescription(pDesc);
    	report.setEntity(pEntity);
    	report.setIssue(String.valueOf(pIssue));
    	report.setSector(String.valueOf(pSector));
    	report.setLocation(pLocation);        
    }
            
    report.save();
    
    new_report = false;
    
    rid = report.getId();
            
    if(update == false){
    	
        Intent intent = new Intent(getBaseContext(), StoryNewActivity.class);
        intent.putExtra("storymode", resultMode);
        intent.putExtra("importing", importing);
        intent.putExtra("rid", report.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //setMediaCount();
         
    }else{
    	/*if(pLocation.equals("0, 0")){
    		Toast.makeText(getApplicationContext(), "Trouble finding location. Try again later!", Toast.LENGTH_LONG).show();
    	}else{
    	*/	
        	Toast.makeText(getBaseContext(), String.valueOf(rid)+" Updated successfully!", Toast.LENGTH_LONG).show();
        	report_close();
    	//}      	
    }
     
}

public void delete_report(){
	dialog_save = new Dialog(Report_PageIndicatorActivity.this);
	dialog_save.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog_save.setContentView(R.layout.dialog_delete);
	dialog_save.findViewById(R.id.button_ok).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			if(rid!=-1){
				Report.get(getApplicationContext(), rid).delete();
			}
			dialog_save.dismiss();
			finish();
		}        	
    });
	dialog_save.findViewById(R.id.button_cancel).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			dialog_save.dismiss();
		}        	
    });
	dialog_save.show();
}

public void showSaveAlert(){
	dialog_save = new Dialog(Report_PageIndicatorActivity.this);
	dialog_save.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog_save.setContentView(R.layout.dialog_save);
	dialog_save.findViewById(R.id.button_save).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			report_save();  
        	report_close();
			dialog_save.dismiss();
		}        	
    });
	dialog_save.findViewById(R.id.button_discard).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
        	report_close();
			dialog_save.dismiss();
		}        	
    });
	dialog_save.show();
}

public boolean something_changed_db(){
	Report report = Report.get(this, rid);
	
	if(((""+editTextTitle.getText().toString()).equals(report.getTitle()))&&
			(spinnerCategories.getSelectedItemPosition()==Integer.parseInt(report.getSector()))&&
			((""+atvPlaces.getText().toString()).equals(report.getLocation()))&&
			((""+editTextDescription.getText().toString()).equals(report.getDescription())))
			{
		return false;
	}else{
		return true;
	}
}
public boolean something_changed(){
		if((""+editTextTitle.getText().toString()).equals("")&&
			(spinnerCategories.getSelectedItemPosition()==0)&&
			((""+atvPlaces.getText().toString()).equals(""))&&
			(((""+editTextDescription.getText()).toString()).equals("")
			)){
		
		return false;
	}else{
			
    	return true;
	}
}
public void setLocation(){
	gpsT = new GPSTracker(Report_PageIndicatorActivity.this); 
	  
    // check if GPS enabled 
    if(gpsT.canGetLocation()){ 

        latitude = gpsT.getLatitude(); 
        longitude = gpsT.getLongitude(); 

        // \n is for new line 
        //gpsInfo.setText(latitude+", "+longitude); 
       /* GeoPoint myGeoPoint = new GeoPoint( 
              (int)(latitude*1000000), 
              (int)(longitude*1000000)); 
      	CenterLocatio(myGeoPoint); */
        if(String.valueOf(latitude).equals("0")){
            gpsT.showSettingsAlert(); 
        }
    }else{ 
        // can't get location 
        // GPS or Network is not enabled 
        // Ask user to enable GPS/network in settings 
        gpsT.showSettingsAlert(); 
    } 
}
public void report_save(){
	
	if (formValid()) {
		launchProject(editTextTitle.getText().toString(), 0,spinnerCategories.getSelectedItemPosition(),"",editTextDescription.getText().toString(),atvPlaces.getText().toString(), true, false);		
	}
	
}
public boolean formValid(){
	return true;
}
public void report_close(){
	dialog_publish = new Dialog(Report_PageIndicatorActivity.this);
	dialog_publish.requestWindowFeature(Window.FEATURE_NO_TITLE);
	dialog_publish.setContentView(R.layout.dialog_publish);
	dialog_publish.findViewById(R.id.button_publish).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			//TODO: check if sync or encrypt is running
			Intent i = new Intent(Report_PageIndicatorActivity.this,XMLRPCSyncService.class);
			i.putExtra("rid", rid);
	        startService(i);
	        	
			
			//Publish via Wordpress xmlrpc
			//new publish_report().execute();
	        	
			dialog_publish.dismiss();
		}        	
    });
	dialog_publish.findViewById(R.id.button_skip).setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
        	do_report_close();
			dialog_publish.dismiss();
		}        	
    });
	dialog_publish.show();
}
class publish_report extends AsyncTask<String, String, String> {
   	 @Override
        protected void onPreExecute() {
	   		super.onPreExecute();
	        pDialog = new ProgressDialog(Report_PageIndicatorActivity.this);
	        pDialog.setMessage("Posting report...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	        
        }
        protected String doInBackground(String... args) {
        	ServerManager sm = StoryMakerApp.getServerManager();
            //sm.setContext(getBaseContext());

            String postId="";
            String urlPost="";
            XmlRpcStruct structA = new XmlRpcStruct();
			structA.put("key","assignment_id");
			structA.put("value",assignmentID);
		 	
			
			try {
				
				
				StringBuffer sbBody = new StringBuffer();
				sbBody.append("");
				
				//upload media
				ArrayList<Project> mListProjects;
				mListProjects = Project.getAllAsList(getApplicationContext(), rid);
				String thumbnail= null;
			 	for (int j = 0; j < mListProjects.size(); j++) {
			 		Project project = mListProjects.get(j);
			 		
			 		Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
			 	
				 	for (Media media: mediaList){

				 		if(media!=null){
				 			
				 			
				 		String ppath = media.getPath();
					 	String ptype = media.getMimeType();
					 	
					 	String file = ppath;
					 	
					 	Log.d("encryption status:", "encryptionstatus" + media.getEncrypted());
					 	
					 	//if encrypted, decrypt before upload
					 	if(media.getEncrypted()!=0){
					 	
					 		Cipher cipher;
							try {				
								cipher = Encryption.createCipher(Cipher.DECRYPT_MODE);
								Encryption.applyCipher(file, file+"_", cipher);
							}catch (Exception e) {
								// TODO Auto-generated catch block
								Log.e("Encryption error", e.getLocalizedMessage());
								e.printStackTrace();
							}
							//Then delete original file
							File oldfile = new File(file);
							oldfile.delete();
							//Then remove _ on encrypted file
							File newfile = new File(file+"_");
							newfile.renameTo(new File(file));
					 	}	
					 		String murl = sm.addMedia(ptype, new File(file));
					 		//create link depending on media type
					 		if(ptype.contains("image")){
					 			murl = "<img width=\"100%\" src=\"" + murl + "\"/>";
					 		}else if(ptype.contains("video")){
					 			murl = "[video width=\"640\" height=\"272\" mp4=\""+ murl +"\"][/video]";
					 		}else{
					 			murl = "[audio mp3=\"" + murl +"\"][/audio]";
					 		}
					 		//post file here
					 		sbBody.append(murl);
					 		sbBody.append("\n\n");
					 		
					 		String bmp = Media.getThumbnailUrl(Report_PageIndicatorActivity.this,media,project);
	            			if (bmp != null)
	            				thumbnail = bmp;
				 		}
			 		}
			 	}
				
			 	
			 	//upload thumbnail
			 	if(thumbnail!=null){
			 		thumbnail = sm.addThumbnail("image/jpeg", new File(thumbnail));
			 	}
			 	
			 	String pDescription = description + "==Media==\n\n" + sbBody.toString();
			 	
			 	postId = sm.post2(title, pDescription, null, null, null, null, null, null, structA, thumbnail);
				urlPost = sm.getPostUrl(postId);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlRpcFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            return urlPost;    
			
        }
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        	do_report_close();
	   }
}

public void do_report_close(){ 	
	//Hide keyboard
    InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE); 
    inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),      
    		    InputMethodManager.HIDE_NOT_ALWAYS);
    //NavUtils.navigateUpFromSameTask(this);
    
    finish();
}
public void setCategories(){
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	try {
	    JSONArray jsonArray2 = new JSONArray(prefs.getString("categories", "[]"));
	    ArrayList<String> list=new ArrayList<String>();
	    list.add("Select Category");
		for(int i=0;i<jsonArray2.length();i++)
		{
			list.add(jsonArray2.getString(i));
		}
		
		ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(getApplicationContext(),  R.layout.spinner_report_new, list);
		spinnerCategories.setAdapter(spinnerMenu);

		
	}catch (Exception e) {
	    e.printStackTrace();
	}
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
    
    
}
