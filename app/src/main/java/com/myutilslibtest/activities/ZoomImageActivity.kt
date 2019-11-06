package com.myutilslibtest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dawinderutilslib.MyUtils
import com.myutilslibtest.R
import com.myutilslibtest.databinding.ActivityZoomImageBinding

class ZoomImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityZoomImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_zoom_image
        )

        val url = "https://futurefive.co.nz/uploads/story/2014/12/05/url.jpg"
        MyUtils.loadImage(this, url, binding.ivZoom, R.drawable.ic_placeholder)
    }
}
