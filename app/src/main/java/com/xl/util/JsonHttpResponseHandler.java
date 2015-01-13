package com.xl.util;

import org.apache.http.Header;
import org.json.JSONArray;
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

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        onFailure();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        onFailure();
    }

    public void onFailure(){
		
	}
	
}
