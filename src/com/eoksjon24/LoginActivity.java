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
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity
{
	final String SAVED_LOGIN = "saved_login";
	final String SAVED_PASSW = "saved_passw";
	
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mLogin;
	private String mPassword;

	// UI references.
	private EditText mLoginView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private CheckBox mSavePassw;

	SharedPreferences sPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setLoginActivity(this);
		setContentView(R.layout.activity_login);
		
		//TODO: Put this to Singleton
		sPref = getSharedPreferences("config", MODE_PRIVATE);
		String savedLogin = sPref.getString(SAVED_LOGIN, "");
		String savedPassw = sPref.getString(SAVED_PASSW, "");
		mLoginView = (EditText) findViewById(R.id.login);
		mLoginView.setText(savedLogin);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(savedPassw);
		mSavePassw = (CheckBox) findViewById(R.id.cbSavePassw);
		mSavePassw.setChecked(!savedLogin.isEmpty() && !savedPassw.isEmpty());


		
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener()
				{
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent)
					{
						if (id == R.id.login || id == EditorInfo.IME_NULL)
						{
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin()
	{
		if (mAuthTask != null)
		{
			return;
		}

		// Reset errors.
		mLoginView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mLogin = mLoginView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (mPassword.length() < 4)
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mLogin))
		{
			mLoginView.setError(getString(R.string.error_field_required));
			focusView = mLoginView;
			cancel = true;
		}
//		else if (!mEmail.contains("@"))
//		{
//			mEmailView.setError(getString(R.string.error_invalid_email));
//			focusView = mEmailView;
//			cancel = true;
//		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			// Check if no view has focus:
			View view = this.getCurrentFocus();
			if (view != null)
			{  
			    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
			
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask(this);
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		}
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void setLoginOn()
	{
		// TODO Auto-generated method stub
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
	{
	    public LoginActivity activity;
	    
		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserLoginTask(LoginActivity a)
	    {
	    	this.activity = a;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=login";
	        String postfix1 = "&username=" + mLogin;
	        String postfix2 = "&stack=";
	        String stack = "";

	        // TODO: attempt authentication against a network service.
			try
			{
				stack = MySingleton.getInstance().SHA1(MySingleton.getInstance().getSALT())
						+ MySingleton.getInstance().SHA1(mPassword);
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
//			for (String credential : DUMMY_CREDENTIALS)
//			{
//				String[] pieces = credential.split(":");
//				if (pieces[0].equals(mEmail))
//				{
//					// Account exists, return true if the password matches.
//					return pieces[1].equals(mPassword);
//				}
//			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success)
		{
			Boolean passwok = false;
			
			Log.i("RESULT", result);

        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                passwok = result.contains("OK") ? true : false;
                if (passwok)
               	{
                	MySingleton.getInstance().setUserID(jObject.getInt("id"));
                	MySingleton.getInstance().setUserName(jObject.getString("username"));
                	MySingleton.getInstance().setUserFullName(jObject.getString("fullname"));
                	MySingleton.getInstance().setUserEmail(jObject.getString("email"));
                	MySingleton.getInstance().setUserLanguage(jObject.getString("language"));
                	MySingleton.getInstance().setUserAvatarBASE64(jObject.getJSONObject("avatar").getString("binary"));
                	MySingleton.getInstance().setUserRole(jObject.getString("role"));
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			if (!passwok)
			{
				mLoginView
					.setError(getString(R.string.error_incorrect_account_or_password));
				mPasswordView
						.setError(getString(R.string.error_incorrect_account_or_password));
				mLoginView.requestFocus();
			}
			showProgress(false);
			mAuthTask = null;
			if (passwok)
			{
				//TODO: Put this to Singleton
				sPref = getSharedPreferences("config", MODE_PRIVATE);
				Editor ed = sPref.edit();
				ed.putString(SAVED_LOGIN, mSavePassw.isChecked() ? mLoginView.getText().toString() : "");
				ed.putString(SAVED_PASSW, mSavePassw.isChecked() ? mPasswordView.getText().toString() : "");
				ed.commit();

				MySingleton.getInstance().getMainActivity().setLoginOnOff();
				finish();
			}
		}

		@Override
		protected void onCancelled()
		{
			mAuthTask = null;
			showProgress(false);
		}
	}
}
