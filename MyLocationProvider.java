package com.example.toshi.havadurumuapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;

public class MyLocationProvider implements LocationListener {

	public enum LocationType {
		GPS, NETWORK, BOTH
	}

	public MyLocationListener locationListener;
	private LocationType mLocationType;
	private Context mContext;
	
	private int mGpsTimeout = 60000;
	private LocationManager mGpsLocationManager;
	
	private LocationManager mNetworkLocationManager;
	private int mNetworkTimeout = 60000;

	public MyLocationProvider(AnasayfaMainActivity context, LocationType type) {
		mLocationType = type;
		mContext = context;
	}

	/**
	 * 
	 * @param gpsTimeout
	 *            (millisecond) is a duration that GPS_PROVIDER is going to be
	 *            enabled. Default value is 60000 milliseconds.
	 * @param networkTimeout
	 *            (millisecond) is a duration that NETWORK_PROVIDER is going to
	 *            be enabled. Default value is 60000 milliseconds.
	 */
	public void setTimeout(int gpsTimeout, int networkTimeout) {
		mGpsTimeout = gpsTimeout;
		mNetworkTimeout = networkTimeout;
	}

	/**
	 * 
	 * @param networkTimeout
	 *            (millisecond) is a duration that NETWORK_PROVIDER is going to
	 *            be enabled. Default value is 60000 milliseconds.
	 */
	public void setTimeout(int networkTimeout) {
		mNetworkTimeout = networkTimeout;
	}

	private void startGpsProvider() {
		mGpsLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_LOW);
		crta.setPowerRequirement(Criteria.POWER_HIGH);
		crta.setAltitudeRequired(false);
		crta.setBearingRequired(false);
		crta.setSpeedRequired(false);
		crta.setCostAllowed(true);
		String provider = mGpsLocationManager.getBestProvider(crta, true);
		mGpsLocationManager.requestLocationUpdates(provider, 0, 0, this);
		new CountDownTimer(mGpsTimeout, 1000) {
			@Override
			public void onFinish() {
				mGpsTimeout = 0;
			}
			@Override
			public void onTick(long arg0) {
				mGpsTimeout -= 1000;
			}
		}.start();
	}
	public void stopListeners(){
		mNetworkLocationManager.removeUpdates(this);
		mGpsLocationManager.removeUpdates(this);
	}
	private void startNetworkProvider() {
		mNetworkLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_LOW);
		crta.setAltitudeRequired(false);
		crta.setBearingRequired(false);
		crta.setCostAllowed(true);
		crta.setPowerRequirement(Criteria.POWER_HIGH);
		String provider = mNetworkLocationManager.getBestProvider(crta, true);
		mNetworkLocationManager.requestLocationUpdates(provider, 0, 0, this);
		new CountDownTimer(mNetworkTimeout, 1000) {

			@Override
			public void onFinish() {
				mNetworkTimeout = 0;
			}

			@Override
			public void onTick(long arg0) {
				mNetworkTimeout -= 1000;
			}

		}.start();
	}

	public void getLocation() {
		switch (mLocationType) {
		case GPS:
			startGpsProvider();
			break;
		case NETWORK:
			startNetworkProvider();
			break;
		case BOTH:
			startGpsProvider();
			startNetworkProvider();
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if(mLocationType == LocationType.NETWORK){
			mNetworkLocationManager.removeUpdates(this);
			locationListener.gotLocation(location);
		} else if(mLocationType == LocationType.GPS) {
			mGpsLocationManager.removeUpdates(this);
			locationListener.gotLocation(location);
		} else{
			if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
				mGpsLocationManager.removeUpdates(this);
				mNetworkLocationManager.removeUpdates(this);
				locationListener.gotLocation(location);
			} else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
				if(mGpsTimeout == 0){
					mGpsLocationManager.removeUpdates(this);
					mNetworkLocationManager.removeUpdates(this);
					locationListener.gotLocation(location);
				} else {
					mNetworkLocationManager.removeUpdates(this);
					locationListener.gotLocation(location);
				}
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(provider.equals("network")){
			locationListener.providerDisabled(LocationType.NETWORK);	
		} else if(provider.equals("gps")){
			locationListener.providerDisabled(LocationType.GPS);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
