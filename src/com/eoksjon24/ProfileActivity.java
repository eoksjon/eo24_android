package com.eoksjon24;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;

public class ProfileActivity extends Activity {

    private static final int CAMERA_REQUEST = 1888; 
    private static final int CROP_REQUEST = 1889; 
    private static final int PICK_FROM_FILE_REQUEST = 1890; 

	private UserGetProfileTask mProfileTask = null;
	private UserSetAvatarTask mSetAvatarTask = null;
	private UserRemoveAvatarTask mRemoveAvatarTask = null;
	private UserChangePasswordTask mChangePasswordTask = null;
	private UserAddEmailTask mAddEmailTask = null;
	private UserActiveEmailTask mActiveEmailTask = null;
	private UserRemoveEmailTask mRemoveEmailTask = null;
	private UserAddBankTask mAddBankTask = null;
	private UserActiveBankTask mActiveBankTask = null;
	private UserRemoveBankTask mRemoveBankTask = null;
	private UserUpdateProfileTask mUpdateProfileTask = null;
	
	
	ArrayList<HashMap<String, String>> aEmailList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> aBankList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        MySingleton.getInstance().setProfileActivity(this);
		setContentView(R.layout.activity_profile);
		if (savedInstanceState == null) 
		{
				getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
        getProfileData();			
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
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

	    private ImageView imgAvatar;

	    public PlaceholderFragment() 
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

			this.imgAvatar = (ImageView)rootView.findViewById(R.id.imgUserAvatar);
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
	        Button btRemoveAvatar = (Button) rootView.findViewById(R.id.button_remove_avatar);		
	        btRemoveAvatar.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
		            MySingleton.getInstance().setUserAvatarBASE64("");		            
		            ((ProfileActivity)getActivity()).removeUserAvatarFromServer();
	            }
	        });		
	        Button btChangePassword = (Button) rootView.findViewById(R.id.button_change_password);		
	        btChangePassword.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {		            
	            	((ProfileActivity)getActivity()).changePassword();
	            }
	        });		
	        Button btAddNewEmail = (Button) rootView.findViewById(R.id.btnAddNewEmail);		
	        btAddNewEmail.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
		            ((ProfileActivity)getActivity()).addNewEmail();
	            }
	        });		
	        Button btAddNewBank = (Button) rootView.findViewById(R.id.btnAddNewBank);		
	        btAddNewBank.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
		            ((ProfileActivity)getActivity()).addNewBank();
	            }
	        });		
	        Button btSave = (Button) rootView.findViewById(R.id.save_button);		
	        btSave.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
		            ((ProfileActivity)getActivity()).updateProfileData();
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
	        	  //Create an instance of bundle and get the returned data
	        	  //Bundle extras = data.getExtras();
	        	  //get the cropped bitmap from extras
	        	  //Bitmap thePic = extras.getParcelable("data");
	        	  //set image bitmap to image view
	        	  //imVCature_pic.setImageBitmap(thePic);
	        	Bitmap photo = (Bitmap) data.getExtras().get("data"); 
	            imgAvatar.setImageBitmap(photo);
	            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
	            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
	            byte[] byteArray = byteArrayOutputStream.toByteArray();
	            String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
	            MySingleton.getInstance().setUserAvatarBASE64(encoded);
	            
	            ((ProfileActivity)getActivity()).putUserAvatarToServer();
	            
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
			 //indicate aspect of desired crop
			 cropIntent.putExtra("aspectX", 1);
			 cropIntent.putExtra("aspectY", 1);
			 //indicate output X and Y
			 cropIntent.putExtra("outputX", 256);
			 cropIntent.putExtra("outputY", 256);
			 //retrieve data on return
			 cropIntent.putExtra("return-data", true);
			 //start the activity - we handle returning in onActivityResult
			 startActivityForResult(cropIntent, CROP_REQUEST);			
		}
	}

	public void getProfileData()
	{		
		Log.i("INFO", "getProfileData");
		if (mProfileTask != null)
		{
			return;
		}
		mProfileTask = new UserGetProfileTask(this);
		mProfileTask.execute((Void) null);
	}

	public void showProfileData() throws JSONException
	{		
		Log.i("INFO", "showProfileData");
		if (mProfileTask != null)
		{
			Log.e("ERROR", "showProfileData is running state !");
			return;
		}
		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);

        byte[] decodedString = Base64.decode(MySingleton.getInstance().getUserAvatarBASE64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		
		((TextView) frag1.getView().findViewById(R.id.textFullName)).setText(MySingleton.getInstance().getUserFullName());
		((TextView) frag1.getView().findViewById(R.id.textIK)).setText(MySingleton.getInstance().getUserIK());
		((TextView) frag1.getView().findViewById(R.id.textUserName)).setText(MySingleton.getInstance().getUserName());
		((ImageView) frag1.getView().findViewById(R.id.imgUserAvatar)).setImageBitmap(decodedByte);
		((TextView) frag1.getView().findViewById(R.id.textEmail)).setText(MySingleton.getInstance().getUserEmail());
		((CheckBox) frag1.getView().findViewById(R.id.cbox_mail_news1)).setChecked((MySingleton.getInstance().getNewletterSystem()).contains("true"));
		((CheckBox) frag1.getView().findViewById(R.id.cbox_mail_news2)).setChecked((MySingleton.getInstance().getNewletterPartner()).contains("true"));
		((EditText) frag1.getView().findViewById(R.id.street_house)).setText(MySingleton.getInstance().getUserAddressStreet());
		((EditText) frag1.getView().findViewById(R.id.city)).setText(MySingleton.getInstance().getUserAddressLocality());
		((EditText) frag1.getView().findViewById(R.id.postal)).setText(MySingleton.getInstance().getUserAddressPostal());
		((EditText) frag1.getView().findViewById(R.id.country)).setText(MySingleton.getInstance().getUserAddressCountry());
		((EditText) frag1.getView().findViewById(R.id.phone_nr)).setText(MySingleton.getInstance().getUserPhone());
    	
    	ViewGroup layoutEmail = (ViewGroup) findViewById(R.id.layoutEmail);
    	layoutEmail.removeAllViewsInLayout();
    	ViewGroup layoutBank = (ViewGroup) findViewById(R.id.layoutBank);
    	layoutBank.removeAllViewsInLayout();
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < MySingleton.getInstance().getUserEmailArray().length(); i++)
    	{                    	
    		JSONObject c = MySingleton.getInstance().getUserEmailArray().getJSONObject(i);
    		//String id = c.getString("id");
    		String email = c.getString("email");
    		String is_active = c.getString("is_active");

    		View vEmailItem = inflater.inflate(R.layout.layout_email_item, null);
    		ImageView imgEmail = (ImageView) vEmailItem.findViewById(R.id.imgEmailItem);
    		TextView tvEmail = (TextView) vEmailItem.findViewById(R.id.txtEmailItem);
    		tvEmail.setText(email);
    		if (is_active.contains("true"))
    		{
        		imgEmail.setColorFilter( Color.BLUE, Mode.SRC_ATOP );
        		tvEmail.setTextColor(Color.BLUE);
    		}
    		CheckBox cbActiveEmail = (CheckBox) vEmailItem.findViewById(R.id.cboxActiveEmail);
    		cbActiveEmail.setChecked(is_active.contains("true"));
    		cbActiveEmail.setClickable(!is_active.contains("true"));
    		cbActiveEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    		{
    		    @Override
    		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		    {
	    		    if(isChecked)
	    		    {
	    		    	RelativeLayout rlItem = (RelativeLayout)buttonView.getParent();
	    		    	LinearLayout llContainer = (LinearLayout)rlItem.getParent();
	    		    	int index = llContainer.indexOfChild(rlItem);
	    		    	activeEmail(MySingleton.getInstance().getUserEmailByIndex(index));
    		        }
    		    }
    		});    		
    		Button btRemoveEmail = (Button) vEmailItem.findViewById(R.id.btnRemoveEmail);
    		btRemoveEmail.setEnabled(!is_active.contains("true"));
    		btRemoveEmail.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
    		    	RelativeLayout rlItem = (RelativeLayout)v.getParent();
    		    	LinearLayout llContainer = (LinearLayout)rlItem.getParent();
    		    	int index = llContainer.indexOfChild(rlItem);
		            removeEmail(MySingleton.getInstance().getUserEmailByIndex(index));
	            }
	        });		
    		
    		layoutEmail.addView(vEmailItem, i);	            
    	}
        
        
    	for (int j = 0; j < MySingleton.getInstance().getUserBankArray().length(); j++)
    	{                    	
    		JSONObject c = MySingleton.getInstance().getUserBankArray().getJSONObject(j);
    		//String id = c.getString("id");
    		String account_no = c.getString("account_no");
    		String account_bank = c.getString("account_bank");
    		String is_active = c.getString("is_active");
    		
    		View vBankItem = inflater.inflate(R.layout.layout_bank_item, null);
    		ImageView imgBank = (ImageView) vBankItem.findViewById(R.id.imgBankItem);
    		TextView tvBank = (TextView) vBankItem.findViewById(R.id.txtBankItem);
    		tvBank.setText(account_no);
    		TextView tvBankName = (TextView) vBankItem.findViewById(R.id.txtBankName);
    		tvBankName.setText(account_bank);
    		if (is_active.contains("true"))
    		{
    			imgBank.setColorFilter( Color.BLUE, Mode.SRC_ATOP );
    			tvBank.setTextColor(Color.BLUE);
    			tvBankName.setTextColor(Color.BLUE);
    		}
    		CheckBox cbActiveBank = (CheckBox) vBankItem.findViewById(R.id.cboxActiveBank);
    		cbActiveBank.setChecked(is_active.contains("true"));
    		cbActiveBank.setClickable(!is_active.contains("true"));
    		cbActiveBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    		{
    		    @Override
    		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		    {
	    		    if(isChecked)
	    		    {
	    		    	RelativeLayout rlItem = (RelativeLayout)buttonView.getParent();
	    		    	LinearLayout llContainer = (LinearLayout)rlItem.getParent();
	    		    	int index = llContainer.indexOfChild(rlItem);
	    		    	activeBank(MySingleton.getInstance().getUserBankByIndex(index));
	    		    }
    		    }
    		});    		
    		
    		Button btRemoveBank = (Button) vBankItem.findViewById(R.id.btnRemoveBank);
    		btRemoveBank.setEnabled(!is_active.contains("true"));
    		btRemoveBank.setOnClickListener(new View.OnClickListener() 
	        {
	            @Override
	            public void onClick(View v) 
	            {
    		    	RelativeLayout rlItem = (RelativeLayout)v.getParent();
    		    	LinearLayout llContainer = (LinearLayout)rlItem.getParent();
    		    	int index = llContainer.indexOfChild(rlItem);
		            removeBank(MySingleton.getInstance().getUserBankByIndex(index));
	            }
	        });		
    		
    		layoutBank.addView(vBankItem, j);	            
    	}
	}

	public void updateProfileData()
	{		
		Log.i("INFO", "updateProfileData");
		if (mUpdateProfileTask != null)
		{
			return;
		}

		Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
		
		String s1 = (String) ((EditText) frag1.getView().findViewById(R.id.street_house)).getText().toString();
		String s2 = (String) ((EditText) frag1.getView().findViewById(R.id.city)).getText().toString();
		String s3 = (String) ((EditText) frag1.getView().findViewById(R.id.postal)).getText().toString();
		String s4 = (String) ((EditText) frag1.getView().findViewById(R.id.country)).getText().toString();
		String s5 = (String) ((EditText) frag1.getView().findViewById(R.id.phone_nr)).getText().toString();
		String s6 = ((CheckBox) frag1.getView().findViewById(R.id.cbox_mail_news1)).isChecked() ? "true" : "false";
		String s7 = ((CheckBox) frag1.getView().findViewById(R.id.cbox_mail_news2)).isChecked() ? "true" : "false";

		mUpdateProfileTask = new UserUpdateProfileTask(this, s1, s2, s3, s4, s5, s6, s7);
		mUpdateProfileTask.execute((Void) null);
	}
	public void putUserAvatarToServer()
	{		
		Log.i("INFO", "setUserAvatar");
		if (mSetAvatarTask != null)
		{
			return;
		}
		mSetAvatarTask = new UserSetAvatarTask(this);
		mSetAvatarTask.execute((Void) null);
	}

	public void removeUserAvatarFromServer()
	{		
		Log.i("INFO", "removeUserAvatarFromServer");
		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(R.string.prompt_remove_avatar);
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
				if (mRemoveAvatarTask != null)
				{
					return;
				}
				mRemoveAvatarTask = new UserRemoveAvatarTask(MySingleton.getInstance().getProfileActivity());
				mRemoveAvatarTask.execute((Void) null);
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		dialog.show();                			
	}

	public void changePassword()
	{		
		Log.i("INFO", "changePassword");
		final Dialog dialogChangePassword = new Dialog(MySingleton.getInstance().getProfileActivity());
		dialogChangePassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogChangePassword.setContentView(R.layout.dialog_change_password);
		Button btnConfirm = (Button) dialogChangePassword.findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
				if (mChangePasswordTask != null)
				{
					return;
				}
				EditText etOldPass = (EditText)dialogChangePassword.findViewById(R.id.password);
	            String oldpass = etOldPass.getText().toString();
				EditText etNewPass = (EditText)dialogChangePassword.findViewById(R.id.password_new);
	            String newpass = etNewPass.getText().toString();
				EditText etRetryPass = (EditText)dialogChangePassword.findViewById(R.id.password_retry);
	            String retrypass = etRetryPass.getText().toString();
	            dialogChangePassword.dismiss();
	            Log.i("PASS", oldpass + ", " + newpass + ", " + retrypass);
	            mChangePasswordTask = new UserChangePasswordTask(MySingleton.getInstance().getProfileActivity(), oldpass, newpass, retrypass);
				mChangePasswordTask.execute((Void) null);
		     }
		 });		
		Button btnCancel = (Button) dialogChangePassword.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		    	 dialogChangePassword.dismiss();
		     }
		 });
		dialogChangePassword.show();
		
/*		dialog.setTitle(R.string.promptChangePassword);
		LinearLayout layout = new LinearLayout(MySingleton.getInstance().getProfileActivity());
		layout.setOrientation(LinearLayout.VERTICAL);
		final EditText etOldPassword = new EditText(MySingleton.getInstance().getProfileActivity());
		etOldPassword.setHint(R.string.prompt_old_password);
		etOldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(etOldPassword);
		final EditText etNewPassword = new EditText(MySingleton.getInstance().getProfileActivity());
		etNewPassword.setHint(R.string.prompt_new_password);
		etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(etNewPassword);
		final EditText etNewPasswordRetry = new EditText(MySingleton.getInstance().getProfileActivity());
		etNewPasswordRetry.setHint(R.string.prompt_new_password_retry);
		etNewPasswordRetry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		layout.addView(etNewPasswordRetry);
		dialog.setView(layout);
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
				if (mChangePasswordTask != null)
				{
					return;
				}
				EditText edit=(EditText)dialog.findViewById(R.id.);
	            String text=edit.getText().toString();
	            mChangePasswordTask = new UserChangePasswordTask(MySingleton.getInstance().getProfileActivity());
				mChangePasswordTask.execute((Void) null);
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		
		dialog.show(); */						
	}

	public void addNewEmail()
	{		
		Log.i("INFO", "addNewEmail");
		final Dialog dialogAddNewEmail = new Dialog(MySingleton.getInstance().getProfileActivity());
		dialogAddNewEmail.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAddNewEmail.setContentView(R.layout.dialog_1edit);
		TextView tvFN = (TextView) dialogAddNewEmail.findViewById(R.id.txtFieldName);
		tvFN.setText(R.string.prompt_new_email);
		EditText etF = (EditText) dialogAddNewEmail.findViewById(R.id.editField);
		etF.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
		etF.setHint(R.string.prompt_new_email);

		Button btnConfirm = (Button) dialogAddNewEmail.findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		 		if (mAddEmailTask != null)
				{
					return;
				}
				EditText etEmail = (EditText)dialogAddNewEmail.findViewById(R.id.editField);
	            String email = etEmail.getText().toString();
	            dialogAddNewEmail.dismiss();
	            Log.i("EMAIL", email);
				mAddEmailTask = new UserAddEmailTask(MySingleton.getInstance().getProfileActivity(), email);
				mAddEmailTask.execute((Void) null);
		     }
		 });		
		Button btnCancel = (Button) dialogAddNewEmail.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		    	 dialogAddNewEmail.dismiss();
		     }
		 });
		dialogAddNewEmail.show();
	}

	public void removeEmail(String email)
	{		
		final String fsRemoveEmail = email;
		Log.i("INFO", "removeEmail: " + email);
		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(getResources().getString(R.string.prompt_remove_email) + "\n" + email + " ?");
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
				if (mRemoveEmailTask != null)
				{
					return;
				}
				mRemoveEmailTask = new UserRemoveEmailTask(MySingleton.getInstance().getProfileActivity(), fsRemoveEmail);
				mRemoveEmailTask.execute((Void) null);
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		dialog.show();
	}

	public void activeEmail(String email)
	{		
		Log.i("INFO", "activeEmail: " + email);
		if (mActiveEmailTask != null)
		{
			return;
		}
		mActiveEmailTask = new UserActiveEmailTask(this, email);
		mActiveEmailTask.execute((Void) null);
	}

	public void addNewBank()
	{		
		Log.i("INFO", "addNewBank");
		final Dialog dialogAddNewBank = new Dialog(MySingleton.getInstance().getProfileActivity());
		dialogAddNewBank.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAddNewBank.setContentView(R.layout.dialog_1edit);
		TextView tvFN = (TextView) dialogAddNewBank.findViewById(R.id.txtFieldName);
		tvFN.setText(R.string.prompt_new_bank);
		EditText etF = (EditText) dialogAddNewBank.findViewById(R.id.editField);
		etF.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS );
		etF.setHint(R.string.prompt_new_bank);

		Button btnConfirm = (Button) dialogAddNewBank.findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		 		if (mAddBankTask != null)
				{
					return;
				}
				EditText etBank = (EditText)dialogAddNewBank.findViewById(R.id.editField);
	            String bank = etBank.getText().toString();
	            dialogAddNewBank.dismiss();
	            Log.i("BANK", bank);
	            mAddBankTask = new UserAddBankTask(MySingleton.getInstance().getProfileActivity(), bank);
	            mAddBankTask.execute((Void) null);
		     }
		 });		
		Button btnCancel = (Button) dialogAddNewBank.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() 
		{
		     public void onClick(View v) 
		     {
		    	 dialogAddNewBank.dismiss();
		     }
		 });
		dialogAddNewBank.show();
	}

	public void removeBank(String bank)
	{		
		final String fsRemoveBank = bank;
		Log.i("INFO", "removeBank: " + bank);
		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(getResources().getString(R.string.prompt_remove_bank) + "\n" + bank + " ?");
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
				if (mRemoveBankTask != null)
				{
					return;
				}
				mRemoveBankTask = new UserRemoveBankTask(MySingleton.getInstance().getProfileActivity(), fsRemoveBank);
				mRemoveBankTask.execute((Void) null);
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		dialog.show();
	}

	public void activeBank(String bank)
	{		
		Log.i("INFO", "activeBank: " + bank);
		if (mActiveBankTask != null)
		{
			return;
		}
		mActiveBankTask = new UserActiveBankTask(this, bank);
		mActiveBankTask.execute((Void) null);
	}

	
	public class UserGetProfileTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserGetProfileTask(ProfileActivity a)
	    {
	    	this.activity = a;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	        String url_select = "http://www.eoksjon24.ee/ea.api?action=account";
	        String postfix1 = "&username=" + MySingleton.getInstance().getUserName();
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
			Log.i("RESULT", result);
        	try
			{
				JSONObject jObject = new JSONObject(result);
                String result = jObject.getString("result");
                boolean resOK = result.contains("OK") ? true : false;
                if (resOK)
               	{
                	MySingleton.getInstance().setUserIK(jObject.getJSONObject("account").getString("code"));
                	MySingleton.getInstance().setUserAddressStreet(jObject.getJSONObject("account").getString("address_street"));
                	MySingleton.getInstance().setUserAddressLocality(jObject.getJSONObject("account").getString("address_locality"));
                	MySingleton.getInstance().setUserAddressPostal(jObject.getJSONObject("account").getString("address_postal"));
                	MySingleton.getInstance().setUserAddressCountry(jObject.getJSONObject("account").getString("address_country"));
                	MySingleton.getInstance().setUserPhone(jObject.getJSONObject("account").getString("phone"));
                	MySingleton.getInstance().setNewletterSystem(jObject.getJSONObject("account").getString("newletter_system"));               	
                	MySingleton.getInstance().setNewletterPartner(jObject.getJSONObject("account").getString("newletter_partner"));               	
                	MySingleton.getInstance().setUserEmailArray(jObject.getJSONArray("email"));
                	MySingleton.getInstance().setUserBankArray(jObject.getJSONArray("bank"));
                	
                	Log.i("INFO_EMAIL", MySingleton.getInstance().getUserEmailArray().toString());
                	Log.i("INFO_BANK", MySingleton.getInstance().getUserBankArray().toString());
                	
                	for (int i = 0; i < MySingleton.getInstance().getUserEmailArray().length(); i++)
                	{                    	
                		JSONObject c = MySingleton.getInstance().getUserEmailArray().getJSONObject(i);
                		HashMap<String, String> map = new HashMap<String, String>();
                		String id = c.getString("id");
                		String email = c.getString("email");
                		String is_active = c.getString("is_active");
                		map.put("id", id);
                		map.put("email", email);
                		map.put("is_active", is_active);
                		aEmailList.add(map);
                	}
                	for (int j = 0; j < MySingleton.getInstance().getUserBankArray().length(); j++)
                	{                    	
                		JSONObject c = MySingleton.getInstance().getUserBankArray().getJSONObject(j);
                		HashMap<String, String> map = new HashMap<String, String>();
                		String id = c.getString("id");
                		String account_no = c.getString("account_no");
                		String account_bank = c.getString("account_bank");
                		String is_active = c.getString("is_active");
                		map.put("id", id);
                		map.put("account_no", account_no);
                		map.put("account_bank", account_bank);
                		map.put("is_active", is_active);
                		aBankList.add(map);
                	}
        			Log.i("FINISH", "UserGetProfileTask");
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mProfileTask = null;
			try
			{
				showProfileData();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled()
		{
			mProfileTask = null;
		}
	}

	public class UserSetAvatarTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserSetAvatarTask(ProfileActivity a)
	    {
	    	this.activity = a;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String imgString = MySingleton.getInstance().getUserAvatarBASE64();	    	
	    	
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_avatar_new";
	        String postfix2 = "&filename=avatar.jpg";
	        String postfix3 = "&filetype=image/jpg";
	        String postfix4 = "&filebinary=" + imgString;
	        String postfix5 = "&stack=";
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
			
			url_select = url_select + postfix1 + postfix2 + postfix3 + postfix4 + postfix5 + stack;
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
                	Log.i("TASK", "UserSetAvatarTask result OK");                	
                	MySingleton.getInstance().setUserAvatarBASE64(jObject.getJSONObject("avatar").getString("binary"));
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mSetAvatarTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mSetAvatarTask = null;
		}
	}

	public class UserRemoveAvatarTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserRemoveAvatarTask(ProfileActivity a)
	    {
	    	this.activity = a;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_avatar_remove";
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
                	Log.i("TASK", "UserRemoveAvatarTask result OK");                	
                	MySingleton.getInstance().setUserAvatarBASE64("");
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mRemoveAvatarTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mRemoveAvatarTask = null;
		}
	}

	public class UserChangePasswordTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    String sOldPass = "";
	    String sNewPass = "";
	    String sRetryPass = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserChangePasswordTask(ProfileActivity a, String sOldPassword, String sNewPassword, String sNewPasswordRetry)
	    {
	    	this.activity = a;
	    	
	    	sOldPass = sOldPassword;
	    	sNewPass = sNewPassword;
	    	sRetryPass = sNewPasswordRetry;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String old_password = sOldPass;
	    	String new_password = sNewPass;
	    	
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_passwd_change";
	        String postfix2 = "&passwd_old=";
	        String old_password_s = "";
	        String postfix3 = "&passwd_new=";
	        String new_password_s = "";
	        String postfix4 = "&stack=";
	        String stack = "";
	        String postfix5 = "&language=et";

	        // TODO: attempt authentication against a network service.
			try
			{
				stack = MySingleton.getInstance().SHA1(MySingleton.getInstance().getSALT())
						+ MySingleton.getInstance().SHA1(Integer.toString(MySingleton.getInstance().getUserID()));
				
				byte[] byteArrayOP = MySingleton.getInstance().SHA1(old_password).getBytes();
				old_password_s = Base64.encodeToString(byteArrayOP, Base64.NO_WRAP);
				byte[] byteArrayNP = MySingleton.getInstance().SHA1(new_password).getBytes();
				new_password_s = Base64.encodeToString(byteArrayNP, Base64.NO_WRAP);
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
			
			url_select = url_select + postfix1 + postfix2 + old_password_s + postfix3 + new_password_s
					+ postfix4 + stack + postfix5;
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
                	Log.i("TASK", "UserChangePasswordTask result OK");  
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(R.string.prompt_password_changed);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();
               	}
                else
                {
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(R.string.prompt_password_changed_error);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mChangePasswordTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mChangePasswordTask = null;
		}
	}

	public class UserAddEmailTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sEMail = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserAddEmailTask(ProfileActivity a, String sNewEMail)
	    {
	    	this.activity = a;
	    	sEMail = sNewEMail;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_email_new";
	        String postfix2 = "&email=";
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
			
			url_select = url_select + postfix1 + postfix2 + sEMail + postfix3 + stack;
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
                	Log.i("TASK", "UserAddEmailTask result OK");  
            		//MySingleton.getInstance().getProfileActivity().
               	}
                else
                {
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(getResources().getString(R.string.prompt_cannot_add_email) + result);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mAddEmailTask = null;
	        getProfileData();			
		}

		@Override
		protected void onCancelled()
		{
			mAddEmailTask = null;
		}
	}

	public class UserActiveEmailTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sEMail = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserActiveEmailTask(ProfileActivity a, String email)
	    {
	    	this.activity = a;
	    	sEMail = email;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_email_active";
	        String postfix2 = "&email=";
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
			
			url_select = url_select + postfix1 + postfix2 + sEMail + postfix3 + stack;
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
                	Log.i("TASK", "UserActiveEmailTask result OK");
                	MySingleton.getInstance().setUserEmail(sEMail);
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        getProfileData();			
			mActiveEmailTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mActiveEmailTask = null;
		}
	}

	public class UserRemoveEmailTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sEMail = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserRemoveEmailTask(ProfileActivity a, String email)
	    {
	    	this.activity = a;
	    	sEMail = email;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_email_delete";
	        String postfix2 = "&email=";
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
			
			url_select = url_select + postfix1 + postfix2 + sEMail + postfix3 + stack;
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
                	Log.i("TASK", "UserRemoveEmailTask result OK");                	
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        getProfileData();			
			mRemoveEmailTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mRemoveEmailTask = null;
		}
	}

	public class UserAddBankTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sBank = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserAddBankTask(ProfileActivity a, String bank)
	    {
	    	this.activity = a;
	    	sBank = bank;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_bank_new";
	        String postfix2 = "&bank=";
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
			
			url_select = url_select + postfix1 + postfix2 + sBank + postfix3 + stack;
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
                	Log.i("TASK", "UserAddBankTask result OK");                	
               	}
                else
                {
            		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getProfileActivity());
            		dialog.setTitle(R.string.app_name);
            		dialog.setMessage(getResources().getString(R.string.prompt_cannot_add_bank) + result);
            		dialog.setPositiveButton(R.string.dialog_button_ok, null);
            		dialog.show();                	
                }
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
			mAddBankTask = null;
	        getProfileData();			
		}

		@Override
		protected void onCancelled()
		{
			mAddBankTask = null;
		}
	}

	public class UserActiveBankTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sBank = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserActiveBankTask(ProfileActivity a, String bank)
	    {
	    	this.activity = a;
	    	sBank = bank;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_bank_active";
	        String postfix2 = "&bank=";
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
			
			url_select = url_select + postfix1 + postfix2 + sBank + postfix3 + stack;
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
                	Log.i("TASK", "UserActiveBankTask result OK");                	
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        getProfileData();			
			mActiveBankTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mActiveBankTask = null;
		}
	}

	public class UserRemoveBankTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sBank = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserRemoveBankTask(ProfileActivity a, String bank)
	    {
	    	this.activity = a;
	    	sBank = bank;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_bank_delete";
	        String postfix2 = "&bank=";
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
			
			url_select = url_select + postfix1 + postfix2 + sBank + postfix3 + stack;
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
                	Log.i("TASK", "UserRemoveBankTask result OK");                	
               	}
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        getProfileData();			
			mRemoveBankTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mRemoveBankTask = null;
		}
	}

	public class UserUpdateProfileTask extends AsyncTask<Void, Void, Boolean>
	{
	    public ProfileActivity activity;
	    private String sAddress_street = "";
	    private String sAddress_locality = "";
	    private String sAddress_postal = "";
	    private String sAddress_country = "";
	    private String sPhone = "";
	    private String sNewletter_system = "";
	    private String sNewletter_partner = "";

		InputStream inputStream = null;
	    String result = ""; 
	    
	    public UserUpdateProfileTask(ProfileActivity a, String v1, String v2, String v3, String v4, String v5, String v6, String v7)
	    {
	    	this.activity = a;
		    sAddress_street = v1;
		    sAddress_locality = v2;
		    sAddress_postal = v3;
		    sAddress_country = v4;
		    sPhone = v5;
		    sNewletter_system = v6;
		    sNewletter_partner = v7;
	    }

	    @Override
		protected Boolean doInBackground(Void... params)
		{
	    	//String url_encode = "";
	    	String url_select = "http://www.eoksjon24.ee/ea.api?action=operation";
	        String postfix1 = "&activity=account_data_change";
	        String postfix2 = "&address_street=";
	        String postfix3 = "&address_locality=";
	        String postfix4 = "&address_postal=";
	        String postfix5 = "&address_country=";
	        String postfix6 = "&phone=";
	        String postfix7 = "&newletter_system=";
	        String postfix8 = "&newletter_partner=";
	        String postfix9 = "&stack=";
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
					postfix2 + sAddress_street + 
					postfix3 + sAddress_locality + 
					postfix4 + sAddress_postal + 
					postfix5 + sAddress_country + 
					postfix6 + sPhone + 
					postfix7 + sNewletter_system + 
					postfix8 + sNewletter_partner + 
					postfix9 + stack;
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
	        getProfileData();			
	        mUpdateProfileTask = null;
		}

		@Override
		protected void onCancelled()
		{
			mUpdateProfileTask = null;
		}
	}

}
