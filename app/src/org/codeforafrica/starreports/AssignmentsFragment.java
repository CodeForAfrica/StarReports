package org.codeforafrica.starreports;

import java.net.MalformedURLException;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.assignments.AssignmentCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import android.widget.ProgressBar;
import org.holoeverywhere.widget.Toast;
import org.json.JSONException;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcFault;
import redstone.xmlrpc.XmlRpcStruct;

import com.animoto.android.views.DraggableGridView;
import com.fima.cardsui.views.CardUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
@SuppressLint("ValidFragment")
public class AssignmentsFragment extends Fragment {
    public ViewPager mAddClipsViewPager;
    View mView = null;
    private ProgressBar pB;
    private BaseActivity mActivity;
    private SharedPreferences mSettings;
    
    protected DraggableGridView mOrderClipsDGV;
    List<Page> posts = null;
    private CardUI mCardView;
    private void initFragment ()
    {
    	mActivity = (BaseActivity)getActivity();
    	
        //mHandlerPub = mActivity.mHandlerPub;

        mSettings = PreferenceManager
        .getDefaultSharedPreferences(getActivity().getApplicationContext());
	
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	initFragment ();
    	
    	int layout = R.layout.activity_assignments;//getArguments().getInt("layout");
    	mView = inflater.inflate(layout, null);
    	
    	
        pB = (ProgressBar)mView.findViewById(R.id.pBLoading);
        //init CardView
        mCardView = (CardUI) mView.findViewById(R.id.cardsview);
        mCardView.setSwipeable(false);
        new GetAssignments().execute();
        
        return mView;
    }
    
    class GetAssignments extends AsyncTask<String, String, String> {
	   	 @Override
	        protected void onPreExecute() {
		   		super.onPreExecute();
		   		
		         
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
	        	
	        	if(posts.size()==0){
	        		Toast.makeText(mActivity.getApplicationContext(), "No assignments found", Toast.LENGTH_LONG).show();
	        	}
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
	        
	    	for(int i = 0; i<posts.size(); i++){
	    		final int s = i;
	    		Page post = posts.get(i);
	    		String title = post.getTitle();
	    		String excerpt = post.getDescription();
	    		String date = post.getDateCreated().toString();
	    		assignmentID[i] = post.getPostid();
	    		
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
						Intent intent = new Intent(getActivity(), Report_PageIndicatorActivity.class);
						intent.putExtra("mediaTypes", _mediaTypes[s]);
						intent.putExtra("assignmentID", assignmentID[s]);
						startActivity(intent);
					}
					
				});
	    		
	    		mCardView.addCard(cardsArray[i]);
				mCardView.refresh();
	    		
	    	}
	    }
}