package com.eoksjon24;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eoksjon24.ProfileActivity.UserSetAvatarTask;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.os.AsyncTask;
import android.provider.MediaStore;

public class NewAuctionActivity extends Activity 
{

    private static final int CAMERA_REQUEST = 1888; 
    private static final int CROP_REQUEST = 1889; 
    private static final int PICK_FROM_FILE_REQUEST = 1890; 

    final int CHECKED_ON = 1;
	final int CHECKED_OFF = 0;

	final int SECTION_ENGLISH_STEP2 = 2;
	final int SECTION_ENGLISH_STEP3 = 3;
	final int SECTION_ENGLISH_STEP4 = 4;
	final int SECTION_ENGLISH_STEP5 = 5;
	final int SECTION_ENGLISH_STEP6 = 6;
	
	final int ID_ROOT_CATALOG = -1;
	
	public Stack<Integer> lifoPID = new Stack<Integer>(); // stack ID for cat. navigation 
	public Stack<String> lifoPath = new Stack<String>(); // stack Path for cat. navigation 
	public ArrayList<Integer> activeID = new ArrayList<Integer>(); // store chosen ID-s
	public ArrayList<String> activePath = new ArrayList<String>(); // store chosen Path-s
	public ArrayList<String> pics = new ArrayList<String>(); // store pic-s for upload. first pic will be main
	
	private GetCatalogTask mCatalogTask = null;
	private AddAuctionTask mAddAuctionTask = null;

	private ScrollView svMain;

	private RadioGroup rgAuctionType;
	private Button btStep1_end_button;
	private Button btStep2_end_button;
	private Button btStep2a_end_button;
	private Button btStep3_end_button;
	private Button btStep4_end_button;
	private Button btStep5_end_button;
	private Button btStep6_end_button;
	
	private View vStep1;
	private View vStep2;
	private View vStep3;
	private View vStep4;
	private View vStep5;
	private View vStep6;
	
	private TextView tvStep2textGetCat;
	private TextView tvStep2CategoriesPath;
	private EditText etStep2Title;
	private EditText etStep2Desc;
	private EditText etStep2Location;

	private EditText etStep4StartPrice;
	private EditText etStep4BuyItNowPrice;
	private CheckBox cbStep4CanChange;
	private RadioGroup rgStep4Condition;
	private EditText etStep4StartDate;
	private EditText etStep4StartTime;
	private Spinner spStep4AuctionDuration;
	private TextView tvStep4AuctionEnd;
	private RadioGroup rgStep4ExtClosure;
	private CheckBox cbStep4PayDeposit;
	private CheckBox cbStep4PayAuction24;
	private CheckBox cbStep4PayCash;
	private CheckBox cbStep4PayViaBank;
	private CheckBox cbStep4TransportCollect;
	private CheckBox cbStep4TransportAccord;

	
	private ViewGroup layoutPics;
	private ViewGroup layoutStep2GetCatSection;
	private ViewGroup layoutStep2ActiveCat;
	private ViewGroup layoutStep2TitleSection;
	private ViewGroup layoutActivePaths;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		MySingleton.getInstance().setNewAuctionActivity(this);
		setContentView(R.layout.activity_new_auction);
		if (savedInstanceState == null) 
		{
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_auction, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() 
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getNewAuctionActivity());
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(R.string.prompt_new_auction_exit);
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
		    	MySingleton.getInstance().getNewAuctionActivity().finish();
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		dialog.show();                			
	}
	
	public void attachLayouts(View rootView)
	{
    	ViewGroup stepsLayout = (ViewGroup) rootView.findViewById(R.id.layoutSteps);
    	stepsLayout.removeAllViews();
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vStep1 = inflater.inflate(R.layout.layout_eng_step1, null);
		vStep2 = inflater.inflate(R.layout.layout_eng_step2, null);
		vStep3 = inflater.inflate(R.layout.layout_eng_step3, null);
		vStep4 = inflater.inflate(R.layout.layout_eng_step4, null);
		vStep5 = inflater.inflate(R.layout.layout_eng_step5, null);
		vStep6 = inflater.inflate(R.layout.layout_eng_step6, null);
		vStep2.setVisibility(View.GONE);
		vStep3.setVisibility(View.GONE);
		vStep4.setVisibility(View.GONE);
		vStep5.setVisibility(View.GONE);
		vStep6.setVisibility(View.GONE);
		stepsLayout.addView(vStep1);
		stepsLayout.addView(vStep2);
		stepsLayout.addView(vStep3);
		stepsLayout.addView(vStep4);
		stepsLayout.addView(vStep5);
		stepsLayout.addView(vStep6);
	}

	
	public void defineViews(View rootView)
	{
		svMain = (ScrollView) rootView.findViewById(R.id.svMain);
		
		btStep1_end_button = (Button) rootView.findViewById(R.id.step1_end_button);
		btStep2_end_button = (Button) rootView.findViewById(R.id.step2_end_button);
		btStep2a_end_button = (Button) rootView.findViewById(R.id.step2a_end_button);
		btStep3_end_button = (Button) rootView.findViewById(R.id.step3_end_button);
		btStep4_end_button = (Button) rootView.findViewById(R.id.step4_end_button);
		btStep5_end_button = (Button) rootView.findViewById(R.id.step5_end_button);
		btStep6_end_button = (Button) rootView.findViewById(R.id.step6_end_button);
		OnClickListener oclStepEndClick = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch(v.getId())
				{
				case R.id.step1_end_button :
					MySingleton.getInstance().getNewAuctionActivity().btStep1_end_button.setVisibility(View.GONE);
					MySingleton.getInstance().getNewAuctionActivity().vStep2.setVisibility(View.VISIBLE);
					for (int i = 0; i < MySingleton.getInstance().getNewAuctionActivity().rgAuctionType.getChildCount(); i++) 
					{
						MySingleton.getInstance().getNewAuctionActivity().rgAuctionType.getChildAt(i).setEnabled(false);
					}					
					break;
				case R.id.step2_end_button :
					MySingleton.getInstance().getNewAuctionActivity().btStep2_end_button.setVisibility(View.GONE);
					MySingleton.getInstance().getNewAuctionActivity().vStep3.setVisibility(View.VISIBLE);
					break;
				case R.id.step2a_end_button :
//					MySingleton.getInstance().getNewAuctionActivity().btStep2a_end_button.setVisibility(View.GONE);
//					MySingleton.getInstance().getNewAuctionActivity().layoutStep2GetCatSection.setVisibility(View.GONE);
//					MySingleton.getInstance().getNewAuctionActivity().layoutStep2ActiveCat.setVisibility(View.VISIBLE);
//					MySingleton.getInstance().getNewAuctionActivity().layoutStep2TitleSection.setVisibility(View.VISIBLE);
					break;
				case R.id.step3_end_button :
					MySingleton.getInstance().getNewAuctionActivity().btStep3_end_button.setVisibility(View.GONE);
					MySingleton.getInstance().getNewAuctionActivity().vStep4.setVisibility(View.VISIBLE);
					break;
				case R.id.step4_end_button :
					MySingleton.getInstance().getNewAuctionActivity().btStep4_end_button.setVisibility(View.GONE);
					MySingleton.getInstance().getNewAuctionActivity().vStep5.setVisibility(View.VISIBLE);
					break;
				case R.id.step5_end_button :
					MySingleton.getInstance().getNewAuctionActivity().btStep5_end_button.setVisibility(View.GONE);
//					MySingleton.getInstance().getNewAuctionActivity().vStep6.setVisibility(View.VISIBLE);
					MySingleton.getInstance().getNewAuctionActivity().addAuction();
					break;
				case R.id.step6_end_button :
//					MySingleton.getInstance().getNewAuctionActivity().btStep6_end_button.setVisibility(View.GONE);
//					MySingleton.getInstance().getNewAuctionActivity().addAuction();
					break;
				}
				
				View view = MySingleton.getInstance().getNewAuctionActivity().getCurrentFocus();
				if (view != null)
				{  
				    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				MySingleton.getInstance().getNewAuctionActivity().svMain.post(new Runnable()
				{ 
			        public void run() 
			        { 
			        	//svMain.fullScroll(svMain.FOCUS_DOWN);
			        	//svMain.smoothScrollTo(0, svMain.getBottom());
			        } 
				});
			}			
		};
		btStep1_end_button.setOnClickListener(oclStepEndClick);
		btStep2_end_button.setOnClickListener(oclStepEndClick);
//		btStep2a_end_button.setOnClickListener(oclStepEndClick);
		btStep3_end_button.setOnClickListener(oclStepEndClick);
		btStep4_end_button.setOnClickListener(oclStepEndClick);
		btStep5_end_button.setOnClickListener(oclStepEndClick);
		btStep6_end_button.setOnClickListener(oclStepEndClick);

		
		//-----------------------STEP1---------------------------
		rgAuctionType = (RadioGroup) rootView.findViewById(R.id.rg_auction_type);		
		OnCheckedChangeListener occlRG = new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group,
					int checkedId)
			{
				checkStepsState();
			}	
		};	
		rgAuctionType.setOnCheckedChangeListener(occlRG);

		//---------------------STEP2------------------------------
		tvStep2textGetCat = (TextView) rootView.findViewById(R.id.textGetCat);
		tvStep2CategoriesPath = (TextView) rootView.findViewById(R.id.txtCategoriesPath);
		layoutActivePaths = (ViewGroup) rootView.findViewById(R.id.layoutActiveCat);
		
		etStep2Title = (EditText) rootView.findViewById(R.id.editTitle);
		etStep2Desc = (EditText) rootView.findViewById(R.id.editDesc);
		etStep2Location = (EditText) rootView.findViewById(R.id.editLocation);	
		//TODO MyTextWatcher
		TextWatcher tw = new TextWatcher()
		{

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				try
				{
					if (s.toString().isEmpty() || Double.parseDouble(s.toString()) == 0)
					{
						cbStep4CanChange.setChecked(false);
					}					
				}
				catch (NumberFormatException e)
				{
					
				}
				checkStepsState();
			}

			@Override
			public void afterTextChanged(Editable s)
			{
			}			
		};
		etStep2Title.addTextChangedListener(tw);
		etStep2Location.addTextChangedListener(tw);
		layoutStep2ActiveCat = (ViewGroup) rootView.findViewById(R.id.layoutStep2ActiveCat);
		layoutStep2TitleSection = (ViewGroup) rootView.findViewById(R.id.layoutStep2TitleSection);
		layoutStep2ActiveCat.setVisibility(View.GONE);
		layoutStep2GetCatSection = (ViewGroup) rootView.findViewById(R.id.layoutGetCatSection);
		layoutStep2TitleSection.setVisibility(View.GONE);
		layoutStep2GetCatSection.setVisibility(View.VISIBLE);

		//-------------------STEP3-------------------------------
		layoutPics = (ViewGroup) rootView.findViewById(R.id.layoutUploadImages);

		//-------------------STEP4-------------------------------
		etStep4StartPrice = (EditText) rootView.findViewById(R.id.etStartPrice);
		etStep4BuyItNowPrice = (EditText) rootView.findViewById(R.id.etBuyItNowPrice);
		cbStep4CanChange = (CheckBox) rootView.findViewById(R.id.cbox_can_change);
		rgStep4Condition = (RadioGroup) rootView.findViewById(R.id.rg_condition);
		etStep4StartDate = (EditText) rootView.findViewById(R.id.et_start_date);
		etStep4StartTime = (EditText) rootView.findViewById(R.id.et_start_time);
		spStep4AuctionDuration = (Spinner) rootView.findViewById(R.id.spinner_auction_duration);
		tvStep4AuctionEnd = (TextView) rootView.findViewById(R.id.tv_auction_end);
		rgStep4ExtClosure = (RadioGroup) rootView.findViewById(R.id.rg_ext_closure);
		cbStep4PayDeposit = (CheckBox) rootView.findViewById(R.id.cbox_pay_deposit);
		cbStep4PayAuction24 = (CheckBox) rootView.findViewById(R.id.cbox_pay_auction24);
		cbStep4PayCash = (CheckBox) rootView.findViewById(R.id.cbox_pay_cash);
		cbStep4PayViaBank = (CheckBox) rootView.findViewById(R.id.cbox_pay_via_bank);
		cbStep4TransportCollect = (CheckBox) rootView.findViewById(R.id.cbox_transport_collect);
		cbStep4TransportAccord = (CheckBox) rootView.findViewById(R.id.cbox_transport_accord);
		
		etStep4StartPrice.addTextChangedListener(tw);
		etStep4BuyItNowPrice.addTextChangedListener(tw);
		etStep4StartDate.addTextChangedListener(tw);
		//etStep4StartTime.addTextChangedListener(tw);
		
		rgStep4Condition.setOnCheckedChangeListener(occlRG);
		rgStep4ExtClosure.setOnCheckedChangeListener(occlRG);

		CompoundButton.OnCheckedChangeListener occlCB = new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (buttonView.getId() == R.id.cbox_can_change)
				{
					try
					{
						String s = etStep4BuyItNowPrice.getText().toString();
						if(s.isEmpty() || Double.parseDouble(s) == 0)
						{
							buttonView.setChecked(false);
							
							AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getNewAuctionActivity());
		            		dialog.setTitle(R.string.app_name);
		            		dialog.setMessage(getResources().getString(R.string.text_cannot_set_item_exchange));
		            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
		            		dialog.show();                								
						}					
					}
					catch (NumberFormatException e)
					{
						
					}
				}
				checkStepsState();
			}
		};
		cbStep4CanChange.setOnCheckedChangeListener(occlCB);
		cbStep4PayDeposit.setOnCheckedChangeListener(occlCB);
		cbStep4PayAuction24.setOnCheckedChangeListener(occlCB);
		cbStep4PayCash.setOnCheckedChangeListener(occlCB);
		cbStep4PayViaBank.setOnCheckedChangeListener(occlCB);
		cbStep4TransportCollect.setOnCheckedChangeListener(occlCB);
		cbStep4TransportAccord.setOnCheckedChangeListener(occlCB);

	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.array_of_auction_duration, android.R.layout.simple_spinner_item);
	         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spStep4AuctionDuration.setAdapter(adapter);
	    spStep4AuctionDuration.setSelection(6);
	    OnLayoutChangeListener olclAuctionDuration = new OnLayoutChangeListener()
	    {
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom)
			{
				showAuctionEndTime();
			}
	    };
	    spStep4AuctionDuration.addOnLayoutChangeListener(olclAuctionDuration);
	    
	    final Calendar c = Calendar.getInstance();
	    etStep4StartDate.setText(new SimpleDateFormat("dd.MM.yyyy").format(c.getTime()));
		etStep4StartTime.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));

		etStep4StartDate.setOnClickListener(new OnClickListener() 
		{
	        @Override
	        public void onClick(View v) 
	        {
	            // TODO Auto-generated method stub
	            Calendar mcurrentTime = Calendar.getInstance();
	            int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
	            int month = mcurrentTime.get(Calendar.MONTH);
	            int year = mcurrentTime.get(Calendar.YEAR);
	            DatePickerDialog mDatePicker;
	            mDatePicker = new DatePickerDialog(MySingleton.getInstance().getNewAuctionActivity(), new DatePickerDialog.OnDateSetListener() 
	            {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						MySingleton.getInstance().getNewAuctionActivity().etStep4StartDate.setText(
								String.format("%02d.%02d.%d", dayOfMonth, monthOfYear+1, year) );						
						MySingleton.getInstance().getNewAuctionActivity().showAuctionEndTime();
					}
	            }, year, month, day);
	            mDatePicker.setTitle(R.string.text_select_date);
	            mDatePicker.show();

	        }
	    });		
		
		
		etStep4StartTime.setOnClickListener(new OnClickListener() 
		{
	        @Override
	        public void onClick(View v) 
	        {
	            // TODO Auto-generated method stub
	            Calendar mcurrentTime = Calendar.getInstance();
	            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
	            int minute = mcurrentTime.get(Calendar.MINUTE);
	            TimePickerDialog mTimePicker;
	            mTimePicker = new TimePickerDialog(MySingleton.getInstance().getNewAuctionActivity(), new TimePickerDialog.OnTimeSetListener() 
	            {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute)
					{
						MySingleton.getInstance().getNewAuctionActivity().etStep4StartTime.setText(
								String.format("%02d:%02d", hourOfDay, minute) );	
						MySingleton.getInstance().getNewAuctionActivity().showAuctionEndTime();
						
					}
	            }, hour, minute, true);//Yes 24 hour time
	            mTimePicker.setTitle(R.string.text_select_time);
	            mTimePicker.show();

	        }
	    });		
		
		
		
		
		
		
		
		
		showCatalogButtons(ID_ROOT_CATALOG, "");
	}
	
	public void checkStep2State()
	{
		layoutStep2ActiveCat.setVisibility(activeID.size() > 0 ? View.VISIBLE : View.GONE);	
		layoutStep2GetCatSection.setVisibility(activeID.size() > 0 ? View.GONE : View.VISIBLE);
		if (activeID.size() == 0)
		{
			showCatalogButtons(ID_ROOT_CATALOG, "");
		}
	}
	
	public void checkStepsState()
	{
		
		//-------------------STEP 1-------------------------------
		
		MySingleton.getInstance().getNewAuctionActivity().
			btStep1_end_button.setEnabled(rgAuctionType.getCheckedRadioButtonId() != -1);
		
		//-------------------STEP 2-------------------------------
		tvStep2textGetCat.setText(activeID.size() > 0 ? R.string.text_step2_get_cat2 : R.string.text_step2_get_cat);
		if(MySingleton.getInstance().getNewAuctionActivity().etStep2Title.getText().toString().length() > 0 &&
				MySingleton.getInstance().getNewAuctionActivity().etStep2Location.getText().toString().length() > 0 &&
				activeID.size() > 0 && activeID.size() <= 3)
		{
			MySingleton.getInstance().getNewAuctionActivity().btStep2_end_button.setEnabled(true);					
		}
		else
		{
			MySingleton.getInstance().getNewAuctionActivity().btStep2_end_button.setEnabled(false);					
		}		
		
		//-------------------STEP 4-------------------------------
		if(etStep4StartPrice.getText().toString().length() > 0 &&
				rgStep4Condition.getCheckedRadioButtonId() != -1 &&
				etStep4StartDate.getText().toString().length() > 0 &&
				etStep4StartTime.getText().toString().length() > 0 &&
				rgStep4ExtClosure.getCheckedRadioButtonId() != -1 &&
				(cbStep4PayDeposit.isChecked() ||
						cbStep4PayAuction24.isChecked() ||
						cbStep4PayCash.isChecked() ||
						cbStep4PayViaBank.isChecked()) &&	
				(cbStep4TransportCollect.isChecked() ||
						cbStep4TransportAccord.isChecked())	
			)
		{
			MySingleton.getInstance().getNewAuctionActivity().btStep4_end_button.setEnabled(true);					
		}
		else
		{
			MySingleton.getInstance().getNewAuctionActivity().btStep4_end_button.setEnabled(false);					
		}		
		
		//-------------------STEP 5-------------------------------
		MySingleton.getInstance().getNewAuctionActivity().btStep5_end_button.setEnabled(true);					
		
		//-------------------STEP 6-------------------------------
		MySingleton.getInstance().getNewAuctionActivity().btStep6_end_button.setEnabled(true);					
		
		
		
	}
	
	public void showCatalogButtons(int id, String path)
	{
		Log.i("INFO", "showCatalogButtons");
		if (mCatalogTask != null)
		{
			return;
		}
		checkStepsState();
		//checkStep2State();
		lifoPID.push(id);
		lifoPath.push(path);
		showPath();
		mCatalogTask = new GetCatalogTask(id);
		mCatalogTask.execute((Void) null);
	}
	
	public void showPath()
	{
		String path = "";
		for (int i = 0; i < lifoPath.size(); i++)
		{
			path += (lifoPID.elementAt(i) == ID_ROOT_CATALOG ? "" : "&#8594;") + lifoPath.elementAt(i);
		}
		Log.i("PATH", path);
		tvStep2CategoriesPath.setText(Html.fromHtml(path));		
	}
	
	public void showActivePaths()
	{
		layoutActivePaths.removeAllViews();
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	if (activePath.size() > 0)
    	{
    		for (int i = 0; i < activePath.size(); i++)
			{
        		View vPathItem = inflater.inflate(R.layout.layout_active_cat_item, null);
        		TextView tvPath = (TextView) vPathItem.findViewById(R.id.txtCategoriesPath);		
        		tvPath.setText(Html.fromHtml(activePath.get(i)));
        		ImageButton ibRemove = (ImageButton) vPathItem.findViewById(R.id.btnRemove);
                ibRemove.setId(i);
                OnClickListener oclRemove = new OnClickListener()
        		{
    				@Override
    				public void onClick(View v)
    				{
    					activeID.remove(v.getId());
    					activePath.remove(v.getId());
    					MySingleton.getInstance().getNewAuctionActivity().checkStepsState();
    					MySingleton.getInstance().getNewAuctionActivity().checkStep2State();
    					MySingleton.getInstance().getNewAuctionActivity().showActivePaths();
    				}            			
        		};
        		ibRemove.setOnClickListener(oclRemove);
        		layoutActivePaths.addView(vPathItem, i);	            
			}
    	}
	}
	
	public void showAuctionEndTime()
	{
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm"); 
		String date = etStep4StartDate.getText().toString() + " " + etStep4StartTime.getText().toString();
		Date myDate;
	    final Calendar c = Calendar.getInstance();
		try 
		{
		    myDate = df.parse(date);
		    c.set(1900 + myDate.getYear(), myDate.getMonth() + 1, myDate.getDate(), myDate.getHours(), myDate.getMinutes());
		} 
		catch (ParseException e) 
		{
		    e.printStackTrace();
		}
		
		int [] period_list = { 1, 2, 3, 4, 5, 6, 7, 9, 10, 14, 28 };
		int days = period_list[spStep4AuctionDuration.getSelectedItemPosition()];
		String s = getResources().getString(R.string.text_step4_auction_end);
		c.add(Calendar.DATE, days);
		tvStep4AuctionEnd.setText(c.getTime().toLocaleString());
		Log.i("EA24", s + " " + c.getTime().toLocaleString());
	}
	
	public void showPicturesList()
	{
		Log.i("INFO", "showPicturesList");

		layoutPics.removeAllViews();
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	if (pics.size() > 0)
    	{
    		for (int i = 0; i < pics.size(); i++)
    		{
        		View vPicItem = inflater.inflate(R.layout.layout_upload_pic_item, null);
        		
        		ImageView ivPic = (ImageView) vPicItem.findViewById(R.id.imgPic);
                byte[] decodedString = Base64.decode(pics.get(i), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivPic.setImageBitmap(decodedByte);	
                
                ImageButton ibMoveUp = (ImageButton) vPicItem.findViewById(R.id.btnMoveUp);
                ibMoveUp.setId(i);
                ImageButton ibMoveDown = (ImageButton) vPicItem.findViewById(R.id.btnMoveDown);
                ibMoveDown.setId(i);
                ImageButton ibRemove = (ImageButton) vPicItem.findViewById(R.id.btnRemove);
                ibRemove.setId(i);
                
        		OnClickListener oclRemove = new OnClickListener()
        		{
    				@Override
    				public void onClick(View v)
    				{
    					pics.remove(v.getId());
    					MySingleton.getInstance().getNewAuctionActivity().showPicturesList();
    				}            			
        		};
        		ibRemove.setOnClickListener(oclRemove);
        		
        		OnClickListener oclMoveUp = new OnClickListener()
        		{
    				@Override
    				public void onClick(View v)
    				{
    					if (pics.size() > 1 && v.getId() > 0)
						{
        					int old_index = v.getId();
    						String temp = pics.get(old_index);
    						pics.remove(old_index);
    						pics.add(--old_index, temp);
        					MySingleton.getInstance().getNewAuctionActivity().showPicturesList();					
						}
    				}            			
        		};
        		ibMoveUp.setOnClickListener(oclMoveUp);
        		
        		OnClickListener oclMoveDown = new OnClickListener()
        		{
    				@Override
    				public void onClick(View v)
    				{
    					if (pics.size() > 1 && v.getId() < pics.size() - 1)
						{
        					int old_index = v.getId();
    						String temp = pics.get(old_index);
    						pics.remove(old_index);
    						pics.add(++old_index, temp);
        					MySingleton.getInstance().getNewAuctionActivity().showPicturesList();					
						}
    				}            			
        		};
        		ibMoveDown.setOnClickListener(oclMoveDown);
               
                layoutPics.addView(vPicItem, i);	            
    		}    		
    	}
    	else
    	{
    		View vEmptyContent = inflater.inflate(R.layout.layout_empty_content, null);
            layoutPics.addView(vEmptyContent);	               		
    	}
	}
	
	public void addAuction()
	{		
		Log.i("INFO", "addAuction");
		if (mAddAuctionTask != null)
		{
			return;
		}
		mAddAuctionTask = new AddAuctionTask();
		mAddAuctionTask.execute((Void) null);
	}
	
	
	
	
	
	
	
	
	
	public static class PlaceholderFragment extends Fragment 
	{

		public PlaceholderFragment() 
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_new_auction, container, false);
			MySingleton.getInstance().getNewAuctionActivity().attachLayouts(rootView);			
			MySingleton.getInstance().getNewAuctionActivity().defineViews(rootView);			

	        Button photoButton = (Button) rootView.findViewById(R.id.button_bmp_from_camera_button);		
	        photoButton.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
	                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
	                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	                startActivityForResult(cameraIntent, CAMERA_REQUEST); // second param may be = 1
	            }
	        });		
	        Button imgSDButton = (Button) rootView.findViewById(R.id.button_bmp_from_sd_button);		
	        imgSDButton.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
	            	Intent intent = new Intent();
	            	intent.setType("image/*");
	            	intent.setAction(Intent.ACTION_GET_CONTENT);
	            	startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE_REQUEST);	            	 
	            }
	        });		
			
			
			
			
			
			
			
			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) 
	    {  
	        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) 
	        {  
	        	File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
	        	try
	        	{
	        		cropCapturedImage(Uri.fromFile(file));
	        	}
	        	catch(ActivityNotFoundException aNFE)
	        	{
	        		String errorMessage = "Sorry - your device doesn't support the crop action!";
	        		Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
	        		toast.show();	        		
	        	}
	        }  
	        else if (requestCode == CROP_REQUEST && resultCode == RESULT_OK)
	        {
	        	Bitmap photo = (Bitmap) data.getExtras().getParcelable("data"); 
	        	Bitmap big_photo = Bitmap.createScaledBitmap(photo, 640, 480, false);
	            //imgAvatar.setImageBitmap(photo);
	            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
//	            photo.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
	            big_photo.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
	            Log.i("COMPRESS", "dimensions: width=" + photo.getWidth() + ", height=" + photo.getHeight());
	            byte[] byteArray = byteArrayOutputStream .toByteArray();
	            String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
	            MySingleton.getInstance().getNewAuctionActivity().pics.add(encoded);
	            Log.i("BITMAP A", "array: len=" + MySingleton.getInstance().getNewAuctionActivity().pics.size());
	            
	            
	            MySingleton.getInstance().getNewAuctionActivity().showPicturesList();
	            
	            
	            
	            // MySingleton.getInstance().setUserAvatarBASE64(encoded);	            
	            //((ProfileActivity)getActivity()).putUserAvatarToServer();
	            
	        } else if (requestCode == PICK_FROM_FILE_REQUEST && resultCode == RESULT_OK)
	        {
	        	Uri selectedImage = data.getData();
	            try
	        	{
	        		cropCapturedImage(selectedImage);
	        	}
	        	catch(ActivityNotFoundException aNFE)
	        	{
	        		String errorMessage = "Sorry - your device doesn't support the crop action!";
	        		Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
	        		toast.show();	        		
	        	}
	        }
	    }
		
		public void cropCapturedImage(Uri picUri)
		{
			 //call the standard crop action intent 
			 Intent cropIntent = new Intent("com.android.camera.action.CROP");
			 //indicate image type and Uri of image
			 cropIntent.setDataAndType(picUri, "image/*");
			 //set crop properties
			 cropIntent.putExtra("crop", "true");
			 //cropIntent.putExtra("scale", true); //my
			 cropIntent.putExtra("scaleUpIfNeeded", true);
			 //indicate aspect of desired crop
			 cropIntent.putExtra("aspectX", 4); //4f
			 cropIntent.putExtra("aspectY", 3); //3f
			 //indicate output X and Y
			 cropIntent.putExtra("outputX", 640); //cropIntent.putExtra("outputX", 640);
			 cropIntent.putExtra("outputY", 480); //cropIntent.putExtra("outputY", 480);
			 
			 cropIntent.putExtra("max-width", 640);
			 cropIntent.putExtra("max-height", 480);			 
			 //retrieve data on return
			 cropIntent.putExtra("return-data", true);
			 //start the activity - we handle returning in onActivityResult
			 startActivityForResult(cropIntent, CROP_REQUEST);			
		}
		
		
		
		
		
		
		
	}
	
	
	
	

	class GetCatalogTask extends AsyncTask<Void, Void, Boolean>
	{

	    InputStream inputStream = null;
	    String result = "";
	    int nCatalogID = -1;

	    public GetCatalogTask(int id)
	    {
	    	nCatalogID = id;
	    }

	    @Override
	    protected void onPreExecute()
	    {
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {

	        String url_select = "http://www.eoksjon24.ee/ea.api?action=categories&language=et";
	        if (nCatalogID != ID_ROOT_CATALOG)
	        {
	        	url_select += "&category_id=" + nCatalogID;
	        }

	        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

	        try 
	        {
	            // Set up HTTP post

	            // HttpClient is more then less deprecated. Need to change to URLConnection
	            HttpClient httpClient = new DefaultHttpClient();

	            HttpPost httpPost = new HttpPost(url_select);
	            httpPost.setEntity(new UrlEncodedFormEntity(param));
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();

	            // Read content & Log
	            inputStream = httpEntity.getContent();
	        } catch (UnsupportedEncodingException e1) 
	        {
	            Log.e("UnsupportedEncodingException", e1.toString());
	            e1.printStackTrace();
	        } catch (ClientProtocolException e2) 
	        {
	            Log.e("ClientProtocolException", e2.toString());
	            e2.printStackTrace();
	        } catch (IllegalStateException e3) 
	        {
	            Log.e("IllegalStateException", e3.toString());
	            e3.printStackTrace();
	        } catch (IOException e4) 
	        {
	            Log.e("IOException", e4.toString());
	            e4.printStackTrace();
	        }
	        // Convert response to string using String Builder
	        try 
	        {
	            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
	            StringBuilder sBuilder = new StringBuilder();

	            String line = null;
	            while ((line = bReader.readLine()) != null) 
	            {
	                sBuilder.append(line + "\n");
	            }

	            inputStream.close();
	            result = sBuilder.toString();

	        } catch (Exception e) 
	        {
	            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
	        }
	        return null;
	    } // protected Void doInBackground(String... params)

	    @Override
	    protected void onPostExecute(final Boolean success) 
	    {
	        //parse JSON data
	        try 
	        {
	        	ViewGroup catalogLayout = (ViewGroup) findViewById(R.id.layoutGetCat);
	        	catalogLayout.removeAllViews();
	        	Button bt;
	        	CheckBox cb;
	        	//Drawable icon;
	        	JSONArray jArray = new JSONArray(result);
	            for(int i=0; i < jArray.length(); i++) 
	            {

	                JSONObject jObject = jArray.getJSONObject(i);

	                int id = jObject.getInt("id");
	                int parent_id = jObject.getInt("parentid");
	                String action = jObject.getString("action");
	                String slug = jObject.getString("slug");
	                int category_item_count = jObject.getInt("category_item_count");
	                int category_child_count = jObject.getInt("category_child_count");
	                Log.i("JSON parsing", "Result: id=" + id + ", parent_id=" + parent_id + 
	                		", action=" + action + ", slug=" + slug + ", count=" + category_item_count);
	                
	                LinearLayout llRow = new LinearLayout(catalogLayout.getContext());
	                llRow.setOrientation(LinearLayout.HORIZONTAL);
	                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
	                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	                llRow.setLayoutParams(lparams);
	                
                	
	                bt = new Button(catalogLayout.getContext());
	                bt.setId(id);
	                bt.setTag(parent_id);	                
	                bt.setText(slug);
	                bt.setAllCaps(true);
	                bt.setGravity(Gravity.LEFT);	                
	                LinearLayout.LayoutParams bparams = new LinearLayout.LayoutParams(
	                        0, LayoutParams.WRAP_CONTENT, 1f);
	                bparams.setMargins(5, 0, 5, 5); // left, top, right, bottom
	                bt.setLayoutParams(bparams);
	                //icon = getResources().getDrawable(R.drawable.ic_menu_goto);
	                //bt.setCompoundDrawablesWithIntrinsicBounds( null, null, icon, null );
	                bt.setBackgroundDrawable(getResources().getDrawable( 
	                		activeID.indexOf(id) != -1 ? R.drawable.btn_blue : R.drawable.btn_yellow));
	                bt.setTextAppearance(bt.getContext(), R.style.ButtonText);
	                
	                
	                llRow.addView(bt);
	                catalogLayout.addView(llRow);
	                OnClickListener oclBtnCatalog = new OnClickListener()
	                {
	        			@Override
	        			public void onClick(View v)
	        			{
	        	            Button b = (Button)v;
	        				showCatalogButtons(v.getId(), b.getText().toString());
	        			}        	
	                };
	                bt.setOnClickListener(oclBtnCatalog);
	            } // End Loop
	            
	        	boolean last_catalog = (jArray.length() == 0); 
	        	boolean active_catalog = activeID.indexOf(nCatalogID) != -1;
	        	btStep2a_end_button.setEnabled((nCatalogID != ID_ROOT_CATALOG && last_catalog && !active_catalog) || activeID.size() > 0);
	        	
	            if (nCatalogID != ID_ROOT_CATALOG && last_catalog && !active_catalog) // if not ROOT + last catalog + not active -> add ADD button
	            {
	                bt = new Button(catalogLayout.getContext());
	                bt.setId(nCatalogID);
	                bt.setText(R.string.text_step2_cat_add);
	                bt.setGravity(Gravity.CENTER);
	                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	                bt.setLayoutParams(params);
	                params.setMargins(5, 5, 5, 0); // left, top, right, bottom
	                bt.setBackgroundDrawable(getResources(). getDrawable( R.drawable.btn_green ));
	                bt.setTextAppearance(bt.getContext(), R.style.ButtonText);
	                catalogLayout.addView(bt);
	                OnClickListener oclBtnAdd = new OnClickListener()
	                {
	        			@Override
	        			public void onClick(View v)
	        			{
	        	            activeID.add(v.getId());
	        	            activePath.add(MySingleton.getInstance().getNewAuctionActivity().tvStep2CategoriesPath.getText().toString());
	        	            lifoPID.clear();
	        	            lifoPath.clear();
	        	            showActivePaths();
	        				showCatalogButtons(ID_ROOT_CATALOG, "");
	        	            //layoutStep2GetCatSection.setVisibility(View.GONE);
	        	            layoutStep2ActiveCat.setVisibility(View.VISIBLE);
	        	            if (v.getId() == R.id.step2a_end_button)
	        	            {
	        					btStep2a_end_button.setVisibility(View.GONE);
	        					layoutStep2GetCatSection.setVisibility(View.GONE);
	        					layoutStep2ActiveCat.setVisibility(View.VISIBLE);
	        					layoutStep2TitleSection.setVisibility(View.VISIBLE);	        	            	
	        	            }
	        	        }        	
	                };
	                bt.setOnClickListener(oclBtnAdd);
	                btStep2a_end_button.setOnClickListener(oclBtnAdd);
	            }
	            if (nCatalogID != ID_ROOT_CATALOG) // if not ROOT then add BACK button
	            {
	                bt = new Button(catalogLayout.getContext());	            	
	                bt.setText(R.string.text_step2_cat_back);
	                bt.setGravity(Gravity.CENTER);
	                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	                bt.setLayoutParams(params);
	                params.setMargins(5, 5, 5, 0); // left, top, right, bottom
	                bt.setBackgroundDrawable(getResources(). getDrawable( R.drawable.btn_black ));
	                bt.setTextAppearance(bt.getContext(), R.style.ButtonText);
	                catalogLayout.addView(bt);
	                OnClickListener oclBtnBack = new OnClickListener()
	                {
	        			@Override
	        			public void onClick(View v)
	        			{
	        	            Button b = (Button)v;
	        	            if (!lifoPID.isEmpty())
	        	            {
	        	            	lifoPID.pop(); //remove current catalog ID	        	            	
	        	            	lifoPath.pop(); //remove current catalog Path	        	            	
	        	            }
	        				showCatalogButtons(!lifoPID.isEmpty() ? lifoPID.pop() : ID_ROOT_CATALOG, 
	        						!lifoPath.isEmpty() ? lifoPath.pop() : "");
	        			}        	
	                };
	                bt.setOnClickListener(oclBtnBack);
	            }

	        } catch (JSONException e) 
	        {
	            Log.e("JSONException", "Error: " + e.toString());
	        } // catch (JSONException e)
	        
	    	mCatalogTask = null;
	    } // protected void onPostExecute(final Boolean success)

	    @Override
		protected void onCancelled()
		{
	    	mCatalogTask = null;
		}
	} //class GetCatalogTask extends AsyncTask<Void, Void, Boolean>



	
	class AddAuctionTask extends AsyncTask<Void, Void, Boolean>
	{

	    InputStream inputStream = null;
	    String result = "";
        String postdata = "";

	    @Override
	    protected void onPreExecute()
	    {
	    	int index;
			JSONObject jObject = new JSONObject();
			try 
			{
				jObject.put("auction_type", "english");
				
				JSONArray jArrayID = new JSONArray();
				for (int i = 0; i < activeID.size(); i++)
				{
					jArrayID.put(activeID.get(i));
				}
				jObject.put("article_categories", jArrayID);
				
				String article_title = etStep2Title.getText().toString();
				jObject.put("article_title", article_title);
				
				String article_description = etStep2Desc.getText().toString();
				jObject.put("article_description", article_description);
				
				String article_location = etStep2Location.getText().toString();
				jObject.put("article_location", article_location);
				
				String price = etStep4StartPrice.getText().toString();
				jObject.put("price", price);
				
				String now_price = etStep4BuyItNowPrice.getText().toString();
				jObject.put("now_price", now_price);
				
				String exchange = cbStep4CanChange.isChecked() ? "true" : "false";
				jObject.put("exchange", exchange);
				
				String [] condition_list = { "new", "used", "old", "antique" };
				index = rgStep4Condition.indexOfChild(findViewById(rgStep4Condition.getCheckedRadioButtonId()));
				String condition = condition_list[index];
				jObject.put("condition", condition);
				
				String start_date = etStep4StartDate.getText().toString();
				jObject.put("start_date", start_date);
				
				String start_time = etStep4StartTime.getText().toString();
				jObject.put("start_time", start_time);
				
				String [] period_list = { "1", "2", "3", "4", "5", "6", "7", "9", "10", "14", "28" };
				String period = period_list[spStep4AuctionDuration.getSelectedItemPosition()];
				jObject.put("period", period);
				
				String [] closure_list = { "2", "5", "10" };
				index = rgStep4ExtClosure.indexOfChild(findViewById(rgStep4ExtClosure.getCheckedRadioButtonId()));
				String closure = closure_list[index];
				jObject.put("closure", closure);
				
				JSONObject jObjectPay = new JSONObject();
				jObjectPay.put("deposit", cbStep4PayDeposit.isChecked() ? "true" : "false");
				jObjectPay.put("account", cbStep4PayAuction24.isChecked() ? "true" : "false");
				jObjectPay.put("cash", cbStep4PayCash.isChecked() ? "true" : "false");
				jObjectPay.put("bank", cbStep4PayViaBank.isChecked() ? "true" : "false");
				jObject.put("payment_method", jObjectPay);
				
				JSONObject jObjectTrans = new JSONObject();
				jObjectTrans.put("coming_up", cbStep4TransportCollect.isChecked() ? "true" : "false");
				jObjectTrans.put("in_agreement", cbStep4TransportAccord.isChecked() ? "true" : "false");
				jObject.put("transport", jObjectTrans);
				
				JSONArray jArrayImg = new JSONArray();
				for (int i = 0; i < pics.size(); i++)
				{
					JSONObject jObjectImage = new JSONObject();
					jObjectImage.put("filename", i + ".jpg");
					jObjectImage.put("filetype", "image/jpg");
					jObjectImage.put("filebinary", pics.get(i));
//					jArrayImg.put(pics.get(i));
					jArrayImg.put(jObjectImage);
				}
				jObject.put("images", jArrayImg);
				
//				postdata = jObject.toString().replaceAll("\"","&quote;");
				postdata = jObject.toString();
				Log.i("POSTDATA", postdata);
				
			} catch (JSONException e5) 
			{
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}	    	
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {

	        String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=auction_submit";
	        String postfix2 = "&postdata=";
	        String postfix3 = "&stack=";
	        String stack = "";

	        // TODO: attempt authentication against a network service.
			try
			{
				stack = MySingleton.getInstance().SHA1(MySingleton.getInstance().getSALT())
						+ MySingleton.getInstance().SHA1(Integer.toString(MySingleton.getInstance().getUserID()));
			} catch (NoSuchAlgorithmException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{				
			}
			
//          postdata = "{\"a\":1,\"b\":2,\"c\":3,\"d\":4,\"e\":5}";
			byte[] byteArray = postdata.getBytes();
			postdata = Base64.encodeToString(byteArray, Base64.NO_WRAP);
			url_select = url_select + postfix1 + postfix2 + postdata + postfix3 + stack;
//			Log.i("BASE64", postdata);

//			url_select = "http://eoksjon24.ee/ea.api?action=system_config&language=et";

			
			
	        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

	        try 
	        {
	            // Set up HTTP post

	            // HttpClient is more then less deprecated. Need to change to URLConnection
	            HttpClient httpClient = new DefaultHttpClient();

	            HttpPost httpPost = new HttpPost(url_select);
	            httpPost.setEntity(new UrlEncodedFormEntity(param));
//	            httpPost.setHeader("Accept", "application/json");
//	            httpPost.setHeader("Content-type", "application/json");
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();

	            // Read content & Log
	            inputStream = httpEntity.getContent();
	        } catch (UnsupportedEncodingException e1) 
	        {
	            Log.e("UnsupportedEncodingException", e1.toString());
	            e1.printStackTrace();
	        } catch (ClientProtocolException e2) 
	        {
	            Log.e("ClientProtocolException", e2.toString());
	            e2.printStackTrace();
	        } catch (IllegalStateException e3) 
	        {
	            Log.e("IllegalStateException", e3.toString());
	            e3.printStackTrace();
	        } catch (IOException e4) 
	        {
	            Log.e("IOException", e4.toString());
	            e4.printStackTrace();
	        }
	        // Convert response to string using String Builder
	        try 
	        {
	            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
	            StringBuilder sBuilder = new StringBuilder();

	            String line = null;
	            while ((line = bReader.readLine()) != null) 
	            {
	                sBuilder.append(line + "\n");
	            }

	            inputStream.close();
	            result = sBuilder.toString();

	        } catch (Exception e) 
	        {
	            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
	        }
	        return null;
	    } // protected Void doInBackground(String... params)

	    @Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("SUCCESS") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "AddAuctionTask result OK");  
            		//MySingleton.getInstance().getProfileActivity().
               	}
                else
                {
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getNewAuctionActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(getResources().getString(R.string.text_cannot_add_auction) + "\n" + result);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
        	mAddAuctionTask = null;
        	MySingleton.getInstance().getNewAuctionActivity().finish();
	    } // protected void onPostExecute(final Boolean success)

	    @Override
		protected void onCancelled()
		{
	    	mAddAuctionTask = null;
		}
	} //class AddAuctionTask extends AsyncTask<Void, Void, Boolean>
	
	
	
	
}
