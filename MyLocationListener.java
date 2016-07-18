package com.example.toshi.havadurumuapp;

import android.location.Location;

import com.example.toshi.havadurumuapp.MyLocationProvider.LocationType;

public interface MyLocationListener {
	void gotLocation(Location location);
	void providerDisabled(LocationType providerType);
}
