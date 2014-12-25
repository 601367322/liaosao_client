package com.xl.util;

import org.apache.http.Header;
import org.json.JSONObject;

public class JsonHttpResponseHandler extends
		com.loopj.android.http.JsonHttpResponseHandler {

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		// TODO Auto-generated method stub
		onSuccess(response);
	}

	public void onSuccess(JSONObject jo) {

	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers,
			String responseString, Throwable throwable) {
		// TODO Auto-generated method stub
		onFailure();
	}
	
	public void onFailure(){
		
	}
	
}
