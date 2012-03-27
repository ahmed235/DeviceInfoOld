package com.jphilli85.deviceinfo.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jphilli85.deviceinfo.DeviceInfo;
import com.jphilli85.deviceinfo.R;
import com.jphilli85.deviceinfo.app.GroupListFragment.OnGroupSelectedListener;
import com.jphilli85.deviceinfo.data.DeviceInfoDatabaseHelper;

public class DeviceInfoActivity extends FragmentActivity implements 
		OnGroupSelectedListener {

	private SQLiteDatabase mDatabase;
		
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		if (DeviceInfo.sAppContext == null) DeviceInfo.sAppContext = getApplicationContext();
		
		mDatabase = (new DeviceInfoDatabaseHelper(this)).getReadableDatabase();
		
		setContentView(R.layout.main);			
	}

	@Override
	public void onGroupSelected(int id) {
		//Toast.makeText(this, "Group " + id + " selected", Toast.LENGTH_SHORT).show();		
	}
}