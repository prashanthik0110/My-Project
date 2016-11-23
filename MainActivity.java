package com.styfox.contactApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

  

import jxl.Cell;
import jxl.CellType;  
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;  
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_PICK_FILE = 1;
   
    
	ArrayList<ContactBean> contactBeanList;
	private TextView filePath;
	private File selectedFile;
	boolean enableimport=false;
	boolean enableEmail=false;   
	ArrayList<ContentProviderOperation> ops;           
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		Button btn=(Button)findViewById(R.id.writeExcel);
		contactBeanList=new ArrayList<ContactBean>();
		btn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub

				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.butonanim));

					enableEmail=true;
					findViewById(R.id.emailbtn).getBackground().setAlpha(255);
					writeToExcel();

					break;         
				}          
				return false;  
			}   
		});
		btn.setBackgroundResource(R.drawable.btnimgae);

		Button emailbtn=(Button)findViewById(R.id.emailbtn);
		emailbtn.setBackgroundResource(R.drawable.btnimgae);   
		emailbtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.butonanim));

					if(enableEmail){
						Intent i=new Intent(getApplicationContext(), EmailActivity.class);
						startActivity(i);

					}else{
						Toast.makeText(getApplicationContext(), "Write the Contacts to Excel", Toast.LENGTH_SHORT).show();
					}
					break;
				}
				return false;
			}
		});

		Button importBtn=(Button)findViewById(R.id.importExcel);
		importBtn.setBackgroundResource(R.drawable.btnimgae);
		importBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.butonanim));

					Intent intent = new Intent(getApplicationContext(), FilePicker.class);            
					startActivityForResult(intent, REQUEST_PICK_FILE);
					break;
				}  
				return false;
			}
		});   
		Button createbtn=(Button)findViewById(R.id.ImportFromExcel);
		createbtn.setBackgroundResource(R.drawable.btnimgae);
		createbtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					try {
						arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.butonanim));

						if(enableimport){
							createContact();
						}else{
							Toast.makeText(getApplicationContext(), "Please select the File to import", Toast.LENGTH_SHORT).show();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}  
				return false;    
			}   
		});
		filePath = (TextView)findViewById(R.id.file_path);
		ImageView setting=(ImageView)findViewById(R.id.settingview);
		setting.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
				// TODO Auto-generated method stub
					Intent i=new Intent(getApplicationContext(), Setting.class);
					startActivity(i);
					break;
				}   
				return false;
			}
		});


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

				Toast.makeText(getApplicationContext(), "Contacts added to ExcelSheet", Toast.LENGTH_SHORT).show();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == RESULT_OK) {

			switch(requestCode) {

			case REQUEST_PICK_FILE:

				if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
					try{
						selectedFile = new File
								(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
						filePath.setText(selectedFile.getPath());
						enableimport=true;
						findViewById(R.id.ImportFromExcel).getBackground().setAlpha(255);
					}catch(Exception e){
						String str=e.toString();
					}
				}
				break;
			}
		}   
	}
	public void createContact() throws IOException  {
		File inputWorkbook = new File(selectedFile.getPath());
		Workbook w;
		ArrayList<ContactBean> creatContactBeanLst=new ArrayList<ContactBean>();
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines

			for (int i = 0; i < sheet.getRows(); i++) {
				ContactBean bean=new ContactBean();
				for (int j = 0; j < sheet.getColumns(); j++) {
					Cell cell = sheet.getCell(j, i);
					CellType type = cell.getType();

					if(j==0){
						bean.setName(cell.getContents());
					}else if(j==1){
						bean.setPhoneNo(cell.getContents());
					}else if(j==2){
						bean.setEmail(cell.getContents());
					}

				}
				creatContactBeanLst.add(bean); 
			}
			for (int i = (0); i < creatContactBeanLst.size(); i++) {
				//addContact(creatContactBeanLst.get(i));
				insertContact(creatContactBeanLst.get(i));  
			}
			Toast.makeText(getBaseContext(), "Contacts is successfully added", Toast.LENGTH_SHORT).show();
  
			/* try{
				// Executing all the insert operations as a single database transaction
				getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
				Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
			}catch (RemoteException e) {					
				e.printStackTrace();
			}catch (OperationApplicationException e) {
				e.printStackTrace();
			}*/
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}  
	
	public void insertContact(ContactBean contact)
	{
		ops =new ArrayList<ContentProviderOperation>();

		int rawContactID = ops.size();

		// Adding insert operation to operations list 
		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null)
				.build());

		// Adding insert operation to operations list
		// to insert display name in the table ContactsContract.Data
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
				.withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, contact.getName())
				.build());

		// Adding insert operation to operations list
		// to insert Mobile Number in the table ContactsContract.Data
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
				.withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER,  contact.getPhoneNo())
				.withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
				.build());

		// Adding insert operation to operations list
		// to insert Work Email in the table ContactsContract.Data
		ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
				.withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
				.withValue(Email.ADDRESS,  contact.getEmail())
				.withValue(Email.TYPE, Email.TYPE_WORK)
				.build());				   
		try{
			// Executing all the insert operations as a single database transaction
			ContentProviderResult[] res=getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		}catch (RemoteException e) {					
			e.printStackTrace();
		}catch (OperationApplicationException e) {
			e.printStackTrace();
		}

	}

}
