package org.codeforafrica.ziwaphi.assignments;

import org.codeforafrica.ziwaphi.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

public class AssignmentCard extends Card {

	private String mDesc = "";
	private View mView;
	private TextView mTextViewTitle;
	private TextView mTextViewDesc;
	private int mId = -1;
	private int mIcon = -1;
	private String mdate;
	private String mbounty;
	private String mlocation;
	private int mnarrative; 
	private int mvideo;
	private int maudio;
	private int mimage;
	
	public AssignmentCard(String title, String desc, String date, String bounty, String location, int narrative, int video, int audio, int image){
		super(title);
		mDesc = desc;
		mdate = date;
		mbounty = bounty;
		mlocation = location;
		mnarrative = narrative; 
		mvideo = video;
		maudio = audio;
		mimage = image;
	} 

	public void setIcon (int icon)
	{
		mIcon = icon;
	}
	

	private OnClickListener mListener;
	
	@Override
	public void setOnClickListener(OnClickListener listener) {
		
		mListener = listener;
		
		super.setOnClickListener(mListener);
	}

	@Override
	public View getCardContent(Context context) {
		
		mView = LayoutInflater.from(context).inflate(R.layout.card_assignment, null);

		mTextViewTitle =((TextView) mView.findViewById(R.id.title));
		mTextViewTitle.setText(title);
		
		mTextViewDesc = ( (TextView) mView.findViewById(R.id.description));
		mTextViewDesc.setText(mDesc);
		((TextView)mView.findViewById(R.id.tvBounty)).setText("Bounty:" + mbounty);
		if (mId != -1)
		{
			mView.setId(mId);
			mView.setOnClickListener(mListener);
			mTextViewDesc.setId(mId);
			mTextViewTitle.setId(mId);
			mView.setOnClickListener(mListener);
		}
		
		
		
		if (mIcon != -1)
		{
			ImageView iv = ((ImageView)mView.findViewById(R.id.cardIcon));
			iv.setImageResource(mIcon);
			
			if (mId != -1)
			{
				iv.setId(mId);
				iv.setOnClickListener(mListener);
			}
		}
		
		if(mvideo!=0){
			((ImageView)mView.findViewById(R.id.imageViewVid)).setImageResource(R.drawable.vids_);
		}
		if(mimage!=0){
			((ImageView)mView.findViewById(R.id.imageViewPic)).setImageResource(R.drawable.pics_);
		}
		if(maudio!=0){
			((ImageView)mView.findViewById(R.id.imageViewAud)).setImageResource(R.drawable.auds_);
		}
		return mView;
	}
	
	
	public void setId (int id)
	{
		mId = id;
	}

	
	
	
}
