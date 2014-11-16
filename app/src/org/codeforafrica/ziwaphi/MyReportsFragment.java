package org.codeforafrica.ziwaphi;

import java.util.ArrayList;

import org.codeforafrica.ziwaphi.R;
import org.codeforafrica.ziwaphi.ReportsActivity.ReportArrayAdapter;
import org.codeforafrica.ziwaphi.ReportsActivity.getThumbnail;
import org.codeforafrica.ziwaphi.model.Media;
import org.codeforafrica.ziwaphi.model.Project;
import org.codeforafrica.ziwaphi.model.Report;
import org.codeforafrica.ziwaphi.ui.ReportCard;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import com.animoto.android.views.DraggableGridView;
import com.fima.cardsui.views.CardUI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
@SuppressLint("ValidFragment")
public class MyReportsFragment extends Fragment {
    private final static String TAG = "MyReportsFragment";

    
    public ViewPager mAddClipsViewPager;
    View mView = null;
 
    private BaseActivity mActivity;
    private Handler mHandlerPub;
    
    private SharedPreferences mSettings;
    
    protected DraggableGridView mOrderClipsDGV;

    
    private ArrayList<Report> mListReports;
	private ReportArrayAdapter aaReports;
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
    	
    	int layout = R.layout.fragment_my_reports;//getArguments().getInt("layout");
    	
        mView = inflater.inflate(layout, null);
        
      //init CardView
      mCardView = (CardUI) mView.findViewById(R.id.cardsview);
      mCardView.setSwipeable(false);
      mListReports = Report.getAllAsList(mActivity);
      createCards();
        return mView;
    }
    
    public void createCards(){
    	for(int i = 0; i<mListReports.size(); i++){
    		
    		final Report r = mListReports.get(i);
    		
    		//Get report status
    		String synced = "";
    		String exported = "";
    		
    		if(r.getServerId().equals("0")){
            	synced = "Not synced";
            }else{
            	synced = "Synced";
            }
            
            if(r.getExported().equals("0")){
            	exported = "Not exported";
            }else{
            	exported = "Exported";
            }
            
            String status = exported+" | "+synced;
    		
            //Get stats
            int vids =0;
            int pics =0;
            int auds =0;
            
            ArrayList<Project> mListProjects;
    		mListProjects = Project.getAllAsList(mActivity.getApplicationContext(), r.getId());
    	 
            //Get MIME types
        	for (int j = 0; j < mListProjects.size(); j++) {
    	 		Project project2 = mListProjects.get(j);
    	 		
    	 		Media[] mediaList = project2.getScenesAsArray()[0].getMediaAsArray();
    	 		
    	 		for(int k = 0; k<mediaList.length; k++){
    	 			
	    	 		Media media = mediaList[k];
	    	 		
	    	 		if(media!=null)
	    	 		{
		    		 	String ptype = media.getMimeType();
		    		 	if(ptype.contains("image")){
		    		 		pics++;
		    		 		
		    		 	}else if(ptype.contains("video")){
		    		 		vids++;
		    		 		
		    		 	}else if(ptype.contains("audio")){
		    		 		auds++;
		    		 	}
	    	 		}
    	 		}
        	}
    		Drawable reportThumb = getReportThumb(r.getId());
			ReportCard androidViewsCard2 = new ReportCard(reportThumb , r.getTitle(), r.getDescription(), status, r.getDate(), vids, pics, auds);
    		final int rid = r.getId();
    		androidViewsCard2.setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View v) {
    				Intent intent =  new Intent(mActivity, Report_PageIndicatorActivity.class);
    				intent.putExtra("rid", rid);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		        startActivity(intent);
    		        getActivity().finish();
    			}
    		});
    		
			mCardView.addCard(androidViewsCard2);
			mCardView.refresh();

    	}
    }
    
    public Drawable getReportThumb(int reportId){
		
    	//Thumbnail Business
        ArrayList<Project> mListProjects;
		mListProjects = Project.getAllAsList(mActivity.getApplicationContext(), reportId);
	 	Log.d("id", "Projects:"+mListProjects.size());

		Bitmap bmp_= null;
		if(mListProjects.size()>0){
    		Project project = mListProjects.get(0);
            // FIXME default to use first scene
            Media[] mediaList = project.getScenesAsArray()[0].getMediaAsArray();
            
            if (mediaList != null && mediaList.length > 0)    
            {
            	for (Media media: mediaList)
            		if (media != null)
            		{
            			
            			Bitmap bmp = Media.getThumbnail(mActivity,media,project);
            			if (bmp != null)
            				bmp_ = bmp;
            			
            		}
            }
		}
		return new BitmapDrawable(getResources(), bmp_);
    }
}