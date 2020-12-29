package com.dawinderutilslib.mapview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.SupportMapFragment
import java.util.*

class MyMapView : SupportMapFragment() {

    private var mListener: OnTouchListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = super.onCreateView(inflater, parent, savedInstanceState)
        val frameLayout = TouchableWrapper(this.requireActivity())
        frameLayout.setBackgroundColor(
            ContextCompat.getColor(
                this.requireActivity(),
                android.R.color.transparent
            )
        )
        (Objects.requireNonNull<View>(layout) as ViewGroup).addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        return layout
    }

    fun setListener(listener: OnTouchListener) {
        mListener = listener
    }

    interface OnTouchListener {
        fun onTouch()
    }

    inner class TouchableWrapper(context: Context) : FrameLayout(context) {

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> mListener!!.onTouch()
            }
            return super.dispatchTouchEvent(event)
        }
    }
}