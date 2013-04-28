/*
 * Roderick Buenviaje
 * April 28, 2013
 * 
 */

package com.rtb.yummm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity
{
    private boolean mIsBackButtonPressed;
    private static final int DURATION = 2000; // 2 seconds
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
 
    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // Obtain the sharedPreference, default to true if not available
        boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);
 
        if (isSplashEnabled)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Finish the splash activity so it can't be returned to.
                    SplashActivity.this.finish();
                    if (!mIsBackButtonPressed)
                    {
                        // Create an Intent that will start the main activity.
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                    }
                }
            }, DURATION);
        }
    }
    
    @Override
    public void onBackPressed() 
    {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }
}
