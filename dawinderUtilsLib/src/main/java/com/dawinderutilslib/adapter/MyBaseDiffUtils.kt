package com.dawinderutilslib.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

@Suppress("WRONG_TYPE_PARAMETER_NULLABILITY_FOR_JAVA_OVERRIDE")
class MyBaseDiffUtils<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}