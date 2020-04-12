package com.dawinderutilslib.pageindicator

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dawinderutilslib.R

class MyPageIndicator2(private var mContext: Context) : ViewPager2.OnPageChangeCallback() {

    private lateinit var viewPager: ViewPager2
    private var dotsCount: Int = 0
    private lateinit var llPagerDots: LinearLayout
    private lateinit var dots: Array<ImageView?>
    private var selectedDrawable: Int = R.drawable.default_dot_selected
    private var unselectedDrawable: Int = R.drawable.default_dot_unselected

    /**
     * Initialize page indicator
     *
     * @param viewPager ViewPager with you want to like this page indicator
     * @param llPagerDots LinearLayout in which you want to add this indicator dots
     */
    fun ini(viewPager: ViewPager2, llPagerDots: LinearLayout): MyPageIndicator2 {
        this.viewPager = viewPager
        this.llPagerDots = llPagerDots
        this.dotsCount = viewPager.adapter!!.itemCount
        return this
    }

    /**
     * Here you can set your custom drawable for selected and un selected dots. If you want one custom and one default layout then jus pass 0 in that parameter
     *
     * @param selectedDot Drawable for selected dot
     * @param unselectedDot Drawable for un selected dot
     */
    fun setDrawable(selectedDot: Int, unselectedDot: Int): MyPageIndicator2 {
        this.selectedDrawable =
            if (selectedDot == 0) R.drawable.default_dot_selected else selectedDot
        this.unselectedDrawable =
            if (unselectedDot == 0) R.drawable.default_dot_unselected else unselectedDot
        return this
    }

    /**
     * Build page indicator
     */
    fun build() {
        llPagerDots.removeAllViews()
        viewPager.currentItem = 0
        viewPager.registerOnPageChangeCallback(this)
        dots = arrayOfNulls(dotsCount)
        for (i in 0 until dotsCount) {
            dots[i] = ImageView(mContext)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(mContext, unselectedDrawable))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            llPagerDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(ContextCompat.getDrawable(mContext, selectedDrawable))
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position < dotsCount) {
            for (i in 0 until dotsCount)
                dots[i]?.setImageDrawable(ContextCompat.getDrawable(mContext, unselectedDrawable))

            dots[position]?.setImageDrawable(ContextCompat.getDrawable(mContext, selectedDrawable))
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}