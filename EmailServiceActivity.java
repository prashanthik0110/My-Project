package com.styfox.contactApp;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class EmailServiceActivity extends Activity {

	Context context;
	public  PendingIntent pendingIntent;
	AlarmManager alarmManager;
	SharedPreferences sharedpreferences;
	String MyPREFERENCES="MyPREFERENCES";
	String triggerTime="triggerTime";
	String mail="mail";
	String defValues="defValues";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context=EmailServiceActivity.this;

		sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		String str=sharedpreferences.getString(triggerTime, defValues);
		if(!str.equalsIgnoreCase(defValues)){
			int index=Integer.parseInt(str);


			Intent intentsOpen = new Intent(this, AlarmReceiver.class);
			intentsOpen.setAction("com.styfox.contactApp.ACTION");
			pendingIntent = PendingIntent.getBroadcast(this,111, intentsOpen, 0);
			alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			if(index==0){
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1*24*60*60*1000, pendingIntent);

			}else if(index==1){
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 7*24*60*60*1000, pendingIntent);

			}else if(index==2){
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*24*60*60*1000, pendingIntent);

			}
			finish();
		}
	}



}
