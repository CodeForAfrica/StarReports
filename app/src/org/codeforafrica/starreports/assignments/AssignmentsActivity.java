package org.codeforafrica.starreports.assignments;

import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.R.id;
import org.codeforafrica.starreports.R.layout;
import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.ui.MyCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fima.cardsui.views.CardUI;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

public class AssignmentsActivity extends BaseActivity{
    private CardUI mCardView;
    ProgressDialog pDialog;
    JSONArray posts = null;
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	    
	    	super.onCreate(savedInstanceState);
	    	
	        setContentView(R.layout.activity_assignments);
	        
	        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        
	        //init CardView
			mCardView = (CardUI) findViewById(R.id.cardsview);
			mCardView.setSwipeable(false);
			
			new GetAssignments().execute();
			
	  }
	  
	  class GetAssignments extends AsyncTask<String, String, String> {
		   	 @Override
		        protected void onPreExecute() {
			   		super.onPreExecute();
			        pDialog = new ProgressDialog(AssignmentsActivity.this);
			        pDialog.setMessage("Retrieving assignments...");
			        pDialog.setIndeterminate(false);
			        pDialog.setCancelable(false);
			        pDialog.show();
			         
		        }
		        protected String doInBackground(String... args) {
		        	    
		        	APIFunctions apiFunctions = new APIFunctions();
					JSONObject sjson = apiFunctions.getPosts();

					
					
					try {
						posts = sjson.getJSONArray("posts");
										
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
		        	return null;
		        }
		        protected void onPostExecute(String file_url) {
		            pDialog.dismiss();
		        	
		        	try {
						createCards();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
			}
		    
		    public void createCards() throws JSONException{
		    	
		    	for(int i = 0; i<posts.length(); i++){
		    		String post = posts.getString(i);
		    		JSONObject json_data = new JSONObject(post);
		    		
		    		String title = json_data.getString("title");
		    		String excerpt = json_data.getString("excerpt");
		    		String date = json_data.getString("date");
		    		
		    		mCardView.addCard(new AssignmentCard(title, excerpt));
					mCardView.refresh();
		    		
		    	}
		    }
}
