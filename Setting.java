package com.styfox.contactApp;


import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;  
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Setting extends Activity {

	// Wigets - GUI    
	String MyPREFERENCES="MyPREFERENCES";
	String triggerTime="triggerTime";
	String mail="mail";
	String defValues="defValues"; 

	Spinner spCountries;
	EditText email;
	Button send;
	SharedPreferences sharedpreferences;
	int index=0;
	// Data Source    


	// Adapter     
	ArrayAdapter<String> adapterBusinessType;     

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.setting);
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		String  triggerTimeindex = sharedpreferences.getString(triggerTime, defValues);
		String mailid = sharedpreferences.getString(mail, defValues);
		spCountries = (Spinner) findViewById(R.id.spCountries);
		email=(EditText)findViewById(R.id.et_address_id);
		send=(Button)findViewById(R.id.send);
		String indexstr=triggerTimeindex;
		if(!indexstr.equalsIgnoreCase(defValues)){
			spCountries.setSelection(Integer.parseInt(indexstr));
		}
		if(!mailid.equalsIgnoreCase(defValues)){
			email.setText(mailid);

		}
		spCountries.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v,
					int position, long id) {
				String item = adapter.getItemAtPosition(position).toString();
				index=position;
			} 

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		send.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:


					if(!email.getText().toString().trim().equalsIgnoreCase("")){
						Editor editor = sharedpreferences.edit();
						editor.putString(triggerTime, ""+index);		
						editor.putString(mail, email.getText().toString().trim());
						editor.commit();
						Intent i=new Intent(getApplicationContext(), EmailServiceActivity.class);
						startActivity(i);
						finish();
					}
					

					break;
				}
				return false;
			}
		});

	}
}