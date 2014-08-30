package org.codeforafrica.starreports;

import org.codeforafrica.starreports.HomePanelsActivity.MyAdapter;
import org.codeforafrica.starreports.HomePanelsActivity.MyFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.EditText;
import org.holoeverywhere.widget.Spinner;

import com.viewpagerindicator.CirclePageIndicator;

public class Report_PageIndicatorActivity extends BaseActivity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
    	initIntroActivityList();
	}

private void initIntroActivityList ()
{
  	setContentView(R.layout.report_pageindicator);
  	initSlidingMenu();
  	
	int[] titles1 =
		{(R.string.report_add_title),
			(R.string.report_add_description),
			(R.string.report_add_category),
			(R.string.report_add_location),
			(R.string.report_add_contact)
			};
	
	int[] messages1 =
		{(R.string.report_add_title_desc),
			(R.string.report_add_description_desc),
			(R.string.report_add_category_desc),
			(R.string.report_add_location_desc),
			(R.string.report_add_contact_desc)
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
     
     
     final float density = getResources().getDisplayMetrics().density;
     
     indicator.setRadius(5 * density);
     indicator.setFillColor(0xFFFF0000);
     indicator.setPageColor(0xFFaaaaaa);
     //indicator.setStrokeColor(0xFF000000);
     //indicator.setStrokeWidth(2 * density);
	    		
     
   
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
	
	 ViewGroup root = (ViewGroup) inflater.inflate(R.layout.report_pager_edit, null);
	 ((TextView)root.findViewById(R.id.title)).setText(mTitle);
     
     ((TextView)root.findViewById(R.id.description)).setVisibility(View.GONE);
     
	 //set visibility depending on section
	 if(mTitle.equals(getResources().getString(R.string.report_add_title))){
		 ((EditText)root.findViewById(R.id.add_title)).setVisibility(View.VISIBLE);;
		 ((EditText)root.findViewById(R.id.add_title)).setHint(mMessage);
     }else if(mTitle.equals(getResources().getString(R.string.report_add_description))){
		 ((EditText)root.findViewById(R.id.add_description)).setHint(mMessage);
		 ((EditText)root.findViewById(R.id.add_description)).setVisibility(View.VISIBLE);;

     }else if(mTitle.equals(getResources().getString(R.string.report_add_category))){
		 ((Spinner)root.findViewById(R.id.add_category)).setPrompt(mMessage);
		 ((Spinner)root.findViewById(R.id.add_category)).setVisibility(View.VISIBLE);;

     }else if(mTitle.equals(getResources().getString(R.string.report_add_location))){
		 ((EditText)root.findViewById(R.id.add_location)).setHint(mMessage);
		 ((EditText)root.findViewById(R.id.add_location)).setVisibility(View.VISIBLE);;

     }else if(mTitle.equals(getResources().getString(R.string.report_add_contact))){
		 ((EditText)root.findViewById(R.id.add_contact)).setHint(mMessage);
		 ((EditText)root.findViewById(R.id.add_contact)).setVisibility(View.VISIBLE);;

     }
     
     
     return root;
 }

}
}
