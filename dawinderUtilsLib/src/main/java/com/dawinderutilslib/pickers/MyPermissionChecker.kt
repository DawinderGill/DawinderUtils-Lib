@file:Suppress("UNUSED_PARAMETER")

package com.dawinderutilslib.pickers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.R
import com.dawinderutilslib.listeners.OnPermissionGranted

object MyPermissionChecker {

    private const val REQUEST_CODE_PERMISSION = 9001
    private lateinit var listener: OnPermissionGranted

    fun getPermission(
        mContext: Context,
        permissions: Array<String>,
        listener: OnPermissionGranted
    ) {
        MyPermissionChecker.listener = listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isPermissionsGranted(
                    mContext,
                    permissions
                )
            ) {
                (mContext as AppCompatActivity).requestPermissions(
                    permissions,
                    REQUEST_CODE_PERMISSION
                )
            } else {
                sendPermissionResult()
            }
        } else {
            sendPermissionResult()
        }
    }

    private fun isPermissionsGranted(mContext: Context, permissions: Array<String>): Boolean {
        var isGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isGranted = false
                break
            }
        }
        return isGranted
    }

    fun onRequestPermissionsResult(
        mContext: Context,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty()) {
                var isGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false
                        break
                    }
                }
                if (isGranted)
                    sendPermissionResult()
                else
                    MyUtils.showToast(
                        mContext,
                        mContext.getString(R.string.permission_not_granted_message)
                    )
            } else {
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.permission_not_granted_message)
                )
            }
        }
    }

    private fun sendPermissionResult() {
        listener.onPermissionGranted()
    }
}