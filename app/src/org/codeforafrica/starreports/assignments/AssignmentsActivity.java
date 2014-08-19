package org.codeforafrica.starreports.assignments;

import java.net.MalformedURLException;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.BaseActivity;
import org.codeforafrica.starreports.R;
import org.codeforafrica.starreports.StoryMakerApp;
import org.codeforafrica.starreports.R.id;
import org.codeforafrica.starreports.R.layout;
import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.ui.MyCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redstone.xmlrpc.XmlRpcFault;

import com.fima.cardsui.views.CardUI;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import net.bican.wordpress.Page;
public class AssignmentsActivity extends BaseActivity{
    private CardUI mCardView;
    ProgressDialog pDialog;
    List<Page> posts = null;
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
		    	for(int i = 0; i<posts.size(); i++){
		    		Page post = posts.get(i);
		    		String title = post.getTitle();
		    		String excerpt = post.getDescription();
		    		String date = post.getDateCreated().toString();
		    		
		    		mCardView.addCard(new AssignmentCard(title, excerpt));
					mCardView.refresh();
		    		
		    	}
		    }
}
