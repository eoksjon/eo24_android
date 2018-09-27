package com.eoksjon24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends Activity
{

	private GetEulaTask mEulaTask = null;
	private CheckUserAvailableTask mUserCheckTask = null;
	private UserRegistrationTask mRegistrationTask = null;
//	public TextView txtEULA;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setRegistrationActivity(this);
		setContentView(R.layout.activity_registration);

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
		getMenuInflater().inflate(R.menu.registration, menu);
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
		public TextView txtEULA;

		public PlaceholderFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_registration,
					container, false);
			txtEULA = (TextView) rootView.findViewById(R.id.textViewAgreement);
			((RegistrationActivity)getActivity()).getEULA();

			 EditText txtUserName = (EditText) rootView.findViewById(R.id.account_name);

			 txtUserName.setOnFocusChangeListener(new OnFocusChangeListener() 
			 {          
			        public void onFocusChange(View v, boolean hasFocus) 
			        {
			            if (!hasFocus) 
			            {
			               // code to execute when EditText loses focus
			            	((RegistrationActivity)getActivity()).checkUserAvailable();
			            }
			        }
			});	
			 
			 
			Button btRegistration = (Button) rootView.findViewById(R.id.registration_button);
			btRegistration.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					MySingleton.getInstance().getRegistrationActivity().userRegistration();
				}				
			});
			
			return rootView;
		}
	}
	
	public void getEULA()
	{		
		if (mEulaTask != null)
		{
			return;
		}
		mEulaTask = new GetEulaTask();
		mEulaTask.execute((Void) null);
	}

	public void checkUserAvailable()
	{		
		if (mUserCheckTask != null)
		{
			return;
		}
		mUserCheckTask = new CheckUserAvailableTask();
		mUserCheckTask.execute((Void) null);
	}

	public void userRegistration()
	{		
		if (mRegistrationTask != null)
		{
			return;
		}
		mRegistrationTask = new UserRegistrationTask(this, 
				"37511100243", 
				"dimsik", 
				"dimsik", 
				"dims.tallinn@gmail.com", 
				"DMITRI", 
				"BUGRÕŠOV", 
				"Tallinn Nelgi 31-48", 
				"Harjumaa", 
				"11213", 
				"EE", 
				"3725114953", 
				"true", 
				"true");
		mRegistrationTask.execute((Void) null);
	}

	class GetEulaTask extends AsyncTask<Void, Void, Boolean>
	{

	    private ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Andmeid laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	GetEulaTask.this.cancel(true);
	            }
	        });
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {

	        String url_select = "http://www.eoksjon24.ee/ea.api?action=eula&language=et";

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
	            int id = 0;
	            String agreement = "";
	        	JSONArray jArray = new JSONArray(result);    
	            for(int i=0; i < jArray.length(); i++) 
	            {

	                JSONObject jObject = jArray.getJSONObject(i);

	                id = jObject.getInt("id");
	                agreement = jObject.getString("agreement");
	                Log.i("JSON parsing", "Result: id=" + Integer.toString(id) + 
	                		", agreement=" + agreement);

	            } // End Loop
	            this.progressDialog.dismiss();
	            TextView tv = (TextView)findViewById(R.id.textViewAgreement);
	            tv.setText(Html.fromHtml(agreement));
	            tv.setMovementMethod(new ScrollingMovementMethod());
	            } catch (JSONException e) 
	        {
	            Log.e("JSONException", "Error: " + e.toString());
	        } // catch (JSONException e)
	        mEulaTask = null;
	    } // protected void onPostExecute(final Boolean success)

		@Override
		protected void onCancelled()
		{
			mEulaTask = null;
		}
	} //class GetCatalogTask extends AsyncTask<Void, Void, Boolean>
	
	class CheckUserAvailableTask extends AsyncTask<Void, Void, Boolean>
	{
		String UserName = "";
	    private ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Andmeid laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
	            public void onCancel(DialogInterface arg0) 
	            {
	            	CheckUserAvailableTask.this.cancel(true);
	            }
	        });
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) 
	    {

	        String url_select = "http://www.eoksjon24.ee//ea.api?action=validation&activity=username_availability";
			EditText txtUserName = (EditText) findViewById(R.id.account_name);
			UserName = txtUserName.getText().toString();
			if (!UserName.isEmpty())
			{
				url_select += "&username=" + UserName;
			}
			else
			{
		        mUserCheckTask = null;
		        return false;
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
            this.progressDialog.dismiss();
	        //parse JSON data
	        try 
	        {
	        	Boolean show_error = false;
	        	JSONObject jObject = new JSONObject(result);
	            result = jObject.getString("result");
	        	String newusername = jObject.getString("username");
                Log.i("JSON parsing", "Result: " + result + 
                		", newusername=" + newusername);

				EditText txtUserName = (EditText) findViewById(R.id.account_name);
				txtUserName.setText(newusername);
				
				if (newusername.isEmpty())
				{
					txtUserName.setError(getString(R.string.error_field_required));
					show_error = true;
				}
				else if (result.contains("OCCUPIED"))
				{
					txtUserName.setError(getString(R.string.registration_user_occupied));					
					show_error = true;
				}
				else if (result.contains("ERROR_LENGTH"))
				{
					JSONObject jConfigObject = new JSONObject(jObject.getString("config"));
					int min = jConfigObject.getInt("min");
					int max = jConfigObject.getInt("max");
					txtUserName.setError(getString(R.string.registration_user_lenght_error) + 
							": min = " + Integer.toString(min) + ", max = " + Integer.toString(max));					
					show_error = true;
				}
				else if (result.contains("ERROR"))
				{
					txtUserName.setError(getString(R.string.registration_user_occupied));
					show_error = true;
				}
			
				if (show_error)
				{
					txtUserName.requestFocus();
				}
				
				
	            } catch (JSONException e) 
	        {
	            Log.e("JSONException", "Error: " + e.toString());
	        } // catch (JSONException e)
	        mUserCheckTask = null;
	    } // protected void onPostExecute(final Boolean success)

		@Override
		protected void onCancelled()
		{
			mUserCheckTask = null;
		}
	} //class CheckUserAvailable extends AsyncTask<Void, Void, Boolean>

	public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean>
	{
	    public RegistrationActivity activity;
	    
	    private String sCode = "";
	    private String sUserName = "";
	    private String sPasswd = "";
	    private String sEmail = "";
	    private String sFirstName = "";
	    private String sLastName = "";	    
	    private String sAddress_street = "";
	    private String sAddress_locality = "";
	    private String sAddress_postal = "";
	    private String sAddress_country = "";
	    private String sPhone = "";
	    private String sNewletter_system = "";
	    private String sNewletter_partner = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserRegistrationTask(RegistrationActivity a, String v1, String v2, String v3, String v4,
	    		String v5, String v6, String v7, String v8, String v9, String v10, String v11, String v12, String v13)
	    {
	    	this.activity = a;
		    this.sCode = v1;
		    this.sUserName = v2;
		    this.sPasswd = v3;
		    this.sEmail = v4;
		    this.sFirstName = v5;
		    this.sLastName = v6;	    
	    	this.sAddress_street = v7;
	    	this.sAddress_locality = v8;
	    	this.sAddress_postal = v9;
	    	this.sAddress_country = v10;
	    	this.sPhone = v11;
	    	this.sNewletter_system = v12;
	    	this.sNewletter_partner = v13;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	//String url_encode = "";
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=register_user";
	        String postfix2 = "&code=";
	        String postfix3 = "&username=";
	        String postfix4 = "&passwd=";
	        String postfix5 = "&email=";
	        String postfix6 = "&firstname=";
	        String postfix7 = "&lastname=";
	        String postfix8 = "&address_street=";
	        String postfix9 = "&address_locality=";
	        String postfix10 = "&address_postal=";
	        String postfix11 = "&address_country=";
	        String postfix12 = "&phone=";
	        String postfix13 = "&newletter_system=";
	        String postfix14 = "&newletter_partner=";
	        String postfix15 = "&stack=";
	        String postfix16 = "&language=et";
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
			
			url_select = url_select + postfix1 + 
					postfix2 + sCode + 
					postfix3 + sUserName + 
					postfix4 + sPasswd + 
					postfix5 + sEmail + 
					postfix6 + sFirstName + 
					postfix7 + sLastName + 
					postfix8 + sAddress_street + 
					postfix9 + sAddress_locality + 
					postfix10 + sAddress_postal + 
					postfix11 + sAddress_country + 
					postfix12 + sPhone + 
					postfix13 + sNewletter_system + 
					postfix14 + sNewletter_partner + 
					postfix15 + stack +
					postfix16 ;
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
                	Log.i("TASK", "UserUpdateProfileTask result OK"); 
                	MySingleton.getInstance().getAuthActivity().finish();
                	MySingleton.getInstance().getRegistrationActivity().finish();
               	}
                else
               	{
                	Log.i("TASK", "UserUpdateProfileTask result ERROR");                	
               	}                	
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        //getProfileData();			
	        mRegistrationTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mRegistrationTask = null;
		}
	}
	
	
	
	
	
}
