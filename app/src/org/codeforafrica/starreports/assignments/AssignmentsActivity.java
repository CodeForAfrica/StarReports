package org.codeforafrica.starreports.assignments;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.Report_PageIndicatorActivity;
import org.codeforafrica.starreports.StoryMakerApp;
import org.codeforafrica.starreports.R.id;
import org.codeforafrica.starreports.R.layout;
import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.ui.MyCard;
import org.holoeverywhere.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fima.cardsui.views.CardUI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import net.bican.wordpress.Page;
public class AssignmentsActivity extends BaseActivity{
    private CardUI mCardView;
    List<Page> posts = null;
    ProgressBar pB;

	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	    
	    	super.onCreate(savedInstanceState);
	    	
	        setContentView(R.layout.activity_assignments);
	        
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        
	        //init CardView
			mCardView = (CardUI) findViewById(R.id.cardsview);
			mCardView.setSwipeable(false);
			
			pB = (ProgressBar)findViewById(R.id.pBLoading);
			
			new GetAssignments().execute();
			
	  }
	  
	  class GetAssignments extends AsyncTask<String, String, String> {
		   	 @Override
		        protected void onPreExecute() {
			   		super.onPreExecute();
			   		pB.setVisibility(View.VISIBLE);
		        }
		        protected String doInBackground(String... args) {
		        	    
		        	try {
						posts = StoryMakerApp.getServerManager().getRecentAssignments(10);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (XmlRpcFault e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	return null;
		        }
		        protected void onPostExecute(String file_url) {
			   		pB.setVisibility(View.GONE);
		        	
		        	try {
						createCards();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
		    
		    public void createCards() throws JSONException{
		        final int[] assignmentID = new int[posts.size()];
		        final String[] _mediaTypes = new String[posts.size()];
		        final String[] assignmentTitles = new String[posts.size()];
		        
		    	for(int i = 0; i<posts.size(); i++){
		    		final int s = i;
		    		Page post = posts.get(i);
		    		String title = post.getTitle();
		    		String excerpt = post.getDescription();
		    		String date = post.getDateCreated().toString();
		    		assignmentID[i] = post.getPostid();
		    		assignmentTitles[s] = title;
		    		
		    		String bounty = "";
		    		String location = "";
		    		int narrative = 0;
		    		int video = 0;
		    		int image = 0;
		    		int audio = 0;
		    		
		    		//create array of cards
		    		AssignmentCard[] cardsArray = new AssignmentCard[posts.size()];
		    		
		    		XmlRpcArray cFields = post.getCustom_fields();
		    		for(int j = 0; j<cFields.size(); j++){
		    			
		    			XmlRpcStruct lField = cFields.getStruct(j);
		    			if(lField.getString("key").equals("assignment_bounty")){
		    				bounty = lField.getString("value");
		    			}
		    			if(lField.getString("key").equals("assignment_location")){
		    				location = lField.getString("value");
		    			}
		    			String mediaTypes = "";
		    			//Types to capture
		    			if(lField.getString("value").contains("narrative")){
		    				narrative = 1;
		    				mediaTypes = mediaTypes + " narrative ";
		    			}
		    			if(lField.getString("value").contains("video")){
		    				video = 1;
		    				mediaTypes = mediaTypes + " video ";
		    			}
		    			if(lField.getString("value").contains("audio")){
		    				audio = 1;
		    				mediaTypes = mediaTypes + " audio ";
		    			}
		    			if(lField.getString("value").contains("image")){
		    				image = 1;
		    				mediaTypes = mediaTypes + " image ";
		    			}
		    			_mediaTypes[i] = mediaTypes;
		    		}
		    		
		    		cardsArray[i] = new AssignmentCard(title, excerpt, date, bounty, location, narrative, video, audio, image);
		    		cardsArray[i].setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {

							// TODO: Auto-generated method stub
							Intent intent = new Intent(AssignmentsActivity.this, Report_PageIndicatorActivity.class);
							intent.putExtra("mediaTypes", _mediaTypes[s]);
							intent.putExtra("assignmentID", assignmentID[s]);
							intent.putExtra("assignmentTitle", assignmentTitles[s]);
							startActivity(intent);
						}
						
					});
		    		
		    		mCardView.addCard(cardsArray[i]);
					mCardView.refresh();
		    		
		    	}
		    }
		    @Override
		    public boolean onCreateOptionsMenu(Menu menu) {
		        super.onCreateOptionsMenu(menu);
		        
		        menu.findItem(R.id.menu_sync_reports).setVisible(false);

		        return true;
		    }
		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        switch (item.getItemId()) {
		            case android.R.id.home:
			        		NavUtils.navigateUpFromSameTask(this);
			        	
		                return true;
		        }
		        return super.onOptionsItemSelected(item);
		    }
}
