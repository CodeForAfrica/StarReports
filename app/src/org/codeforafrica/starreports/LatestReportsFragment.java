package org.codeforafrica.starreports;

import java.net.MalformedURLException;
import java.util.List;

import net.bican.wordpress.Page;

import org.codeforafrica.starreports.api.APIFunctions;
import org.codeforafrica.starreports.server.ServerManager;
import org.codeforafrica.starreports.ui.MyCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redstone.xmlrpc.XmlRpcFault;

import com.animoto.android.views.DraggableGridView;
import com.fima.cardsui.views.CardUI;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
    JSONArray posts = null;
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
    	
    	for(int i = 0; i<posts.length(); i++){
    		String post = posts.getString(i);
    		JSONObject json_data = new JSONObject(post);
    		
    		String title = json_data.getString("title");
    		String excerpt = json_data.getString("excerpt");
    		String date = json_data.getString("date");
    		
    		mCardView.addCard(new MyCard(title, excerpt));
			mCardView.refresh();
    		
    	}
    }
}