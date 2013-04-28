/*
 * Roderick Buenviaje
 * April 28, 2013
 * 
 */

package com.rtb.yummm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity
{
    String restaurant_name;
    String restaurant_address;
    String restaurant_phone;
    int restaurant_status;
    String restaurant_review;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Show the Up button in the action bar.
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
            restaurant_name = null;
            restaurant_address = null;
            restaurant_phone = null;
            restaurant_status = MainActivity.YummmTypes.RESTAURANTS_PAGE.getVal();
            restaurant_review = null;
        }
        else
        {
            restaurant_name = extras.getString("restaurant_name");
            restaurant_address = extras.getString("restaurant_address");
            restaurant_phone = extras.getString("restaurant_phone");
            restaurant_status = extras.getInt("restaurant_status");
            restaurant_review = extras.getString("restaurant_review").trim();
        }
        
        TextView restName = (TextView) findViewById(R.id.restaurantName);
        TextView restAddress = (TextView) findViewById(R.id.restaurantAddress);
        TextView restPhone = (TextView) findViewById(R.id.restaurantPhone);
        EditText restReview = (EditText) findViewById(R.id.reviewTextBox);
        
        ImageButton thumbsUp = (ImageButton) findViewById(R.id.thumbsUp);
        ImageButton thumbsDown = (ImageButton) findViewById(R.id.thumbsDown);
        ImageButton shareButton = (ImageButton) findViewById(R.id.shareButton);
        
        restName.setText(restaurant_name);
        restAddress.setText(restaurant_address.split(";")[0] + "\n" + restaurant_address.split(";")[1]);
        restPhone.setText(restaurant_phone);
        restReview.setText(restaurant_review);
        
        if (restaurant_status == MainActivity.YummmTypes.YUMMM_PAGE.getVal())
        {
            thumbsUpSelect();
        }
        else if (restaurant_status == MainActivity.YummmTypes.MEH_PAGE.getVal())
        {
            thumbsDownSelect();
        }
        else
        {
            thumbsUp.setBackgroundColor(Color.BLACK);
            thumbsDown.setBackgroundColor(Color.BLACK);
        }
        
        Linkify.addLinks(restPhone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(restAddress, Linkify.MAP_ADDRESSES);
        
        setupActionBar();
        
        thumbsUp.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
                thumbsUpSelect();
                
                EditText restReview = (EditText) findViewById(R.id.reviewTextBox);
                MainActivity mActivity = new MainActivity();
                mActivity.updateRestaurant(
                        restaurant_name,
                        restaurant_address,
                        restaurant_phone,
                        MainActivity.YummmTypes.YUMMM_PAGE.getVal(), 
                        restReview.getText().toString().trim());
                
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "YUMMM...", Toast.LENGTH_SHORT).show();
                DetailActivity.this.finish();
            }
        });
        
        thumbsDown.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
                thumbsDownSelect();
                
                EditText restReview = (EditText) findViewById(R.id.reviewTextBox);
                
                MainActivity mActivity = new MainActivity();
                mActivity.updateRestaurant(
                        restaurant_name,
                        restaurant_address,
                        restaurant_phone,
                        MainActivity.YummmTypes.MEH_PAGE.getVal(),
                        restReview.getText().toString().trim());
                
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "MEH!", Toast.LENGTH_SHORT).show();
                DetailActivity.this.finish();
            }
        });
        
        shareButton.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v)
            {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                if (restaurant_status == MainActivity.YummmTypes.YUMMM_PAGE.getVal())
                {
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check this place out!");
                }
                else if (restaurant_status == MainActivity.YummmTypes.MEH_PAGE.getVal())
                {
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "This place is meh!");
                }
                else
                {
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wanna go?");
                }
                
                String shareText = restaurant_name + "\n" +
                        restaurant_address.split(";")[0] + "\n" + 
                        restaurant_address.split(";")[1] + "\n" +
                        restaurant_phone + "\n";
                
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
                
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }
    
    @Override
    public void onBackPressed() 
    {
        EditText restReview = (EditText) findViewById(R.id.reviewTextBox);
        if (!restReview.getText().toString().trim().isEmpty())
        {
            MainActivity mActivity = new MainActivity();
            mActivity.updateRestaurant(
                    restaurant_name,
                    restaurant_address,
                    restaurant_phone,
                    restaurant_status, 
                    restReview.getText().toString().trim());
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            DetailActivity.this.finish();
        }
        else
        {
            super.onBackPressed();
        }
    }
    
    public void thumbsUpSelect()
    {
        ImageButton thumbsUp = (ImageButton) findViewById(R.id.thumbsUp);
        ImageButton thumbsDown = (ImageButton) findViewById(R.id.thumbsDown);
        
        thumbsUp.setBackgroundColor(Color.BLUE);
        thumbsDown.setBackgroundColor(Color.BLACK);
    }
    
    public void thumbsDownSelect()
    {
        ImageButton thumbsUp = (ImageButton) findViewById(R.id.thumbsUp);
        ImageButton thumbsDown = (ImageButton) findViewById(R.id.thumbsDown);
        
        thumbsUp.setBackgroundColor(Color.BLACK);
        thumbsDown.setBackgroundColor(Color.BLUE);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar()
    {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
