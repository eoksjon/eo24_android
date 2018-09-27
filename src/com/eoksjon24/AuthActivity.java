package com.eoksjon24;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AuthActivity extends Activity implements OnClickListener
{
	
	ImageButton btnMID;
	ImageButton btn1;
	ImageButton btn2;
	ImageButton btn3;
	ImageButton btn4;
	ImageButton btn5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setAuthActivity(this);
		setContentView(R.layout.layout_auth);
		
		btnMID = (ImageButton) findViewById(R.id.imgbtnMID);
		btn1 = (ImageButton) findViewById(R.id.imgbtn1);
		btn2 = (ImageButton) findViewById(R.id.imgbtn2);
		btn3 = (ImageButton) findViewById(R.id.imgbtn3);
		btn4 = (ImageButton) findViewById(R.id.imgbtn4);
		btn5 = (ImageButton) findViewById(R.id.imgbtn5);
		
		btnMID.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		Log.d("AuthView", "OnClick");
		// TODO Auto-generated method stub
		Uri uri;
		Intent intent;
		switch (v.getId())
		{
			case R.id.imgbtnMID:
	            intent = new Intent(v.getContext(), RegistrationActivity.class);
	            startActivity(intent);	
	            break;
			case R.id.imgbtn1:
				uri = Uri.parse("https://www.seb.ee/ip/ipank.p?lang=EST"); // SEB
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case R.id.imgbtn2:
				uri = Uri.parse("https://www.swedbank.ee/touch/payments"); // Swedbank
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case R.id.imgbtn3:
				uri = Uri.parse("https://www2.danskebank.ee/ibank/login/login"); // Danskebank
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case R.id.imgbtn4:
				uri = Uri.parse("https://netbank.nordea.com/pnbwap/login.do"); // Nordea
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case R.id.imgbtn5:
				uri = Uri.parse("http://www.lhv.ee"); // LHV
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
		}
	}

}
