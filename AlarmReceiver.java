package com.styfox.contactApp;
  
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	private final String SOMEACTION = "com.styfox.contactApp.ACTION";
	

   
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (SOMEACTION.equals(action)) {
			try{
			        Intent i = new Intent(context, SendEmailAuto.class);
			        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(i);    
			       
		 			}catch(Exception e){
		} 
		}else{
			try{
			    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			        Intent i = new Intent(context, EmailServiceActivity.class);
			        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(i);    
			    }    
		 			}catch(Exception e){  
				String str=e.toString();
			}
		}                          
		}                                 

}