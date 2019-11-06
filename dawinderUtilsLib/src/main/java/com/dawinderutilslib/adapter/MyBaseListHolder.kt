package com.dawinderutilslib.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dawinderutilslib.BR

class MyBaseListHolder<T> internal constructor(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    internal fun bind(item: T) {
        binding.setVariable(BR.data, item)
        binding.executePendingBindings()
    }
}