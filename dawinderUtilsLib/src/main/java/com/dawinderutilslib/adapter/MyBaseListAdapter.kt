package com.dawinderutilslib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.dawinderutilslib.listeners.OnAdapterItemClick

class MyBaseListAdapter<T>(private val layout: Int, private val listener: OnAdapterItemClick?) :
    ListAdapter<T, MyBaseListHolder<T>>(MyBaseDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBaseListHolder<T> {
        return MyBaseListHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            )
        )
    }

    override fun getItemViewType(position: Int): Int {
        return layout
    }

    override fun onBindViewHolder(holder: MyBaseListHolder<T>, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    private fun onItemClick(position: Int) {
        listener?.onItemClick(position)
    }
}