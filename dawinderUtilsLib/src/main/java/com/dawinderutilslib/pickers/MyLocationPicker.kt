package com.dawinderutilslib.pickers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.R
import com.dawinderutilslib.listeners.OnLocationPick
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

object MyLocationPicker {

    private const val REQUEST_CODE_LOCATION = 8001
    private var locationManager: LocationManager? = null
    private lateinit var listener: OnLocationPick

    fun getCurrentLocation(mContext: Context, listener: OnLocationPick) {
        this.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION
                )
            } else {
                checkLocationSettings(mContext)
            }
        } else {
            checkLocationSettings(mContext)
        }
    }

    fun onRequestPermissionsResult(
        mContext: Context,
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings(mContext)
            } else {
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.location_permission_required)
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationSettings(mContext: Context) {
        locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {
            showSettingPopup(mContext)
        } else {
            getLocation(mContext)
        }
    }

    private fun showSettingPopup(mContext: Context) {
        val mLocationRequest = LocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val client = LocationServices.getSettingsClient(mContext)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            mContext as AppCompatActivity
        ) { getLocation(mContext) }
        task.addOnFailureListener(mContext) { e ->
            when ((e as ApiException).statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED -> try {
                    val resolvable = e as ResolvableApiException
                    resolvable.startResolutionForResult(mContext, REQUEST_CODE_LOCATION)
                } catch (e: Exception) {
                    MyUtils.showToast(
                        mContext,
                        String.format(mContext.getString(R.string.location_error), e.message)
                    )
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    MyUtils.showToast(
                        mContext,
                        mContext.getString(R.string.location_setting_unavailable)
                    )
                }
            }
        }
    }

    fun onActivityResult(mContext: Context, requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                getLocation(mContext)
            } else {
                MyUtils.showToast(mContext, mContext.getString(R.string.turn_on_gps))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(mContext: Context) {
        val loc = locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        if (loc != null) {
            listener.onLocationPick(loc.latitude, loc.longitude)
        } else {
            MyUtils.showToast(mContext, mContext.getString(R.string.failed_to_get_location))
        }
        /*Handler().postDelayed({

        }, 2500)*/
    }
}