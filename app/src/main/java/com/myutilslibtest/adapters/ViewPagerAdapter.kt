package com.myutilslibtest.adapters

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.myutilslibtest.databinding.ItemViewpagerBinding

class ViewPagerAdapter(var mContext: Context, var list: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val itemBinding =
            ItemViewpagerBinding.inflate(LayoutInflater.from(mContext), collection, false)
        itemBinding.data = list[position]
        itemBinding.executePendingBindings()
        collection.addView(itemBinding.root, 0)
        return itemBinding.root
    }

    override fun destroyItem(arg0: ViewGroup, arg1: Int, arg2: Any) {
        arg0.removeView(arg2 as View)
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun saveState(): Parcelable? {
        return null
    }
}