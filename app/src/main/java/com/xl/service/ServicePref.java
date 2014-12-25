package com.xl.service;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface ServicePref {

	@DefaultBoolean(false)
	boolean isStarted();
	
	@DefaultLong(PushService.INITIAL_RETRY_INTERVAL)
	long retryInterval();
}
