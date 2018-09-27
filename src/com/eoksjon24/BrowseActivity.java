package com.eoksjon24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BrowseActivity extends Activity
{
	final String FIELD_CONTENT    = "&filter[article_title]=";
	final String FIELD_PRICE_FROM = "&filter[price_from]=";
	final String FIELD_PRICE_TO   = "&filter[price_to]=";

	final String SORT_CONTENT     = "&sort[b.article_title]=";
	final String SORT_PRICE       = "&sort[b.price]=";
	final String SORT_BID_COUNT   = "&sort[b.bid_count]=";
	final String SORT_TIME_LEFT   = "&sort[time_left]=";

	final String SORT_DIR_ASC     = "ASC";
	final String SORT_DIR_DESC    = "DESC";
	
	private GetCatalogTask mCatalogTask = null;
	private GetCatalogContentTask mCatalogContentTask = null;
	private int nCatalogID = -1;
	private String sCatalogName;
	private String sFilter = "";

	private ViewGroup vgBrowse;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setBrowseActivity(this);
		setContentView(R.layout.activity_browse);
		
		Bundle b = getIntent().getExtras();
		if (b != null)
		{
			int value = b.getInt("catalogID");
			nCatalogID = value;
			String str = b.getString("catalogName");
			sCatalogName = str;
			Log.i("INFO", Integer.toString(value));
			Log.i("INFO", str);
		}
		else
		{
			sCatalogName = getResources().getString(R.string.default_root_catalog_name);
			Log.i("INFO", "catalogID is null");					
		}
		
		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_browse,
					container, false);
			
			// TODO Change to Singleton call
			((BrowseActivity)getActivity()).getCatalogList();
			((BrowseActivity)getActivity()).vgBrowse = (ViewGroup) this.getView();

			return rootView;
		}
	}

	public void getCatalogList()
	{		
		Log.i("INFO", "GetCatalogList");
		if (mCatalogTask != null)
		{
			return;
		}
		mCatalogTask = new GetCatalogTask();
		mCatalogTask.execute((Void) null);
	}
	
	public void getCatalogContentList(String sFilter)
	{		
		Log.i("INFO", "GetCatalogContentList");
		Log.i("FILTER", sFilter);
		if (mCatalogContentTask != null)
		{
			return;
		}
		mCatalogContentTask = new GetCatalogContentTask(sFilter);
		mCatalogContentTask.execute((Void) null);
	}
	
	public void setCatalogID(int nCID, String sCatalogName)
	{		
        Intent intent = new Intent(this.getApplicationContext(), BrowseActivity.class);
        Bundle b = new Bundle();
        b.putInt("catalogID", nCID);
        b.putString("catalogName", sCatalogName);
        intent.putExtras(b);
        startActivity(intent);
	}
	
	public int getCatalogID()
	{
		return nCatalogID;
	}

	public void showAuctionByID(int nID)
	{		
        Intent intent = new Intent(this.getApplicationContext(), AuctionActivity.class);
        Bundle b = new Bundle();
        b.putInt("auctionID", nID);
        intent.putExtras(b);
        startActivity(intent);
	}
	
	public void attachFilterLayout()
	{
    	ViewGroup filterLayout = (ViewGroup) findViewById(R.id.layoutFilter);
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vFilter = inflater.inflate(R.layout.layout_filter, null);
		
		Button btShowFilter = (Button) vFilter.findViewById(R.id.button_filter);
		Button btUseFilter = (Button) vFilter.findViewById(R.id.button_filter2);
		Button btResetFilter = (Button) vFilter.findViewById(R.id.button_reset_filter);
		
        OnClickListener oclBtnClick = new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{		    	
				ViewGroup filterLayout = (ViewGroup) findViewById(R.id.layoutFilter);
		    	ViewGroup bodyLayout = (ViewGroup) filterLayout.findViewById(R.id.layout_filter_body); 
				Button btShowFilter = (Button) filterLayout.findViewById(R.id.button_filter);
				Button btUseFilter = (Button) filterLayout.findViewById(R.id.button_filter2);
				Button btResetFilter = (Button) filterLayout.findViewById(R.id.button_reset_filter);
				EditText etContent = (EditText) filterLayout.findViewById(R.id.et_content);
				EditText etPriceStart = (EditText) filterLayout.findViewById(R.id.et_price_start);
				EditText etPriceEnd = (EditText) filterLayout.findViewById(R.id.et_price_end);
				RadioGroup rgSort = (RadioGroup) filterLayout.findViewById(R.id.rg_sort_field);
				RadioGroup rgSortDir = (RadioGroup) filterLayout.findViewById(R.id.rg_sort_direction);
				switch (v.getId())
	            {
	            case R.id.button_filter:
        			v.setVisibility(View.GONE);
        			bodyLayout.setVisibility(View.VISIBLE);
	            	break;
	            case R.id.button_filter2:
        			bodyLayout.setVisibility(View.GONE);
        			btShowFilter.setVisibility(View.VISIBLE);
        			
        			sFilter = "";
        			String sContent = etContent.getText().toString();
        			sFilter += !sContent.isEmpty() ? FIELD_CONTENT + sContent : "";
        			String sPriceFrom = etPriceStart.getText().toString();
        			sFilter += !sPriceFrom.isEmpty() ? FIELD_PRICE_FROM + sPriceFrom : "";
        			String sPriceTo = etPriceEnd.getText().toString();
        			sFilter += !sPriceTo.isEmpty() ? FIELD_PRICE_TO + sPriceTo : "";
        			int idSort = rgSort.getCheckedRadioButtonId();
        			int idSortDir = rgSortDir.getCheckedRadioButtonId();
        			String sSortBy = "";
        			switch (idSort)
        			{
        			case R.id.ri_time_left :
        				sSortBy = SORT_TIME_LEFT + (idSortDir == R.id.ri_DESC ? SORT_DIR_DESC : SORT_DIR_ASC);
        				break;
        			case R.id.ri_content :
        				sSortBy = SORT_CONTENT + (idSortDir == R.id.ri_DESC ? SORT_DIR_DESC : SORT_DIR_ASC);
        				break;
        			case R.id.ri_price :
        				sSortBy = SORT_PRICE + (idSortDir == R.id.ri_DESC ? SORT_DIR_DESC : SORT_DIR_ASC);
        				break;
        			case R.id.ri_offers :
        				sSortBy = SORT_BID_COUNT + (idSortDir == R.id.ri_DESC ? SORT_DIR_DESC : SORT_DIR_ASC);
        				break;
        			}        			
        			sFilter += sSortBy;
        			MySingleton.getInstance().getBrowseActivity().getCatalogContentList(sFilter);
	            	break;
	            case R.id.button_reset_filter:
        			bodyLayout.setVisibility(View.GONE);
        			btShowFilter.setVisibility(View.VISIBLE);       			
        			etContent.setText("");
        			etPriceStart.setText("");
        			etPriceEnd.setText("");
        			rgSort.check(R.id.ri_time_left);
        			rgSortDir.check(R.id.ri_ASC);       			
           			MySingleton.getInstance().getBrowseActivity().getCatalogContentList("");
	            	break;	            
	            }
			}        	
        };
        btShowFilter.setOnClickListener(oclBtnClick);
        btUseFilter.setOnClickListener(oclBtnClick);
        btResetFilter.setOnClickListener(oclBtnClick);
        
		LinearLayout llFilterBody = (LinearLayout) vFilter.findViewById(R.id.layout_filter_body);
		llFilterBody.setVisibility(View.GONE);		
		filterLayout.addView(vFilter);
	}

	class GetCatalogTask extends AsyncTask<Void, Void, Boolean>
	{

	    private ProgressDialog progressDialog = new ProgressDialog(BrowseActivity.this);
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
	                GetCatalogTask.this.cancel(true);
	            }
	        });
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {

	        String url_select = "http://www.eoksjon24.ee/ea.api?action=categories&language=et";
	        if (getCatalogID() > 0)
	        {
	        	url_select += "&category_id=" + Integer.toString(getCatalogID());
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
	        	ViewGroup catalogLayout = (ViewGroup) findViewById(R.id.layoutCatalog);
	        	catalogLayout.removeAllViews();
	        	ViewGroup browseHeaderLayout = (ViewGroup) findViewById(R.id.layoutBrowseHeader);
	        	Button bt;
	        	Drawable icon;
	        	TextView txtCatalogName;
	        	String sResult = "";
	        	JSONArray jArray = new JSONArray(result);    
	            for(int i=0; i < jArray.length(); i++) 
	            {

	                JSONObject jObject = jArray.getJSONObject(i);

	                int id = jObject.getInt("id");
	                int parent_id = jObject.getInt("parentid");
	                String action = jObject.getString("action");
	                String slug = jObject.getString("slug");
	                int category_item_count = jObject.getInt("category_item_count");
	                Log.i("JSON parsing", "Result: id=" + Integer.toString(id) + ", parent_id=" + Integer.toString(parent_id) + 
	                		", action=" + action + ", slug=" + slug + ", count=" + Integer.toString(category_item_count));
                	
	                sResult = "<b>" + slug + "</b>" + " (" + Integer.toString(category_item_count) + ")";
	                
	                bt = new Button(catalogLayout.getContext());
	                bt.setText(Html.fromHtml(sResult));
	                bt.setGravity(Gravity.LEFT);
	                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	                params.setMargins(0, 0, 0, 5); // left, top, right, bottom
	                bt.setLayoutParams(params);
	                icon = getResources(). getDrawable( R.drawable.ic_menu_goto);
	                bt.setCompoundDrawablesWithIntrinsicBounds( null, null, icon, null );
	                bt.setBackgroundDrawable(getResources(). getDrawable( R.drawable.btn_yellow ));
	                bt.setTextAppearance(bt.getContext(), R.style.ButtonText);
	                bt.setId(id);
	                catalogLayout.addView(bt);
	                OnClickListener oclBtnCatalog = new OnClickListener()
	                {

	        			@Override
	        			public void onClick(View v)
	        			{
	        	            Button b = (Button)v;
	        				setCatalogID(v.getId(), b.getText().toString());
	        			}        	
	                };
	                bt.setOnClickListener(oclBtnCatalog);
	            } // End Loop
	            
                txtCatalogName = (TextView) browseHeaderLayout.findViewById(R.id.txtCatalogName);
                if (txtCatalogName != null)
                {
                	txtCatalogName.setText(sCatalogName);	                	
                }


	        } catch (JSONException e) 
	        {
	            Log.e("JSONException", "Error: " + e.toString());
	        } // catch (JSONException e)
	        
	        if (MySingleton.getInstance().getBrowseActivity().nCatalogID != -1)
	        {
		        MySingleton.getInstance().getBrowseActivity().attachFilterLayout();	        	
	        }
	        
            this.progressDialog.dismiss();
			getCatalogContentList("");
	    	mCatalogTask = null;
	    } // protected void onPostExecute(final Boolean success)

	    @Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
	    	mCatalogTask = null;
		}
	} //class GetCatalogTask extends AsyncTask<Void, Void, Boolean>
	
	
	class GetCatalogContentTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(BrowseActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 
	    String sFilter = "";

	    public GetCatalogContentTask(String filter)
	    {
	    	sFilter = filter;
	    }

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage(getResources().getString(R.string.text_loading));
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetCatalogContentTask.this.cancel(true);
	            }
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=browsing";
	        if (getCatalogID() > 0)
	        {
	        	url_select += "&category_id=" + Integer.toString(getCatalogID());
	        }
	        if (!sFilter.isEmpty())
	        {
	        	url_select += sFilter;
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
	        //parse JSON data
	        try 
	        {
	        	int nFakeRate = 6;
	        	ViewGroup layoutContent = (ViewGroup) findViewById(R.id.layoutContent);
	        	layoutContent.removeAllViews();
	        	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            //LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.fragment_browse, null);
	        	JSONArray jArray = new JSONArray(result);   
	            for(int i=0; i < jArray.length(); i++) 
	            {
	                JSONObject jObject = jArray.getJSONObject(i);

	                int auction_id = jObject.getInt("auction_id");
	                String auction_title = jObject.getString("auction_title");
	                double price = jObject.getDouble("price");
	                int bid_count = jObject.getInt("bid_count");
	                int time_left = jObject.getInt("time_left");
	                String sRate = jObject.getString("rate");
	                int rate = Integer.parseInt(sRate.isEmpty() ? "0" : sRate);
	                JSONObject jImgObject = jObject.getJSONObject("image");
	            	String img_type = jImgObject.getString("type");
	            	String img_binary = jImgObject.getString("binary");		 
	                Log.i("JSON parsing", 
	                		"Result: auction_id=" + Integer.toString(auction_id) + 
	                		", auction_title=" + auction_title + 
	                		", price=" + Double.toString(price) + 
	                		", bid_count=" + Integer.toString(bid_count) + 
	                		", time_left=" + Integer.toString(time_left) + 
	                		", rate=" + Integer.toString(rate) + 
	                		", img_type=" + img_type + 
	                		", img_binary=" + img_binary);
	                
	                View custom = inflater.inflate(R.layout.layout_content_item, null);
	                custom.setId(auction_id);
	                TextView tvDescription = (TextView) custom.findViewById(R.id.textDescription);
	                tvDescription.setText(auction_title);
	                TextView tvPrice = (TextView) custom.findViewById(R.id.textPrice);
	                tvPrice.setText(String.format(Locale.US, " %,.2f", price) + "\u20ac ");
	                
	                TextView tvInfo = (TextView) custom.findViewById(R.id.textInfo);
	                String sTimeLeft = MySingleton.getInstance().getTimeLeft(time_left);
	                tvInfo.setText(
	                		getResources().getString(R.string.browse_item_time_left) + " " + sTimeLeft + "\n" + 
	                		getResources().getString(R.string.browse_item_bids_total) + " " + Integer.toString(bid_count));
	                
	                if (!img_binary.isEmpty())
	                {
		                byte[] decodedString = Base64.decode(img_binary, Base64.DEFAULT);
		                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		                ImageView img = (ImageView) custom.findViewById(R.id.imgThumb);
		                img.setImageBitmap(decodedByte);
	                }
	                ImageView imgRate = (ImageView) custom.findViewById(R.id.imgStars);
	             // TODO Remove fake rate
	                //final Random random = new Random();
	                rate = nFakeRate - 1 > 0 ? nFakeRate-- : 0;
	                if(rate < 1)
	                {
	                	imgRate.setImageDrawable(null);
	                }
	                else
	                {
	                	final Options bitmapOptions=new Options();
	                	bitmapOptions.inDensity=1;
	                	bitmapOptions.inTargetDensity=1;
	                	Bitmap bmp = 
	                			BitmapFactory.decodeResource(getResources(), R.drawable.stars_222_32, bitmapOptions);
	                	bmp.setDensity(Bitmap.DENSITY_NONE);
	                	bmp = Bitmap.createBitmap(bmp, 0, 0, 32 * rate, 32);
	                	imgRate.setImageBitmap(bmp);
	                }

	                custom.setClickable(true);
	                custom.setOnClickListener(new View.OnClickListener()
	                {
	                    public void onClick(View v)
	                    {
	        	            //Toast.makeText(v.getContext(), "Enter to this auction", Toast.LENGTH_SHORT).show();	
	        	            showAuctionByID(v.getId());
	                    }
	                });

	                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	                params.setMargins(0, 10, 0, 10); // left, top, right, bottom
	                custom.setLayoutParams(params);
	                
	                layoutContent.addView(custom);	            
	            
	            } // End Loop	
	            if (jArray.length() == 0)
	            {
	                View v = inflater.inflate(R.layout.layout_empty_filter, null);
	                layoutContent.addView(v);	            	            	
	            }
	        } catch (JSONException e) 
	        {
	            Log.e("JSONException", "Error: " + e.toString());
	        } // catch (JSONException e)
            this.progressDialog.dismiss();
	        mCatalogContentTask = null;
	    } // protected void onPostExecute(final Boolean success)

	    @Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
	    	mCatalogContentTask = null;
		}
	} // class GetCatalogContentTask extends AsyncTask<Void, Void, Boolean>	

    @Override
    public void onPause() 
    {
    	super.onPause();
    	Log.i("INFO", "BrowseActivity paused");
    }
   
    @Override
    public void onResume() 
    {
        MySingleton.getInstance().setBrowseActivity(this);
    	super.onResume();
    	Log.i("INFO", "BrowseActivity resumed");    	
   }   	
}
