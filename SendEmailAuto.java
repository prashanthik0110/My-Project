package com.styfox.contactApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SendEmailAuto extends Activity{
	ArrayList<ContactBean> contactBeanList;
	String address, subject, message, file_path;
	String MyPREFERENCES="MyPREFERENCES";
	String triggerTime="triggerTime";
	String mail="mail";
	String defValues="defValues";
	SharedPreferences sharedpreferences;
	Uri URI = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
		                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		contactBeanList=new ArrayList<ContactBean>();
		writeToExcel();
		sendMail();

		
	}
	public void writeToExcel(){
		String Fnamexls="contactfile"  + ".xls";
		File sdCard = Environment.getExternalStorageDirectory();
		File directory = new File (sdCard.getAbsolutePath() + "/ContactApp");
		directory.mkdirs();
		File file = new File(directory, Fnamexls);

		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));  

		WritableWorkbook workbook;       

		readContacts();     
    
		for (int i = 0; i < contactBeanList.size(); i++) {
			if(contactBeanList.get(i).getName()==null){
				contactBeanList.remove(i);
				i=i-1;
			}  
		}    
		try {
			workbook = Workbook.createWorkbook(file, wbSettings);  
			//workbook.createSheet("Report", 0);
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);

			for (int i = 0; i < contactBeanList.size(); i++) {
				Label label1 = new Label(1,i,""+contactBeanList.get(i).getPhoneNo());
				Label label0 = new Label(0,i,""+contactBeanList.get(i).getName());
				Label label2 = new Label(2,i,""+contactBeanList.get(i).getEmail());

				try {
					sheet.addCell(label1);
					sheet.addCell(label0);
					sheet.addCell(label2);

				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			workbook.write();
			try {
				workbook.close();

			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//createExcel(excelSheet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void readContacts(){
		contactBeanList.clear();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		if (cur.getCount() > 0) {   
			while (cur.moveToNext()) {
				ContactBean cbean=new ContactBean();
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					System.out.println("name : " + name );
					if(name==null){
						continue;
					}
					cbean.setName(name);

					// get the phone number
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
							new String[]{id}, null);
					while (pCur.moveToNext()) {
						String phone = pCur.getString(
								pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						System.out.println("phone" + phone);
						cbean.setPhoneNo(phone);
					} 
					pCur.close();


					// get email and type

					Cursor emailCur = cr.query(        
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",  
							new String[]{id}, null); 
					while (emailCur.moveToNext()) { 
						// This would allow you get several email addresses
						// if the email addresses were stored in an array  
						String email = emailCur.getString(
								emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						String emailType = emailCur.getString(
								emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

						System.out.println("Email " + email );

						cbean.setEmail(email);

					} 
					emailCur.close();


				}
				contactBeanList.add(cbean);
			}
		}
	}  
	public void sendMail(){
		boolean isServiceAvailable=isNetworkAvailable(getApplicationContext());

		if (isServiceAvailable) {
			address = sharedpreferences.getString(mail, defValues);
			subject = "Contacts Update";
			message = "";         

			String emailAddresses[] = { address };
          try{
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					emailAddresses);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		//	emailIntent.setType("plain/text");
		//	emailIntent.setType("application/xls");
			emailIntent.setType("application/vnd.ms-excel");     

			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			File sdCard = Environment.getExternalStorageDirectory();
			URI = Uri.parse("file://" +sdCard.getAbsolutePath() + "/ContactApp"+"/contactfile"  + ".xls");
			if (URI != null)
				emailIntent.putExtra(Intent.EXTRA_STREAM, URI);

			startActivity(emailIntent); 
			finish();
          }catch(Exception e){  
        	  String str=e.toString();
          }
		}else {
			Toast.makeText(getApplicationContext(), "Please connect to internet",Toast.LENGTH_SHORT).show();
		}
		
		
	}
	public  boolean isNetworkAvailable(Context context) {
		ConnectivityManager connection = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = null;
		if (connection != null) {
			nInfo = connection.getActiveNetworkInfo();
		}
		if (nInfo == null || !nInfo.isConnectedOrConnecting()) {
			return false;
		}

		if (nInfo == null || !nInfo.isConnected()) {
			return false;
		}
		if (nInfo != null
				&& ((nInfo.getType() == ConnectivityManager.TYPE_MOBILE) || (nInfo
						.getType() == ConnectivityManager.TYPE_WIFI))) {
			if (nInfo.getState() != NetworkInfo.State.CONNECTED
					|| nInfo.getState() == NetworkInfo.State.CONNECTING) {
				return false;
			}
		}
		return true;
	}
}
