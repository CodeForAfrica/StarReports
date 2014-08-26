package org.codeforafrica.starreports;

import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReportRemoteViewActivity extends BaseActivity{
	//private WebView mWebview;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_web_view);
    	
    	WebView myWebView = (WebView) findViewById(R.id.web_engine);
    	myWebView.setWebViewClient(new WebViewClient());
    	
        //mWebview = new WebView(this);
    	myWebView.loadUrl(getIntent().getStringExtra("postUrl"));
        //setContentView(mWebview);
        //mWebview.setWebViewClient(new WebViewClient());
        //Toast.makeText(getApplicationContext(), getIntent().getStringExtra("url"), duration)
	}
}
