/*
 * Roderick Buenviaje
 * April 28, 2013
 * 
 */

package com.rtb.yummm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Reject extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reject, menu);
        return true;
    }

}
