package com.eoksjon24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

public class AuctionActivity extends Activity
{
	private final Wamp mConnection = new WampConnection();
	final String wsuri = "ws://rtc.eoksjon24.ee:8081/ws.php";
	final String channel_bid = "auction_bid_";
	final String channel_post = "auction_post_";
	
	double nAuctionBidStep = 0;
	Button btNewPriceOffer;
	Button btBuyItNow;
	Button btAddQuestion;
	EditText etNewPriceOffer;
	EditText etCurrentPrice;
	TextView tvAuctionUserLastOffer;
	
	Button bt_tab1;
	Button bt_tab2;
	Button bt_tab3;
	Button bt_tab4;	
	LinearLayout ll_section1;
	LinearLayout ll_section2;
	LinearLayout ll_section3;
	LinearLayout ll_section4;
	
	private int nAuctionID = -1;
	private String sAuctionType = "";
	private GetAuctionContentTask mAuctionContentTask = null;
	private GetAuctionInfoTask mAuctionInfoTask = null;
	private GetSellerInfoTask mSellerInfoTask = null;
	private GetOffersListTask mOffersListTask = null;
	private GetQAListTask mQAListTask = null;
	private MakeBidTask mMakeBidTask = null;
	private AddQuestionTask mAddQuestionTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setAuctionActivity(this);
		setContentView(R.layout.activity_auction);

		Bundle b = getIntent().getExtras();
		if (b != null)
		{
			int value = b.getInt("auctionID");
			nAuctionID = value;
			Log.i("INFO", "Auction ID: " + Integer.toString(value));
		}
		else
		{
			Log.i("INFO", "Auction ID is null");					
		}
		
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		connectToChannels();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auction, menu);
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
    public void onPause() 
    {
    	super.onPause();
    	Log.i("INFO", "AuctionActivity paused");
    }
   
    @Override
    public void onResume() 
    {
    	super.onResume();
    	Log.i("INFO", "AuctionActivity resumed");   	
    }

    public void getAuctionContent()
	{		
		Log.i("INFO", "getAuctionContent");
		if (mAuctionContentTask != null)
		{
			return;
		}
		mAuctionContentTask = new GetAuctionContentTask();
		mAuctionContentTask.execute((Void) null);
	}

    public void getAuctionInfo()
	{		
		Log.i("INFO", "getAuctionInfo");
		if (mAuctionInfoTask != null)
		{
			return;
		}
		mAuctionInfoTask = new GetAuctionInfoTask();
		mAuctionInfoTask.execute((Void) null);
	}

    public void getSellerInfo()
	{		
		Log.i("INFO", "getSellerInfo");
		if (mSellerInfoTask != null)
		{
			return;
		}
		mSellerInfoTask = new GetSellerInfoTask();
		mSellerInfoTask.execute((Void) null);
	}

    public void getOffersList()
	{		
		Log.i("INFO", "getOffersList");
		if (mOffersListTask != null)
		{
			return;
		}
		mOffersListTask = new GetOffersListTask();
		mOffersListTask.execute((Void) null);
	}

    public void getQAList()
	{		
		Log.i("INFO", "getQAList");
		if (mQAListTask != null)
		{
			return;
		}
		mQAListTask = new GetQAListTask();
		mQAListTask.execute((Void) null);
	}

	public int getAuctionID()
	{
		return nAuctionID;
	}
	
	public void addNewQuestion()
	{		
		Log.i("INFO", "addNewQuestion");
		final Dialog dialogAddNewQuestion = new Dialog(MySingleton.getInstance().getAuctionActivity());
		dialogAddNewQuestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAddNewQuestion.setContentView(R.layout.dialog_1edit_multiline);
		TextView tvFN = (TextView) dialogAddNewQuestion.findViewById(R.id.txtFieldName);
		tvFN.setText(R.string.prompt_new_Question);
		EditText etF = (EditText) dialogAddNewQuestion.findViewById(R.id.editField);
		etF.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		etF.setHint(R.string.prompt_new_Question);
		etF.requestFocus();
		dialogAddNewQuestion.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		Button btnConfirm = (Button) dialogAddNewQuestion.findViewById(R.id.btn_confirm);
		btnConfirm.setText(R.string.text_SEND);
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		 		if (mAddQuestionTask != null)
				{
					return;
				}
				EditText etQuestion = (EditText)dialogAddNewQuestion.findViewById(R.id.editField);
	            String Question = etQuestion.getText().toString();
	            dialogAddNewQuestion.dismiss();
	            Log.i("Question", Question);
				mAddQuestionTask = new AddQuestionTask(MySingleton.getInstance().getAuctionActivity(), Question);
				mAddQuestionTask.execute((Void) null);
		     }
		});		
		Button btnCancel = (Button) dialogAddNewQuestion.findViewById(R.id.btn_cancel);
		btnCancel.setText(R.string.text_CLOSE);
		btnCancel.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		    	 dialogAddNewQuestion.dismiss();
		     }
		 });
		dialogAddNewQuestion.show();
	}

	
	
   /**
    * We want PubSub events delivered to us in JSON payload to be automatically
    * converted to this domain POJO. We specify this class later when we subscribe.
    */
	private static class MyEvent1  // if new bis is received
	{
		public double current_price;
		public double bid_price;
		public String bid_maker;

		@Override
		public String toString() 
		{
			return "{current_price: " + current_price +
					", bid_price: " + bid_price +
					", bid_maker: " + bid_maker + "}";
		}
	}
	
	private static class MyEvent2  // if new post is received 
	{
		public boolean received;

		@Override
		public String toString() 
		{
			return "{result: " + received + "}";
		}
	}

	public void connectToChannels()
	{
		mConnection.unsubscribe();
		mConnection.disconnect();
		
		Log.i("WAMP", "Connecting to\n" + wsuri + " ..");
		mConnection.connect(wsuri, new Wamp.ConnectionHandler()
		{
	         @Override
	         public void onOpen()
	         {
	            mConnection.subscribe(channel_bid + nAuctionID, MyEvent1.class, new Wamp.EventHandler()
	            {
	               @Override
	               public void onEvent(String topicUri, Object event) 
	               {
	                  MyEvent1 evt = (MyEvent1) event;
	                  Log.i("WAMP", "Event BID received : " + evt.toString());
	                  gotAuctionBidCallback(event);
	               }
	            });
	            mConnection.subscribe(channel_post + nAuctionID, MyEvent2.class, new Wamp.EventHandler()
	            {
	               @Override
	               public void onEvent(String topicUri, Object event) 
	               {
	                  MyEvent2 evt = (MyEvent2) event;
	                  Log.i("WAMP", "Event POST received : " + evt.toString());
	                  gotAuctionPostCallback(event);
	               }
	            });
	         }
	
	         @Override
	         public void onClose(int code, String reason) 
	         {
	        	Log.i("WAMP", "Connection closed.");
	        	Log.i("WAMP", reason);
	         }
		});	
	}
	
	public void gotAuctionBidCallback(Object event)
	{
        MyEvent1 evt = (MyEvent1) event;
		MySingleton.getInstance().getAuctionActivity().tvAuctionUserLastOffer.setText(evt.bid_maker);
		MySingleton.getInstance().getAuctionActivity().etCurrentPrice.setText(String.format(Locale.US, " %1.2f", evt.current_price));
		MySingleton.getInstance().getAuctionActivity().etNewPriceOffer.setText(String.format(Locale.US, " %1.2f", evt.bid_price));
		Vibrator vibe = (Vibrator) MySingleton.getInstance().getAuctionActivity().getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500); // 500 is time in ms
		String message = getResources().getString(R.string.text_auction_offer_received);
		message = message.replaceAll("#user#", evt.bid_maker);
		message = message.replaceAll("#price#", String.format(Locale.US, " %1.2f", evt.current_price));
		Toast toast = Toast.makeText(MySingleton.getInstance().getAuctionActivity(), message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.getView().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_green));
		toast.show();	
		getOffersList();
	}
	
	public void gotAuctionPostCallback(Object event)
	{
//		Vibrator vibe = (Vibrator) MySingleton.getInstance().getAuctionActivity().getSystemService(Context.VIBRATOR_SERVICE);
//		vibe.vibrate(500); // 500 is time in ms
//		Toast toast = Toast.makeText(MySingleton.getInstance().getAuctionActivity(), "New post received", Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER, 0, 0);
//		toast.getView().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_blue));
//		toast.show();	
		getQAList();
	}

	
	
	
	
	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			Log.i("FRAGMENT", "onCreateView");
			View rootView = inflater.inflate(R.layout.fragment_auction,
					container, false);

			TextView tv1 = (TextView) rootView.findViewById(R.id.txtBuyItNowPriceText);
			tv1.setVisibility(View.GONE);
    		EditText edBuyItNowPrice = (EditText) rootView.findViewById(R.id.txtBuyItNowPrice);
    		edBuyItNowPrice.setVisibility(View.GONE);
			TextView tv2 = (TextView) rootView.findViewById(R.id.txtBuyItNowPriceEUR);
			tv2.setVisibility(View.GONE);
			Button btQ2 = (Button) rootView.findViewById(R.id.btnQ2);
			btQ2.setVisibility(View.GONE);

			MySingleton.getInstance().getAuctionActivity().etCurrentPrice = (EditText) rootView.findViewById(R.id.txtCurrentPrice);
			MySingleton.getInstance().getAuctionActivity().etNewPriceOffer = (EditText) rootView.findViewById(R.id.txtNewOfferPrice);
			MySingleton.getInstance().getAuctionActivity().btNewPriceOffer = (Button) rootView.findViewById(R.id.button_make_offer);
			MySingleton.getInstance().getAuctionActivity().btNewPriceOffer.setVisibility(View.GONE);
			MySingleton.getInstance().getAuctionActivity().btBuyItNow = (Button) rootView.findViewById(R.id.button_buy_now);
			MySingleton.getInstance().getAuctionActivity().btBuyItNow.setVisibility(View.GONE);
			MySingleton.getInstance().getAuctionActivity().tvAuctionUserLastOffer = (TextView) rootView.findViewById(R.id.txtAuctionUserLastOffer);
			MySingleton.getInstance().getAuctionActivity().btAddQuestion = (Button) rootView.findViewById(R.id.button_question);
			
			MySingleton.getInstance().getAuctionActivity().etNewPriceOffer.addTextChangedListener(new TextWatcher() 
			{
				   @Override    
				   public void beforeTextChanged(CharSequence s, int start, int count, int after)
				   {
				   }

				   @Override    
				   public void onTextChanged(CharSequence s, int start, int before, int count)
				   {
					   try
					   {
						   double price_current = Double.parseDouble(MySingleton.getInstance().getAuctionActivity().etCurrentPrice.getText().toString());
						   double price_offer = Double.parseDouble(s.toString());
						   double bid_step = MySingleton.getInstance().getAuctionActivity().nAuctionBidStep;
						   String sSimpleOffer = getResources().getString(R.string.text_button_make_offer);
						   String sAutoOffer = getResources().getString(R.string.text_button_make_auto_offer);
						   MySingleton.getInstance().getAuctionActivity().btNewPriceOffer.setText(
								   price_current + bid_step >=  price_offer || price_current == 0 || bid_step == 0 ? sSimpleOffer : sAutoOffer);
						   MySingleton.getInstance().getAuctionActivity().btNewPriceOffer.setEnabled(price_offer >= price_current);
						   Log.i("PRICE", "price_current = " + price_current + "bid_step" + bid_step + "price_offer" + price_offer);
						}
					   catch (NumberFormatException e) 
					   {
						   // did not contain a valid double
					   }
				   }

				@Override
				public void afterTextChanged(Editable s)
				{
					// TODO Auto-generated method stub
					
				}
			});
			
			
			View.OnClickListener oclTabs = new View.OnClickListener()
			{

				@Override
				public void onClick(View v) 
				{
					MySingleton.getInstance().getAuctionActivity().ChangeLayoutByButtonID(v);
				}		
			};
			rootView.findViewById(R.id.button_tab1).setOnClickListener(oclTabs);
			rootView.findViewById(R.id.button_tab2).setOnClickListener(oclTabs);
			rootView.findViewById(R.id.button_tab3).setOnClickListener(oclTabs);
			rootView.findViewById(R.id.button_tab4).setOnClickListener(oclTabs);
			
			MySingleton.getInstance().getAuctionActivity().bt_tab1 = 
					(Button) rootView.findViewById(R.id.button_tab1);
			MySingleton.getInstance().getAuctionActivity().bt_tab2 = 
					(Button) rootView.findViewById(R.id.button_tab2);
			MySingleton.getInstance().getAuctionActivity().bt_tab3 = 
					(Button) rootView.findViewById(R.id.button_tab3);
			MySingleton.getInstance().getAuctionActivity().bt_tab4 = 
					(Button) rootView.findViewById(R.id.button_tab4);

			MySingleton.getInstance().getAuctionActivity().ll_section1 = 
					(LinearLayout) rootView.findViewById(R.id.layoutAuctionSectionInfo);
			MySingleton.getInstance().getAuctionActivity().ll_section2 = 
					(LinearLayout) rootView.findViewById(R.id.layoutAuctionSectionSeller);
			MySingleton.getInstance().getAuctionActivity().ll_section3 = 
					(LinearLayout) rootView.findViewById(R.id.layoutAuctionSectionOffers);
			MySingleton.getInstance().getAuctionActivity().ll_section4 = 
					(LinearLayout) rootView.findViewById(R.id.layoutAuctionSectionQA);
			
			
			MySingleton.getInstance().getAuctionActivity().btNewPriceOffer.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
	            	MySingleton.getInstance().getAuctionActivity().MakeBid();
	            }
	        });		

			MySingleton.getInstance().getAuctionActivity().btAddQuestion.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
	            	MySingleton.getInstance().getAuctionActivity().addNewQuestion();
	            }
	        });		
			
    		((AuctionActivity)getActivity()).getAuctionContent();
    		((AuctionActivity)getActivity()).getAuctionInfo();
    		((AuctionActivity)getActivity()).getSellerInfo();
    		((AuctionActivity)getActivity()).getOffersList();
    		((AuctionActivity)getActivity()).getQAList();
    		((AuctionActivity)getActivity()).ChangeLayoutByButtonID(MySingleton.getInstance().getAuctionActivity().bt_tab1);
			return rootView;
		}
	}
	
	public void ChangeLayoutByButtonID(View v)
	{
		Log.i("BUTTON TAB", "Tab id " + v.getId() + " pressed.");
		MySingleton.getInstance().getAuctionActivity().bt_tab1.setSelected(false);
		MySingleton.getInstance().getAuctionActivity().bt_tab2.setSelected(false);
		MySingleton.getInstance().getAuctionActivity().bt_tab3.setSelected(false);
		MySingleton.getInstance().getAuctionActivity().bt_tab4.setSelected(false);
		v.setSelected(true);
		MySingleton.getInstance().getAuctionActivity().ll_section1.setVisibility(LinearLayout.GONE);
		MySingleton.getInstance().getAuctionActivity().ll_section2.setVisibility(LinearLayout.GONE);
		MySingleton.getInstance().getAuctionActivity().ll_section3.setVisibility(LinearLayout.GONE);
		MySingleton.getInstance().getAuctionActivity().ll_section4.setVisibility(LinearLayout.GONE);

		switch (v.getId())
		{
			case R.id.button_tab1:
				MySingleton.getInstance().getAuctionActivity().ll_section1.setVisibility(LinearLayout.VISIBLE);
				break;
			case R.id.button_tab2:
				MySingleton.getInstance().getAuctionActivity().ll_section2.setVisibility(LinearLayout.VISIBLE);
				break;
			case R.id.button_tab3:
				MySingleton.getInstance().getAuctionActivity().ll_section3.setVisibility(LinearLayout.VISIBLE);
				break;
			case R.id.button_tab4:
				MySingleton.getInstance().getAuctionActivity().ll_section4.setVisibility(LinearLayout.VISIBLE);
				break;
		}
	}

	public void MakeBid()
	{
 		if (mMakeBidTask != null)
		{
			return;
		}
 		double new_offer = Double.parseDouble(etNewPriceOffer.getText().toString());
 		mMakeBidTask =	new MakeBidTask(MySingleton.getInstance().getAuctionActivity(), new_offer);
 		mMakeBidTask.execute((Void) null);		
	}

	class GetAuctionContentTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(AuctionActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetAuctionContentTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=auction";
	        if (getAuctionID() > 0)
	        {
	        	url_select += "&auction_id=" + Integer.toString(getAuctionID());
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
		}		

		@Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);			
	        //parse JSON data
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "GetAuctionContentTask result OK");                	
            		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);

            		((TextView) frag1.getView().findViewById(R.id.txtAuctionName)).setText(jObject.getJSONObject("auction").getString("article_title"));
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionID)).setText(Integer.toString(getAuctionID()));
            		
            		MySingleton.getInstance().getAuctionActivity().sAuctionType = jObject.getJSONObject("auction").getString("auction_type");
            		
                    String sImageArray = (jObject.getJSONObject("auction").getString("initial_image"));
                    JSONArray jImgArray = new JSONArray(sImageArray);
        			if (jImgArray.length() > 0)
                    {
            			String sImgMain = jImgArray.getJSONObject(0).getString("filebinary");
                    	byte[] decodedString = Base64.decode(sImgMain, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ((ImageView) frag1.getView().findViewById(R.id.imgMain)).setImageBitmap(decodedByte);                    	
                    }
            		
                    String sImageArray2 = (jObject.getJSONObject("auction").getString("auction_thumbnails"));
                    JSONArray jImgArray2 = new JSONArray(sImageArray2);
                	ViewGroup layoutImages = (ViewGroup) findViewById(R.id.layoutAuctionThumbs);
                	for (int i = 0; i < jImgArray2.length(); i++)
                    {
            			String sImgMain = jImgArray2.getJSONObject(i).getString("filebinary");
                    	byte[] decodedString = Base64.decode(sImgMain, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView iv = new ImageView(layoutImages.getContext());
                        iv.setScaleType(ScaleType.FIT_START);
                        iv.setImageBitmap(decodedByte);
                        iv.setPadding(5, 0, 5, 0);

                        layoutImages.addView(iv);
                    }

            		((EditText) frag1.getView().findViewById(R.id.txtCurrentPrice))
            			.setText(String.format(Locale.US, " %1.2f", jObject.getJSONObject("auction").getDouble("current_price")));
            		((EditText) frag1.getView().findViewById(R.id.txtNewOfferPrice))
            			.setText(String.format(Locale.US, " %1.2f", jObject.getJSONObject("auction").getDouble("bid_price")));
            		MySingleton.getInstance().getAuctionActivity().nAuctionBidStep = 
            				jObject.getJSONObject("auction").getDouble("bid_price") - jObject.getJSONObject("auction").getDouble("current_price");
            		Log.i("STEP", "step bid price = " + MySingleton.getInstance().getAuctionActivity().nAuctionBidStep);
            		
            		int nBuyItNowStatus = jObject.getJSONObject("auction").getJSONObject("now_price").getInt("status");
            		if (nBuyItNowStatus != 0)
            		{
                		((EditText) frag1.getView().findViewById(R.id.txtBuyItNowPrice))
                			.setText(String.format(Locale.US, " %1.2f", jObject.getJSONObject("auction").getJSONObject("now_price").getDouble("value")));            			
            		}
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionTimeLeft)).setText(
            				MySingleton.getInstance().getTimeLeft(jObject.getJSONObject("auction").getInt("time_left")));
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionClosure)).setText(
            				jObject.getJSONObject("auction").getString("auction_closure") + " min.");
           		
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		Date dateStart = null;
            		Date dateEnd = null;
            		try
					{
						dateStart = sdf.parse(jObject.getJSONObject("auction").getString("auction_start"));
						dateEnd   = sdf.parse(jObject.getJSONObject("auction").getString("auction_close"));
					} catch (ParseException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}            		
               		((TextView) frag1.getView().findViewById(R.id.txtAuctionStart)).setText(dateStart != null ? dateStart.toLocaleString() : "");
               		((TextView) frag1.getView().findViewById(R.id.txtAuctionEnd)).setText(dateEnd != null ? dateEnd.toLocaleString() : "");
               		
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionWatch)).setText(
            				jObject.getJSONObject("auction").getString("views_count"));
            		if (jObject.getJSONObject("auction").getInt("bid_count") > 0)
            		{
                		((TextView) frag1.getView().findViewById(R.id.txtAuctionUserLastOffer)).setText(
                				jObject.getJSONObject("auction").getString("bid_maker"));
            		}

        			TextView tv1 = (TextView) frag1.getView().findViewById(R.id.txtBuyItNowPriceText);
        			tv1.setVisibility(nBuyItNowStatus != 0 ? View.VISIBLE : View.GONE);
            		EditText edBuyItNowPrice = (EditText) frag1.getView().findViewById(R.id.txtBuyItNowPrice);
            		edBuyItNowPrice.setVisibility(nBuyItNowStatus != 0 ? View.VISIBLE : View.GONE);
        			TextView tv2 = (TextView) frag1.getView().findViewById(R.id.txtBuyItNowPriceEUR);
        			tv2.setVisibility(nBuyItNowStatus != 0 ? View.VISIBLE : View.GONE);
          
        			Button btMakeOffer = (Button) frag1.getView().findViewById(R.id.button_make_offer);
            		btMakeOffer.setVisibility(MySingleton.getInstance().getUserID() != 0 ? View.VISIBLE : View.GONE);
            		Button btBuyNow = (Button) frag1.getView().findViewById(R.id.button_buy_now);
               		btBuyNow.setVisibility(MySingleton.getInstance().getUserID() != 0 && nBuyItNowStatus != 0 ? View.VISIBLE : View.GONE);
            		Button btQ2 = (Button) frag1.getView().findViewById(R.id.btnQ2);
            		btQ2.setVisibility(nBuyItNowStatus != 0 ? View.VISIBLE : View.GONE);

            		EditText edNewOfferPrice = (EditText) frag1.getView().findViewById(R.id.txtNewOfferPrice);
            		edNewOfferPrice.setFocusable(MySingleton.getInstance().getUserID() != 0 ? true : false);
            		edNewOfferPrice.setEnabled(MySingleton.getInstance().getUserID() != 0 ? true : false);
            		
            		
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
			mAuctionContentTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
			mAuctionContentTask = null;
		}
	} // class GetAuctionContentTask extends AsyncTask<Void, Void, Boolean>	

	class GetAuctionInfoTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(AuctionActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() 
	    {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetAuctionInfoTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_details&activity=article_info";
	        if (getAuctionID() > 0)
	        {
	        	url_select += "&auction_id=" + Integer.toString(getAuctionID());
	        }
	        url_select += "&language=et";

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
		}		

		@Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);			
	        //parse JSON data
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "GetAuctionInfoTask result OK");                	
            		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);

            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemCondition)).setText(
            				jObject.getJSONObject("article_info").getString("condition"));
            		
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemLocation)).setText(
            				jObject.getJSONObject("article_info").getString("article_location"));
            		
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemsTotal)).setText(
            				jObject.getJSONObject("article_info").getString("amount"));
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemsTotal)).setVisibility(
            				MySingleton.getInstance().getAuctionActivity().sAuctionType.contains("english") ? View.GONE : View.VISIBLE);
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemsTotalText)).setVisibility(
            				MySingleton.getInstance().getAuctionActivity().sAuctionType.contains("english") ? View.GONE : View.VISIBLE);
            		
            		
            		
            		boolean bCanExchange = jObject.getJSONObject("article_info").getBoolean("exchange");
            		String sExchange = getResources().getString(bCanExchange ? R.string.text_YES : R.string.text_NO);
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemChange)).setText(sExchange);


                    JSONArray jTransportArray = new JSONArray(jObject.getJSONObject("article_info").getString("transport"));
                    String sOutput = "";
                	for (int i = 0; i < jTransportArray.length(); i++)
                    {
                		sOutput += jTransportArray.getString(i) + (i + 1 < jTransportArray.length() ? "\n" : "");               		
                    }          		
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionItemTransport)).setText(sOutput);
           		
                    JSONArray jPaymentArray = new JSONArray(jObject.getJSONObject("article_info").getString("payment"));
                    sOutput = "";
                	for (int i = 0; i < jPaymentArray.length(); i++)
                    {
                		sOutput += jPaymentArray.getString(i) + (i + 1 < jPaymentArray.length() ? "\n" : "");                		
                    }          		
                	((TextView) frag1.getView().findViewById(R.id.txtAuctionItemPayment)).setText(sOutput);
           		
            		((TextView) frag1.getView().findViewById(R.id.txtItemDescription)).setText(
    				Html.fromHtml(jObject.getJSONObject("article_info").getString("article_description")));
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
            mAuctionInfoTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
			mAuctionInfoTask = null;
		}
	} // class GetAuctionInfoTask extends AsyncTask<Void, Void, Boolean>	

	class GetSellerInfoTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(AuctionActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetSellerInfoTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_details&activity=seller_info";
	        if (getAuctionID() > 0)
	        {
	        	url_select += "&auction_id=" + Integer.toString(getAuctionID());
	        }
	        url_select += "&language=et";

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
		}		

		@Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);			
	        //parse JSON data
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "GetSellerInfoTask result OK");                	
            		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
            		
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionSellerAccountLabel)).setText(
            				jObject.getJSONObject("seller_info").getString("seller_label"));            	
            		((TextView) frag1.getView().findViewById(R.id.txtAuctionSellerAccount)).setText(
            				jObject.getJSONObject("seller_info").getString("seller_user"));
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
            mSellerInfoTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
            mSellerInfoTask = null;
		}
	} // class GetSellerInfoTask extends AsyncTask<Void, Void, Boolean>	
	
	class GetOffersListTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(AuctionActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetOffersListTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_details&activity=bid_info";
	        if (getAuctionID() > 0)
	        {
	        	url_select += "&auction_id=" + Integer.toString(getAuctionID());
	        }
	        url_select += "&language=et";

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
		}		

		@Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);			
	        //parse JSON data
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "GetOffersListTask result OK");                	
            		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
            		
                    JSONArray jLabelsArray = new JSONArray(jObject.getString("labels"));
            		((TextView) frag1.getView().findViewById(R.id.txtOffersLabel1)).setText(
            				jLabelsArray.getString(0));            	
            		((TextView) frag1.getView().findViewById(R.id.txtOffersLabel2)).setText(
            				jLabelsArray.getString(1));            	
            		((TextView) frag1.getView().findViewById(R.id.txtOffersLabel3)).setText(
            				jLabelsArray.getString(2));

                	ViewGroup layoutOffers = (ViewGroup) findViewById(R.id.layoutAuctionOffersList);
                	layoutOffers.removeAllViewsInLayout();
                	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    JSONArray jItemsArray = new JSONArray(jObject.getString("values"));
            		for (int i = 0; i < jItemsArray.length(); i++)
            		{
                		JSONObject c = jItemsArray.getJSONObject(i);
                		View vOfferItem = inflater.inflate(R.layout.layout_offer_item, null);
                		vOfferItem.setBackgroundColor(Color.parseColor(i%2==0 ? "#ffffff" : "#eeeeee"));
                		TextView tvItem1 = (TextView) vOfferItem.findViewById(R.id.txtOffersItem1);
                		tvItem1.setText(c.getString("username"));
                		TextView tvItem2 = (TextView) vOfferItem.findViewById(R.id.txtOffersItem2);
                		tvItem2.setText(c.getString("bid_price"));
                		TextView tvItem3 = (TextView) vOfferItem.findViewById(R.id.txtOffersItem3);
                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                		Date dateStart = null;
                		dateStart = sdf.parse(c.getString("ts"));
                		tvItem3.setText(dateStart != null ? dateStart.toLocaleString() : "");
                		layoutOffers.addView(vOfferItem, i);	            
            		}
            		if (MySingleton.getInstance().getAuctionActivity().bt_tab3 != null)
            			MySingleton.getInstance().getAuctionActivity().
            				bt_tab3.setText(getResources().getString(R.string.text_tab3) + "\n(" + jItemsArray.length() + ")");
               }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
            mOffersListTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
            mOffersListTask = null;
		}
	} // class GetOffersListTask extends AsyncTask<Void, Void, Boolean>	

	class GetQAListTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(AuctionActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetQAListTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_details&activity=posts_info";
	        if (getAuctionID() > 0)
	        {
	        	url_select += "&auction_id=" + Integer.toString(getAuctionID());
	        }
	        url_select += "&language=et";

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
		}		

		@Override
	    protected void onPostExecute(final Boolean success) 
	    {
			Log.i("RESULT", result);			
	        //parse JSON data
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "GetQAListTask result OK");                	
            		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
            		
                    JSONArray jLabelsArray = new JSONArray(jObject.getString("labels"));
            		((TextView) frag1.getView().findViewById(R.id.txtQALabel1)).setText(
            				jLabelsArray.getString(0));            	

            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		ViewGroup layoutQA = (ViewGroup) findViewById(R.id.layoutAuctionQAList);
            		layoutQA.removeAllViewsInLayout();
                	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    JSONArray jItemsArray = new JSONArray(jObject.getString("values"));
            		for (int i = 0; i < jItemsArray.length(); i++)
            		{
                		JSONObject c = jItemsArray.getJSONObject(i);
                		View vQAItem = inflater.inflate(R.layout.layout_qa_item, null);
                		vQAItem.setBackgroundColor(Color.parseColor(i%2==0 ? "#ffffff" : "#eeeeee"));
                		
                		TextView tvItem1 = (TextView) vQAItem.findViewById(R.id.txtBuyer);
                		tvItem1.setText(c.getString("presenter"));
                		
                		TextView tvItem2 = (TextView) vQAItem.findViewById(R.id.txtDateTimeQ);
                		Date dateQ = null;
                		dateQ = sdf.parse(c.getString("post_ts"));
                		tvItem2.setText(dateQ != null ? dateQ.toLocaleString() : "");
                		//DateTimeFormat formater = DateFormat.getDateTimeInstance();
                		                		                	
                		TextView tvItem3 = (TextView) vQAItem.findViewById(R.id.txtBuyerQ);
                		tvItem3.setText(c.getString("post"));
                		
                		TextView tvItem4 = (TextView) vQAItem.findViewById(R.id.txtBuyerWarning);
                		tvItem4.setText(c.getString("post_warning"));
                		if (c.getString("post_warning").isEmpty())
                			tvItem4.setVisibility(View.GONE);



                		TextView tvItem5 = (TextView) vQAItem.findViewById(R.id.txtSeller);
                		tvItem5.setText(c.getString("seller"));
                		
                		TextView tvItem6 = (TextView) vQAItem.findViewById(R.id.txtDateTimeA);
                		Date dateA = null;
                		dateA = sdf.parse(c.getString("comment_ts"));
                		tvItem6.setText(dateA != null ? dateA.toLocaleString() : "");
                		                		                	
                		TextView tvItem7 = (TextView) vQAItem.findViewById(R.id.txtSellerA);
                		tvItem7.setText(c.getString("comment"));
                		
                		TextView tvItem8 = (TextView) vQAItem.findViewById(R.id.txtSellerWarning);
                		tvItem8.setText(c.getString("comment_warning"));
                		if (c.getString("comment_warning").isEmpty())
                			tvItem8.setVisibility(View.GONE);

                		
                		layoutQA.addView(vQAItem, i);	            
            		}
            		if (MySingleton.getInstance().getAuctionActivity().bt_tab4 != null)
            			MySingleton.getInstance().getAuctionActivity().
            				bt_tab4.setText(getResources().getString(R.string.text_tab4) + "\n(" + jItemsArray.length() + ")");
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
            mQAListTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
            mQAListTask = null;
		}
	} // class GetQAListTask extends AsyncTask<Void, Void, Boolean>	

	public class AddQuestionTask extends AsyncTask<Void, Void, Boolean>
	{
	    public AuctionActivity activity;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    private String sQuestion;
	    
	    public AddQuestionTask(AuctionActivity a, String question)
	    {
	    	this.activity = a;
	    	sQuestion = question;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_post_submit";
	        String postfix1 = "&post_content=";
	        String postfix2 = "&auction_id=";
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
			
			url_select = url_select + postfix1 + sQuestion + postfix2 + nAuctionID + postfix3 + stack;
			url_select = url_select.replaceAll(" ", "%20");
			Log.i("REQUEST", url_select);				
			
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
			return true;		
		}

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
                	Log.i("TASK", "AddQuestionTask result OK");  
            		//MySingleton.getInstance().getProfileActivity().
               	}
                else
                {
//            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getAuctionActivity());
//            		dialog.setTitle(R.string.app_name);
//            		dialog.setMessage(getResources().getString(R.string.text_cannot_make_bid) + result);
//            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
//            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mAddQuestionTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mAddQuestionTask = null;
		}
	}	



	
	public class MakeBidTask extends AsyncTask<Void, Void, Boolean>
	{
	    public AuctionActivity activity;
	    private double nNewOfferPrice = 0.00;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public MakeBidTask(AuctionActivity a, double new_offer)
	    {
	    	this.activity = a;
	    	nNewOfferPrice = new_offer;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_make_bid";
	        String postfix1 = "&bid_price=";
	        String postfix2 = "&auction_id=";
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
			
			url_select = url_select + postfix1 + nNewOfferPrice + postfix2 + nAuctionID + postfix3 + stack;
			Log.i("REQUEST", url_select);				
			
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
			return true;		
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{			
			Log.i("RESULT", result);
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	Log.i("TASK", "MakeBidTask result OK");  
            		//MySingleton.getInstance().getProfileActivity().
               	}
                else
                {
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getAuctionActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(getResources().getString(R.string.text_cannot_make_bid) + result);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mMakeBidTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mMakeBidTask = null;
		}
	}	
	
}
