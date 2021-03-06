package com.app.infideap.whatsappsearchtoolbar;

/**
 * Created by sks on 16/3/18.
 */


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.content.Context;

public class target_activity1 extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private  static GoogleMap mMap;
    boolean isImageFitToScreen;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static  Integer[] XMEN = {R.drawable.kailash, R.drawable.bhoramdeo, R.drawable.barnawapara, R.drawable.girodhpuri, R.drawable.mahant};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private static Context mContext;
    Timer swipeTimer;
    Handler handler;
    Runnable Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);
        //share icon
        ImageView btn_share=(ImageView)findViewById(R.id.imageView);
        btn_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareIt();
            }
        });
        checkPermission();
       // Toast.makeText(this,MainActivity.order.substring(2,MainActivity.order.length()-2), Toast.LENGTH_LONG).show();
        XMEN[0]= MainActivity.RESOURCE.getIdentifier(MainActivity.order.substring(0,MainActivity.order.length()-2)+"_a","drawable",getPackageName());
        XMEN[1]= MainActivity.RESOURCE.getIdentifier(MainActivity.order.substring(0,MainActivity.order.length()-2)+"_b","drawable",getPackageName());
        XMEN[2]= MainActivity.RESOURCE.getIdentifier(MainActivity.order.substring(0,MainActivity.order.length()-2)+"_c","drawable",getPackageName());
        XMEN[3]= MainActivity.RESOURCE.getIdentifier(MainActivity.order.substring(0,MainActivity.order.length()-2)+"_d","drawable",getPackageName());
        XMEN[4]= MainActivity.RESOURCE.getIdentifier(MainActivity.order.substring(0,MainActivity.order.length()-2)+"_e","drawable",getPackageName());


currentPage=0;

        init();
// start latlng
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
//end latlng



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }
    //start latlng
    @Override
    protected void onResume() {
        super.onResume();
        currentPage=0;
        init();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

           //
            // Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        //distance calculation
        Location startpoint = new Location("location A");
        startpoint.setLatitude(currentLatitude);
        startpoint.setLongitude(currentLongitude);

        Location endpoint = new Location("location A");
        endpoint.setLatitude(MainActivity.lt);
        endpoint.setLongitude(MainActivity.ln);

        double distance = startpoint.distanceTo(endpoint);
       // Toast.makeText(this, distance + "", Toast.LENGTH_LONG).show();
        if ((distance / 1000) <50) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            NotificationCompat.Builder mBuilder=(NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.near_me)
                    .setContentTitle("CGTourism")
                    .setContentText("You're near to your point of Interest")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("You're near to your point of Interest"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            // Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//add sound when notification comes in.
            //  mBuilder.setSound(alarmSound);
//add vibration when notification comes in.

            // NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.notify(0,mBuilder.build());
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, mBuilder.build());

        }
    }
    // end latlng
    private void shareIt() {
//sharing implementation here

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plane");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "cgtourism");
        String id=MainActivity.order.trim().substring(2,MainActivity.order.length()-2);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check this place out,it's awesome, you will love it:"+
                getResources().getString(MainActivity.RESOURCE.getIdentifier("l_"+id,"string",MainActivity.PACKAGE_NAME)));
        startActivity(Intent.createChooser(sharingIntent, "Share this place"));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    //
    private void init() {
        XMENArray.clear();
        currentPage=0;
        for (int i = 0; i < XMEN.length; i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyAdapter(target_activity1.this, XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
         handler = new Handler();
         Update = new Runnable() {
            public void run() {
                if (currentPage != XMEN.length) {
                    mPager.setCurrentItem(currentPage++, true);
                }

            }
        };
         swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 6000);


       /* Timer timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }*/


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OneFragment bookingFragment = new OneFragment();
                    return bookingFragment;
                case 1:
                    TwoFragment detailsFragment = new TwoFragment();
                    //detailsFragment.setText("adsfs");
                    return detailsFragment;
                //case 2:
                //ThreeFragment ratingsFragments = new ThreeFragment();
                //return ratingsFragments;
                case 2:
                    FourFragment mapFragments = new FourFragment();
                    return mapFragments;
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "details";
                case 2:
                    return "map";

            }
            return null;
        }
    }
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }


}