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

import com.eoksjon24.AuctionActivity.AddQuestionTask;
import com.eoksjon24.ProfileActivity.UserGetProfileTask;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public class InboxActivity extends Activity
{

	private GetInboxTask mInboxTask = null;
	private AnswerTask mAnswerTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setInboxActivity(this);
		setContentView(R.layout.activity_inbox);

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
		getMenuInflater().inflate(R.menu.inbox, menu);
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
	
	public void getInboxData()
	{		
		Log.i("INFO", "getInboxData");
		if (mInboxTask != null)
		{
			return;
		}
		mInboxTask = new GetInboxTask();
		mInboxTask.execute((Void) null);
	}
	
	public void makeAnswer(int post_id, int auction_id)
	{		
		Log.i("INFO", "makeAnswer");
		Log.i("A_ID", "a_id: " + auction_id);
		final Dialog dialogAnswer = new Dialog(MySingleton.getInstance().getInboxActivity());
		dialogAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAnswer.setContentView(R.layout.dialog_1edit_multiline);
		TextView tvFN = (TextView) dialogAnswer.findViewById(R.id.txtFieldName);
		tvFN.setText(R.string.prompt_inbox_answer);
		EditText etF = (EditText) dialogAnswer.findViewById(R.id.editField);
		etF.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE );
		etF.setHint(R.string.prompt_inbox_answer);
		etF.requestFocus();
		dialogAnswer.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		Button btnConfirm = (Button) dialogAnswer.findViewById(R.id.btn_confirm);
		btnConfirm.setText(R.string.text_inbox_dialog_btn_reply);
		btnConfirm.setId(post_id);
		btnConfirm.setTag(auction_id);
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		 		if (mAnswerTask != null)
				{
					return;
				}
				EditText etAnswer = (EditText)dialogAnswer.findViewById(R.id.editField);
	            String Answer = etAnswer.getText().toString();
	            dialogAnswer.dismiss();
	            Log.i("Answer", Answer);
				mAnswerTask = new AnswerTask(MySingleton.getInstance().getInboxActivity(), v.getId(), (Integer)v.getTag(), Answer);
				mAnswerTask.execute((Void) null);
		     }
		});		
		Button btnCancel = (Button) dialogAnswer.findViewById(R.id.btn_cancel);
		btnCancel.setText(R.string.text_CLOSE);
		btnCancel.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		    	 dialogAnswer.dismiss();
		     }
		 });
		dialogAnswer.show();
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
			View rootView = inflater.inflate(R.layout.fragment_inbox,
					container, false);
			
			
			
			MySingleton.getInstance().getInboxActivity().getInboxData();
			
			
			
			return rootView;
		}
	}

	class GetInboxTask extends AsyncTask<Void, Void, Boolean>
	{
	    private ProgressDialog progressDialog = new ProgressDialog(InboxActivity.this);
	    InputStream inputStream = null;
	    String result = ""; 

	    @Override
	    protected void onPreExecute() {
	        progressDialog.setMessage("Laadimine...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() 
	        {
				@Override
				public void onCancel(DialogInterface dialog)
				{
	            	GetInboxTask.this.cancel(true);
				}
	        });
	    }
	    
		@Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=account_inbox";
	        String postfix1 = "&activity=posts";
	        String postfix2 = "&stack=";
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
			
			url_select = url_select + postfix1 + postfix2 + stack;
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
//				JSONObject jObject = new JSONObject(result);
				
//                String result = jObject.getString("result");
//                boolean resOK = result.contains("OK") ? true : false;
//                if (resOK)
//               	{
//                Log.i("TASK", "GetInboxTask result OK");                	
//              }
				
                JSONArray jPostArray = new JSONArray(result);
        		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
        		ViewGroup layoutInbox = (ViewGroup) findViewById(R.id.layoutInbox);
        		layoutInbox.removeAllViewsInLayout();
            	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		
        		for (int i = 0; i < jPostArray.length(); i++)
        		{
            		JSONObject c = jPostArray.getJSONObject(i);
            		//Log.i("JSON obj", "res: " + c);
            		View vInboxItem = inflater.inflate(R.layout.layout_inbox_item, null);
            		vInboxItem.setBackgroundColor(Color.parseColor(i%2==0 ? "#ffffff" : "#eeeeee"));
            		
            		TextView tvAuctionName = (TextView) vInboxItem.findViewById(R.id.txtAuctionName);
            		tvAuctionName.setText(c.getString("article_title"));

            		TextView tvUserName = (TextView) vInboxItem.findViewById(R.id.txtUserName);
            		tvUserName.setText(c.getString("username"));
            		
            		TextView tvDateTimeQ = (TextView) vInboxItem.findViewById(R.id.txtDateTimeQ);
            		Date dateQ = null;
            		dateQ = sdf.parse(c.getString("post_ts"));
            		tvDateTimeQ.setText(dateQ != null ? dateQ.toLocaleString() : "");
            		                		                	
            		CheckBox cbReplied = (CheckBox) vInboxItem.findViewById(R.id.cbox_replied);
            		cbReplied.setChecked(!c.getString("is_commented").contains("false"));
            		
            		TextView tvPost = (TextView) vInboxItem.findViewById(R.id.txtBuyerQ);
            		tvPost.setText(c.getString("post"));
            		
            		TextView tvPostW = (TextView) vInboxItem.findViewById(R.id.txtBuyerWarning);            		
            		if (c.getString("post_warning").isEmpty() || c.getString("post_warning").contains("false"))
            			tvPostW.setVisibility(View.GONE);
            		else
            			tvPostW.setText(getResources().getString(R.string.text_inbox_post_warning));

            		LinearLayout llReply = (LinearLayout) vInboxItem.findViewById(R.id.layoutReply); 
            		Button bt_inbox_answer = (Button) vInboxItem.findViewById(R.id.btn_inbox_answer);
            		bt_inbox_answer.setId(c.getInt("id"));
            		bt_inbox_answer.setTag((Integer)c.getInt("auction_id"));
            		if (c.getString("is_commented").contains("false"))
            		{
            			llReply.setVisibility(View.GONE);            			
            		}
            		else
            		{
            			bt_inbox_answer.setVisibility(View.GONE);            
            			
            			TextView tvDateTimeA = (TextView) vInboxItem.findViewById(R.id.txtDateTimeA);
                		Date dateA = null;
                		dateA = sdf.parse(c.getString("comment_ts"));
                		tvDateTimeA.setText(dateA != null ? dateA.toLocaleString() : "");
                		                		                	
                		TextView tvAnswer = (TextView) vInboxItem.findViewById(R.id.txtSellerA);
                		tvAnswer.setText(c.getString("comment"));
                		
                		TextView tvAnswerW = (TextView) vInboxItem.findViewById(R.id.txtSellerWarning);
                		tvAnswerW.setText(c.getString("comment_warning"));
                		if (c.getString("comment_warning").isEmpty() || c.getString("comment_warning").contains("false"))
                			tvAnswerW.setVisibility(View.GONE); 
                		else
                			tvAnswerW.setText(getResources().getString(R.string.text_inbox_post_warning));
            		}
            		
            		OnClickListener oclAnswer = new OnClickListener()
            		{
						@Override
						public void onClick(View v)
						{
							MySingleton.getInstance().getInboxActivity().makeAnswer(v.getId(), (Integer)v.getTag());
						}            			
            		};
            		bt_inbox_answer.setOnClickListener(oclAnswer);
            		
            		layoutInbox.addView(vInboxItem, i);	            
        		}
        		
        		
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
            this.progressDialog.dismiss();
            mInboxTask = null;			
	    } // protected void onPostExecute(final Boolean success)
	    
		@Override
		protected void onCancelled()
		{
            this.progressDialog.dismiss();
            mInboxTask = null;
		}
	} // class GetInboxTask extends AsyncTask<Void, Void, Boolean>	
	
	public class AnswerTask extends AsyncTask<Void, Void, Boolean>
	{
	    public InboxActivity activity;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    private String sAnswer;
	    private int nPostID;
	    private int nAuctionID;
	    
	    public AnswerTask(InboxActivity a, int post_id, int auction_id, String answer)
	    {
	    	this.activity = a;
	    	sAnswer = answer;
	    	nPostID = post_id;
	    	nAuctionID = auction_id;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=auction_post_comment_submit";
	        String postfix1 = "&post_id=";
	        String postfix2 = "&auction_id=";
	        String postfix3 = "&post_content=";
	        String postfix4 = "&stack=";
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
			
			url_select = url_select + postfix1 + nPostID + postfix2 + nAuctionID + postfix3 + sAnswer + postfix4 + stack;
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
                	Log.i("TASK", "AnswerTask result OK");  
            		MySingleton.getInstance().getInboxActivity().getInboxData();
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
			mAnswerTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mAnswerTask = null;
		}
	}	

	
	
	
	
	
}
