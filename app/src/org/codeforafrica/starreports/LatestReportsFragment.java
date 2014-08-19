package org.codeforafrica.starreports;

import java.net.MalformedURLException;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.ui.MyCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONException;
import redstone.xmlrpc.XmlRpcFault;

import com.animoto.android.views.DraggableGridView;
import com.fima.cardsui.views.CardUI;

import android.annotation.SuppressLint;
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
public class LatestReportsFragment extends Fragment {
    public ViewPager mAddClipsViewPager;
    View mView = null;
    ProgressDialog pDialog;
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
    	
    	int layout = R.layout.fragment_latest_reports;//getArguments().getInt("layout");
    	
        mView = inflater.inflate(layout, null);
      //init CardView
        mCardView = (CardUI) mView.findViewById(R.id.cardsview);
        mCardView.setSwipeable(false);
        new GetRecentPosts().execute();
        
        return mView;
    }
    
    class GetRecentPosts extends AsyncTask<String, String, String> {
   	 @Override
        protected void onPreExecute() {
	   		super.onPreExecute();
	        /*pDialog = new ProgressDialog(mActivity.getBaseContext());
	        pDialog.setMessage("Retrieving latest posts...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        //pDialog.show();
	         *  
	         */
        }
        protected String doInBackground(String... args) {
        	
        	/*    
        	APIFunctions apiFunctions = new APIFunctions();
			JSONObject sjson = apiFunctions.getPosts();

			
			
			try {
				posts = sjson.getJSONArray("posts");
								
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
        	//Get Posts using xmlrpc
        	try {
				posts = StoryMakerApp.getServerManager().getRecentPosts(10);
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
            //pDialog.dismiss();
        	
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
    		
    		mCardView.addCard(new MyCard(title, excerpt));
			mCardView.refresh();
    		
    	}
    }
}