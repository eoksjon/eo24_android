package com.eoksjon24;

import com.eoksjon24.ProfileActivity.UserRemoveAvatarTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks 
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static final String TAG = "myLogs";
    private boolean mMainViewActivated;
   
    private Button btnLogin;
    private Button btnNewUserOrProfile;
    private Button btnBrowse;
    private Button btnNewAuction;
    

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MySingleton.getInstance().setMainActivity(this);
        setContentView(R.layout.activity_main);
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        mNavigationDrawerFragment.setDrawerState(false);
        
        // TODO Change to normal call
        mMainViewActivated = true;
    }
    
    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
    	Intent intent;
    	Log.d(TAG, "onNavigationDrawerItemSelected " + Integer.toString(position));
    	// update the main content by replacing fragment      
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
		// TODO Change to normal call
        if (mMainViewActivated)
        {
            switch (position)
            {
                case 1: // Lisa uus ...
    	            intent = new Intent(this.getBaseContext(), NewAuctionActivity.class);
    	            startActivity(intent);
                	break;
                case 2: // Uued teated
    	            intent = new Intent(this.getBaseContext(), InboxActivity.class);
    	            startActivity(intent);
                    break;
                case 3: // Kirjavahetus
    	            Toast.makeText(this.getBaseContext(), "Kirjavahetus", Toast.LENGTH_SHORT).show();				
                    break;
                case 4: // eOksjon24.ee teated
    	            Toast.makeText(this.getBaseContext(), "eOksjon24.ee teated", Toast.LENGTH_SHORT).show();				
                    break;
                case 5: // Konto seaded
    	            intent = new Intent(this.getBaseContext(), ProfileActivity.class);
    	            startActivity(intent);
                    break;
                case 6: // Logi välja
                	MySingleton.getInstance().getMainActivity().LogOut();				
                    break;
            }        	
        }
    }

    public void onSectionAttached(int number)
    {
        switch (number)
        {
            case 1:
                mTitle = getString(R.string.app_name);
                break;
            case 2:
                mTitle = getString(R.string.app_name);
                break;
            case 3:
                mTitle = getString(R.string.app_name);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    
    public void LogOut()
    {
		AlertDialog.Builder dialog = new AlertDialog.Builder(MySingleton.getInstance().getMainActivity());
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(R.string.prompt_logout);
		dialog.setPositiveButton(R.string.dialog_button_confirm, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int id) 
		    {
		    	MySingleton.getInstance().resetUserInfo();
		    	MySingleton.getInstance().getMainActivity().setLoginOnOff();
		    }
		});
		dialog.setNegativeButton(R.string.dialog_button_cancel, null);
		dialog.show();                			

    	
    	
    }
    
    public void setLoginOnOff()
    {
    	
    	boolean logged_on = (MySingleton.getInstance().getUserID() != 0);

       	btnLogin.setText(logged_on ? R.string.main_button1b : R.string.main_button1);
       	btnNewUserOrProfile.setText(logged_on ? R.string.main_button2b : R.string.main_button2);
		btnNewAuction.setVisibility(logged_on ? View.VISIBLE : View.GONE);
       
       	if (mNavigationDrawerFragment != null)
       	{
    		mNavigationDrawerFragment.setDrawerUserInfo();
       		mNavigationDrawerFragment.setDrawerState(logged_on);
       	}    	            
    }
   
    @Override
    public void onPause() 
    {
    	super.onPause();
    	Log.i("INFO", "MainActivity paused");
    }
   
    @Override
    public void onResume() 
    {
    	super.onResume();
    	Log.i("INFO", "MainActivity resumed");
    	MySingleton.getInstance().getMainActivity().setLoginOnOff();
   }
   
    
    
    
    
    
    

    public static class PlaceholderFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            rootView.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_background));
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            
            
            
            
            
            OnClickListener oclBtnLoginOnOff = new OnClickListener()
            {
    			@Override
    			public void onClick(View v)
    			{
    		    	boolean logged_on = (MySingleton.getInstance().getUserID() != 0);
    		    	if (logged_on)
    		    	{
    		    		MySingleton.getInstance().getMainActivity().LogOut();
    		    	}
    		    	else
    		    	{
         	            Intent intent = new Intent(v.getContext(), LoginActivity.class);
        	            startActivity(intent);    		    		
    		    	}
    			}        	
            };
            OnClickListener oclbtnNewUserOrProfile = new OnClickListener()
            {
    			@Override
    			public void onClick(View v)
    			{
    		    	boolean logged_on = (MySingleton.getInstance().getUserID() != 0);
    		    	if (logged_on)
    		    	{
        				Intent intent = new Intent(v.getContext(), ProfileActivity.class);
        	            startActivity(intent);
    		    	}
    		    	else
    		    	{
        	            Intent intent = new Intent(v.getContext(), AuthActivity.class);
        	            startActivity(intent);
    		    	}
    			}        	
            };
            OnClickListener oclBtnBrowse = new OnClickListener()
            {

    			@Override
    			public void onClick(View v)
    			{
     	            Intent intent = new Intent(v.getContext(), BrowseActivity.class);
    	            startActivity(intent);
    			}        	
            };            
            OnClickListener oclBtnNewAuction = new OnClickListener()
            {

    			@Override
    			public void onClick(View v)
    			{
		    		Intent intent = new Intent(v.getContext(), NewAuctionActivity.class);
    	            startActivity(intent);
    			}        	
            };            
           
            
           MySingleton.getInstance().getMainActivity().btnLogin = 
        		   (Button) rootView.findViewById(R.id.btnMain1);
           MySingleton.getInstance().getMainActivity().btnNewUserOrProfile = 
        		   (Button) rootView.findViewById(R.id.btnMain2);
           MySingleton.getInstance().getMainActivity().btnBrowse = 
        		   (Button) rootView.findViewById(R.id.btnMain3);
           MySingleton.getInstance().getMainActivity().btnNewAuction = 
        		   (Button) rootView.findViewById(R.id.btnMain4);
           MySingleton.getInstance().getMainActivity().btnLogin.setOnClickListener(oclBtnLoginOnOff);
           MySingleton.getInstance().getMainActivity().btnNewUserOrProfile.setOnClickListener(oclbtnNewUserOrProfile);
           MySingleton.getInstance().getMainActivity().btnBrowse.setOnClickListener(oclBtnBrowse);
           MySingleton.getInstance().getMainActivity().btnNewAuction.setOnClickListener(oclBtnNewAuction);
           MySingleton.getInstance().getMainActivity().setLoginOnOff();
          
           return rootView;
        }

        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
