package com.eoksjon24;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

// http://eoksjon24.ee/ea.api?action=auction_config&type=english&language=et
// http://eoksjon24.ee/ea.api?action=system_config&language=et


public class MySingleton
{
	private static MySingleton instance;
	   
	private MainActivity         mMainActivity;
	private LoginActivity        mLoginActivity;
	private BrowseActivity       mBrowseActivity;
	private AuthActivity         mAuthActivity;
	private ProfileActivity      mProfileActivity;
	private RegistrationActivity mRegistrationActivity;
	private AuctionActivity      mAuctionActivity;
	private NewAuctionActivity   mNewAuctionActivity;
	private InboxActivity        mInboxActivity;
	
	// Short user info
	private int mUserID = 0;
	private String mUserName = "";
	private String mUserFullName = "";
	private String mUserEmail = "";
	private String mUserLanguage = "";
	private String mUserAvatarBASE64 = "";
	private String mUserRole = "";
	
	// Additional user info
	private String mUserIK = "";
	private String mUserAddressStreet = "";
	private String mUserAddressLocality = "";
	private String mUserAddressPostal = "";
	private String mUserAddressCountry = "";
	private String mUserPhone = "";
	private String mNewletterSystem = "";
	private String mNewletterPartner = "";
	// + email array
	// + bank array
	JSONArray jArrayEmail;
	JSONArray jArrayBank;
	
	private static final String SALT = "eAuction24";
	
	
	
	public static void initInstance()
	{
		if (instance == null)
		{
			// Create the instance
			instance = new MySingleton();
			Log.i("INFO", "Singleton class init done.");
		}
	}
	 
	public static MySingleton getInstance()
	{
		// Return the instance
		return instance;
	}
	   
	private MySingleton()
	{
		// Constructor hidden because this is a singleton
	}
	   

	public void                setMainActivity(MainActivity activity) { mMainActivity = activity; }
	public MainActivity        getMainActivity() { return mMainActivity; }
	public void                setBrowseActivity(BrowseActivity activity) { mBrowseActivity = activity; }
	public BrowseActivity      getBrowseActivity() { return mBrowseActivity; }
	public void                setLoginActivity(LoginActivity activity) {mLoginActivity = activity;}
	public LoginActivity       getLoginActivity() { return mLoginActivity; }
	public void                setAuthActivity(AuthActivity activity) {mAuthActivity = activity;}
	public AuthActivity        getAuthActivity() { return mAuthActivity; }
	public void                setProfileActivity(ProfileActivity activity) {mProfileActivity = activity;}
	public ProfileActivity     getProfileActivity() { return mProfileActivity; }
	public void                setRegistrationActivity(RegistrationActivity activity) { mRegistrationActivity = activity; }
	public RegistrationActivity getRegistrationActivity() { return mRegistrationActivity; }
	public void                setAuctionActivity(AuctionActivity activity) { mAuctionActivity = activity; }
	public AuctionActivity     getAuctionActivity() { return mAuctionActivity; }
	public void                setNewAuctionActivity(NewAuctionActivity activity) { mNewAuctionActivity = activity; }
	public NewAuctionActivity  getNewAuctionActivity() { return mNewAuctionActivity; }
	public void                setInboxActivity(InboxActivity activity) { mInboxActivity = activity; }
	public InboxActivity       getInboxActivity() { return mInboxActivity; }
		
	public void    setUserID(int id) { mUserID = id; }
	public int     getUserID() { return mUserID; }
	public void    setUserName(String str) { mUserName = str; }
	public String  getUserName() { return mUserName; }
	public void    setUserFullName(String str) { mUserFullName = str; }
	public String  getUserFullName() { return mUserFullName; }
	public void    setUserEmail(String str) { mUserEmail = str; }
	public String  getUserEmail() { return mUserEmail; }
	public void    setUserLanguage(String str) { mUserLanguage = str; }
	public String  getUserLanguage() { return mUserLanguage; }
	public void    setUserAvatarBASE64(String str) { mUserAvatarBASE64 = str; }
	public String  getUserAvatarBASE64() { return mUserAvatarBASE64; }
	public void    setUserRole(String str) { mUserRole = str; }
	public String  getUserRole() { return mUserRole; }
	
	public void    setUserIK(String str) { mUserIK = str; }
	public String  getUserIK() { return mUserIK; }
	public void    setUserAddressStreet(String str) { mUserAddressStreet = str; }
	public String  getUserAddressStreet() { return mUserAddressStreet; }
	public void    setUserAddressLocality(String str) { mUserAddressLocality = str; }
	public String  getUserAddressLocality() { return mUserAddressLocality; }
	public void    setUserAddressPostal(String str) { mUserAddressPostal = str; }
	public String  getUserAddressPostal() { return mUserAddressPostal; }
	public void    setUserAddressCountry(String str) { mUserAddressCountry = str; }
	public String  getUserAddressCountry() { return mUserAddressCountry; }
	public void    setUserPhone(String str) { mUserPhone = str; }
	public String  getUserPhone() { return mUserPhone; }
	public void    setNewletterSystem(String str) { mNewletterSystem = str; }
	public String  getNewletterSystem() { return mNewletterSystem; }
	public void    setNewletterPartner(String str) { mNewletterPartner = str; }
	public String  getNewletterPartner() { return mNewletterPartner; }
	
	public void    setUserEmailArray(JSONArray jsa) { jArrayEmail = jsa; }
	public JSONArray  getUserEmailArray() { return jArrayEmail; }
	public String  getUserEmailByIndex(int index) 
	{ 
		try
		{
			return jArrayEmail.getJSONObject(index).getString("email");
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public void    setUserBankArray(JSONArray jsa) { jArrayBank = jsa; }
	public JSONArray  getUserBankArray() { return jArrayBank; }
	public String  getUserBankByIndex(int index) 
	{ 
		try
		{
			return jArrayBank.getJSONObject(index).getString("account_no");
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getSALT() { return SALT; }
	
	public void    resetUserInfo()
	{
		setUserID(0);
		setUserName("");
		setUserFullName("");
		setUserEmail("");
		setUserLanguage("");
		setUserAvatarBASE64("");
		setUserRole("");		

		setUserIK("");		
		setUserAddressStreet("");		
		setUserAddressLocality("");		
		setUserAddressPostal("");		
		setUserAddressCountry("");		
		setUserPhone("");		
		setUserEmailArray(null);		
		setUserBankArray(null);			
	}

	
	public String MD5(String s) 
	{
	    try 
	    {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	        {
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}

	private String convertToHex(byte[] data) 
	{ 
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) 
	    { 
	        int halfbyte = (data[i] >>> 4) & 0x0F;
	        int two_halfs = 0;
	        do 
	        { 
	            if ((0 <= halfbyte) && (halfbyte <= 9)) 
	            {
	                buf.append((char) ('0' + halfbyte));
	            }
	            else 
	            {
	                buf.append((char) ('a' + (halfbyte - 10)));
	            }
	            halfbyte = data[i] & 0x0F;
	        } while(two_halfs++ < 1);
	    } 
	    return buf.toString();
	} 


	public String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  
	{ 
	    MessageDigest md = MessageDigest.getInstance("SHA-1");
	    byte[] sha1hash = new byte[40];
	    md.update(text.getBytes("iso-8859-1"), 0, text.length());
	    sha1hash = md.digest();
	    return convertToHex(sha1hash);
	} 

    public static byte[] encrypt(String text, String keyWord)
    {
        byte[] arr = text.getBytes();
        byte[] keyarr = keyWord.getBytes();
        byte[] result = new byte[arr.length];
        for(int i = 0; i< arr.length; i++)
        {
            result[i] = (byte) (arr[i] ^ keyarr[i % keyarr.length]);
        }
        return result;
    }
    
    public static String decrypt(byte[] text, String keyWord)
    {
        byte[] result  = new byte[text.length];
        byte[] keyarr = keyWord.getBytes();
        for(int i = 0; i < text.length;i++)
        {
            result[i] = (byte) (text[i] ^ keyarr[i% keyarr.length]);
        }
        return new String(result);
    }	
	
	
	public String getTimeLeft(int time_left)
	{
        int day = (int)TimeUnit.SECONDS.toDays(time_left);        
        long hours = TimeUnit.SECONDS.toHours(time_left) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(time_left) - (TimeUnit.SECONDS.toHours(time_left)* 60);
        long second = TimeUnit.SECONDS.toSeconds(time_left) - (TimeUnit.SECONDS.toMinutes(time_left) *60);
        String sDays = day > 0 ? Integer.toString(day) + "p" : "";
        String sHours = hours > 0 ? Long.toString(hours) + "h" : "0h";
        String sMinutes = minute > 0 ? Long.toString(minute) + "min" : "0min";
        String sSeconds = second > 0 ? Long.toString(second) + "s" : "0s";
        String sTimeLeft = "";
        sTimeLeft = minute > 0 ? sMinutes + ", " + sSeconds : sSeconds;
        sTimeLeft = hours > 0 ? sHours + ", " + sMinutes : sTimeLeft;
        sTimeLeft = day > 0 ? sDays + ", " + sHours : sTimeLeft;
		return sTimeLeft;
	}
	
	
	
}









