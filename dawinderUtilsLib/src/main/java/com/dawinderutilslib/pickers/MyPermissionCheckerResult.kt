package com.dawinderutilslib.pickers

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dawinderutilslib.listeners.OnPermissionResult

object MyPermissionCheckerResult {
    private const val REQUEST_CODE_PERMISSION = 7001
    private lateinit var listener: OnPermissionResult

    fun getPermission(
        mContext: Context,
        permissions: Array<String>,
        listener: OnPermissionResult
    ) {
        this.listener = listener
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
                sendPermissionResult(true)
            }
        } else {
            sendPermissionResult(true)
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
        requestCode: Int,
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
                if (isGranted) sendPermissionResult(true)
                else sendPermissionResult(false)
            } else {
                sendPermissionResult(false)
            }
        }
    }

    private fun sendPermissionResult(result: Boolean) {
        listener.onPermissionResult(result)
    }
}