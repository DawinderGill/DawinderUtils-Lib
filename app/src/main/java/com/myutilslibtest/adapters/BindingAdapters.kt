package com.myutilslibtest.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.dawinderutilslib.MyUtils
import com.myutilslibtest.R


@Suppress("UNUSED_PARAMETER")
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("setImage")
    fun setImage(view: ImageView, url: String) {
        MyUtils.loadImage(view.context, url, view, R.drawable.ic_placeholder)
    }
}