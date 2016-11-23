package com.styfox.contactApp;

import java.io.File;

import org.json.JSONException;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EmailActivity extends Activity implements OnClickListener {

	EditText et_address, et_subject, et_message;
	String address, subject, message, file_path;
	Button bt_send, bt_attach; 
	TextView tv_attach;

	private static final int PICK_IMAGE = 100;
	private static final int FILE_SELECT_CODE = 0;

	Uri URI = null;
	int columnindex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.email);
		initializeViews();   
		bt_send.setOnClickListener(this);
		bt_attach.setOnClickListener(this);

	}   

	private void initializeViews() {
		et_address = (EditText) findViewById(R.id.et_address_id);
		et_subject = (EditText) findViewById(R.id.et_subject_id);
		et_message = (EditText) findViewById(R.id.et_message_id);
		bt_send = (Button) findViewById(R.id.bt_send_id);
		bt_attach = (Button) findViewById(R.id.bt_attach_id);
		tv_attach = (TextView) findViewById(R.id.tv_attach_id);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_attach_id:
			openGallery();
			//showFileChooser();
			break;

		case R.id.bt_send_id:
			
			
			boolean isServiceAvailable=isNetworkAvailable(getApplicationContext());

			if (isServiceAvailable) {
				address = et_address.getText().toString();
				subject = et_subject.getText().toString();
				message = et_message.getText().toString();         

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
				Toast.makeText(getApplicationContext(), ""+sdCard.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				URI = Uri.parse("file://" +sdCard.getAbsolutePath() + "/ContactApp"+"/contactfile"  + ".xls");
				if (URI != null)
					emailIntent.putExtra(Intent.EXTRA_STREAM, URI);

				startActivity(emailIntent); 
	          }catch(Exception e){  
	        	  String str=e.toString();
	          }
			}else {
				Toast.makeText(getApplicationContext(), "Please connect to internet",Toast.LENGTH_SHORT).show();
			}
			
        
			break;
    
		}

	}

	private void openGallery() {
		Intent intent = new Intent();
		intent.setType(".xls");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE
				);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case FILE_SELECT_CODE:
	        if (resultCode == RESULT_OK) {
	            // Get the Uri of the selected file 
	            Uri uri = data.getData();
	            // Get the path
	          //  String path = FileUtils.getPath(this, uri);
	            // Get the file instance
	            // File file = new File(path);
	            // Initiate the upload
	        }
	        break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	/*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			columnindex = cursor.getColumnIndex(filePathColumn[0]);
			file_path = cursor.getString(columnindex);
			// Log.e("Attachment Path:", attachmentFile);
			tv_attach.setText(file_path);
			URI = Uri.parse("file://" + file_path);
			cursor.close();
		}
	}*/   
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
