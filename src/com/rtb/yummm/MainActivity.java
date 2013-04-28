/*
 * Roderick Buenviaje
 * April 28, 2013
 * 
 * 
 * This app is meant as a sample app to show various functions and design aspects
 * 
 * It implements sharing and also saves the state of the restaurants through an external file.
 * The list of restaurants are based off of the area and currently use a simple text file right now.
 * If the text file exists, we use the one with modifications, if not, we create a new one if we make any
 * changes to whether we want to dine or not.
 * 
 * Future modifications would be to move the Share button to the top bar in the details window.  Pull the initial list
 * over the internet.  Update the file stored in the /data/data/com.rtb.yummm/files directory with the updated list
 * when turning off the app.  Splash page can get annoying so ability to disable this via settings.
 * Nicer icons
 * 
 * This implementation saves the restaurants that are not "yummm"ed since there is always the possibility the user
 * pressed the thumbs down button by accident, or maybe you like the place once, but then the place becomes horrible
 * due to change of management.  These need to be accounted for.
 * 
 */

package com.rtb.yummm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity
{
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager            mViewPager;
    
    private static String sFile = "restaurantList.in";
    
    // Unparsed Strings
    private static List<String> fullRestaurantList = new ArrayList<String>();
    
    // Parsed lists
    private static List<List> restaurants = new ArrayList<List>();
    private static List<List> mehRestaurants = new ArrayList<List>();
    private static List<List> yummmRestaurants = new ArrayList<List>();
    
    private static int fragPosition = YummmTypes.RESTAURANTS_PAGE.getVal();
    
    // These types allow us to add or remove pages without having to change much code
    // Details can also be modified easily.
    public enum YummmTypes 
    {
        YUMMM_PAGE(0),
        RESTAURANTS_PAGE(1),
        MEH_PAGE(2),
        
        NAME(0),
        ADDRESS(1),
        PHONE(2),
        STATUS(3),
        REVIEW(4),
        LAST_MARKER(5);
        
        private int value;
        
        private YummmTypes(int value)
        {
            this.value = value;
        }
        
        public int getVal()
        {
            return this.value;
        }
    };
    
    @Override
    public void onBackPressed() 
    {
        MainActivity.this.finish();
        super.onBackPressed();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yummm_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(fragPosition);
        
        populateRestaurantLists();
    }
    
    public void populateRestaurantLists()
    {
        try
        {
            BufferedReader reader;
            
            // If the file doesn't exist, that means we haven't made any modifications to the list yet
            // So we load our copy from our assets
            if (!(new File(Environment.getExternalStorageDirectory() + "/" + sFile).exists()))
            {
                reader = new BufferedReader(new InputStreamReader(getAssets().open(sFile)));
            }
            else
            {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + sFile))));
            }
            
            List<String> restaurantDetails = new LinkedList<String>();
            
            String inputLine;
            
            if (reader != null)
            {
                fullRestaurantList.clear();
                restaurants.clear();
                yummmRestaurants.clear();
                mehRestaurants.clear();
                while ((inputLine = reader.readLine()) != null)
                {
                    // Ideally I would search to see that the data is in the proper format before parsing
                    fullRestaurantList.add(inputLine);
                    restaurantDetails = new ArrayList<String>(Arrays.asList(inputLine.split(";;")));
                    
                    // Since we don't always have a review, we will populate an empty string in this case
                    if (restaurantDetails.size() != YummmTypes.LAST_MARKER.getVal())
                    {
                        restaurantDetails.add("");
                    }

                    // Add the restaurant to the proper list
                    if (Integer.parseInt(restaurantDetails.get(YummmTypes.STATUS.getVal())) == YummmTypes.RESTAURANTS_PAGE.getVal())
                    {
                        restaurants.add(restaurantDetails);
                    }
                    else if (Integer.parseInt(restaurantDetails.get(YummmTypes.STATUS.getVal())) == YummmTypes.YUMMM_PAGE.getVal())
                    {
                        yummmRestaurants.add(restaurantDetails);
                    }
                    else // MEH_PAGE
                    {
                        mehRestaurants.add(restaurantDetails);
                    }
                }
                
                reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            // If file doesn't exist, we exit out of our program.
            // File MUST exist right now.  Ideally, we would obtain our list from the net
            System.out.println("Exiting: " + sFile + " does not exist");
            e.printStackTrace();
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // This function is used to update the status of the restaurant and the review as well
    public void updateRestaurant(String name, String address, String phone, int status, String review)
    {
        try
        {
            File sdCard = Environment.getExternalStorageDirectory();
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(sdCard + "/" + sFile, false));

            List<String> restaurantDetails = new ArrayList<String>();
            int restCounter = 0;
            
            for (String rest : fullRestaurantList)
            {
                restaurantDetails = Arrays.asList(rest.split(";;"));
                
                // If the name and address are the same, then we overwrite the previous entry
                // This accounts for restaurants with the same name, but different addresses
                if (restaurantDetails.contains(name) && restaurantDetails.contains(address))
                {
                    // Form new string to overwrite old one
                    rest = name + ";;" + address + ";;" + phone + ";;" + status + ";;" + review;
                    fullRestaurantList.set(restCounter, rest);
                }
                
                out.write(rest + "\n");
                
                restCounter++;
            }

            // Make sure to empty the buffer and close out the file stream
            out.flush();
            out.close();
        }
        catch (java.io.IOException e)
        {
            // do something if an IOException occurs.
        }
        
        // Switch to correct page if there is any change
        fragPosition = status;
    }
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == YummmTypes.YUMMM_PAGE.getVal())
            {
                Fragment fragment = new AcceptFragment();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            }
            else if (position == YummmTypes.MEH_PAGE.getVal())
            {
                Fragment fragment = new RejectFragment();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            }
            else
            {
                Fragment fragment = new RestaurantListFragment();
                Bundle args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            }
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_fragment_accept).toUpperCase(l);
                case 1:
                    return getString(R.string.title_fragment_restaurants).toUpperCase(l);
                case 2:
                    return getString(R.string.title_fragment_reject).toUpperCase(l);
            }
            return null;
        }
    }
    
    // All fragments are basically the same, but work on different lists that contain applicable restaurants
    public static class RestaurantListFragment extends Fragment
    {
        public RestaurantListFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);
            ListView restaurantList = (ListView) rootView.findViewById(R.id.listViewRestaurant);
            
            restaurantList.clearChoices();
            
            ArrayList<String> restaurantNames = new ArrayList<String>();
            for (List rest : restaurants)
            {
                restaurantNames.add(rest.get(YummmTypes.NAME.getVal()).toString());
            }
            
            restaurantList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1 , restaurantNames));
            
            restaurantList.setOnItemClickListener(new OnItemClickListener()
            {
                @Override 
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                {
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("restaurant_name", restaurants.get(position).get(YummmTypes.NAME.getVal()).toString());
                    i.putExtra("restaurant_address", restaurants.get(position).get(YummmTypes.ADDRESS.getVal()).toString());
                    i.putExtra("restaurant_phone", restaurants.get(position).get(YummmTypes.PHONE.getVal()).toString());
                    i.putExtra("restaurant_status", Integer.parseInt(restaurants.get(position).get(YummmTypes.STATUS.getVal()).toString()));
                    i.putExtra("restaurant_review", restaurants.get(position).get(YummmTypes.REVIEW.getVal()).toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                }
            });
            return rootView;
        }
    }
    
    public static class AcceptFragment extends Fragment
    {
        public AcceptFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_accept, container, false);
            ListView restaurantList = (ListView) rootView.findViewById(R.id.listViewAccept);
            
            ArrayList<String> restaurantNames = new ArrayList<String>();
            
            restaurantList.clearChoices();
            
            for (List rest : yummmRestaurants)
            {
                restaurantNames.add(rest.get(YummmTypes.NAME.getVal()).toString());
            }
            
            restaurantList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1 , restaurantNames));
            
            restaurantList.setOnItemClickListener(new OnItemClickListener()
            {
                @Override 
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("restaurant_name", yummmRestaurants.get(position).get(YummmTypes.NAME.getVal()).toString());
                    i.putExtra("restaurant_address", yummmRestaurants.get(position).get(YummmTypes.ADDRESS.getVal()).toString());
                    i.putExtra("restaurant_phone", yummmRestaurants.get(position).get(YummmTypes.PHONE.getVal()).toString());
                    i.putExtra("restaurant_status", Integer.parseInt(yummmRestaurants.get(position).get(YummmTypes.STATUS.getVal()).toString()));
                    i.putExtra("restaurant_review", yummmRestaurants.get(position).get(YummmTypes.REVIEW.getVal()).toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                }
            });
            
            return rootView;
        }
    }

    public static class RejectFragment extends Fragment
    {
        public RejectFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_reject, container, false);
            ListView restaurantList = (ListView) rootView.findViewById(R.id.listViewReject);
            
            restaurantList.clearChoices();
            
            ArrayList<String> restaurantNames = new ArrayList<String>();
            for (List rest : mehRestaurants)
            {
                restaurantNames.add(rest.get(YummmTypes.NAME.getVal()).toString());
            }
            
            restaurantList.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1 , restaurantNames));
            
            restaurantList.setOnItemClickListener(new OnItemClickListener()
            {
                @Override 
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                {
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    i.putExtra("restaurant_name", mehRestaurants.get(position).get(YummmTypes.NAME.getVal()).toString());
                    i.putExtra("restaurant_address", mehRestaurants.get(position).get(YummmTypes.ADDRESS.getVal()).toString());
                    i.putExtra("restaurant_phone", mehRestaurants.get(position).get(YummmTypes.PHONE.getVal()).toString());
                    i.putExtra("restaurant_status", Integer.parseInt(mehRestaurants.get(position).get(YummmTypes.STATUS.getVal()).toString()));
                    i.putExtra("restaurant_review", mehRestaurants.get(position).get(YummmTypes.REVIEW.getVal()).toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    getActivity().finish();
                }
            });
            
            return rootView;
        }
    }
}
