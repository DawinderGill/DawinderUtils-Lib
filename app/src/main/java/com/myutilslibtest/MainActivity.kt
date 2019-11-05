package com.myutilslibtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dawinderutilslib.ToastMessage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ToastMessage.showToast(this, "Hello")
    }
}
