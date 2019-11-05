package com.dawinderutilslib

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class MyWebViewClient(private val mDialog: ProgressBar) : WebViewClient() {

    init {
        mDialog.visibility = View.VISIBLE
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        mDialog.visibility = View.GONE
    }
}