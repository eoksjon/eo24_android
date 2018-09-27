package com.eoksjon24;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class MyApplication extends Application 
{

	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		Log.i("INFO", "Singleton class init call.");
		initSingletons();
	}

	@Override
	public void onLowMemory() 
	{
		super.onLowMemory();
	}

	@Override
	public void onTerminate() 
	{
		super.onTerminate();
	}
	
	protected void initSingletons()
	{
		MySingleton.initInstance();
	}

}
