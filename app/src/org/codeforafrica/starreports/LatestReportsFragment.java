package org.codeforafrica.starreports;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import com.animoto.android.views.DraggableGridView;
import android.annotation.SuppressLint;
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
 
    private BaseActivity mActivity;
    private SharedPreferences mSettings;
    
    protected DraggableGridView mOrderClipsDGV;

    
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
        

        return mView;
    }
  
}