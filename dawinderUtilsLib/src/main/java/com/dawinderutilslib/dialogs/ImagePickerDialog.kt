package com.dawinderutilslib.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dawinderutilslib.R
import com.dawinderutilslib.databinding.ItemImagePickerBinding
import com.dawinderutilslib.listeners.OnDialogOptionSelected
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImagePickerDialog : BottomSheetDialogFragment() {

    private lateinit var binding: ItemImagePickerBinding
    private lateinit var mContext: Context
    private lateinit var listener: OnDialogOptionSelected

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_image_picker, container, false)
        binding.tvGallery.setOnClickListener {
            dismiss()
            listener.onDialogOptionSelected(mContext, true)
        }
        binding.tvCamera.setOnClickListener {
            dismiss()
            listener.onDialogOptionSelected(mContext, false)
        }
        return binding.root
    }

    fun setData(mContext: Context, listener: OnDialogOptionSelected) {
        this.mContext = mContext
        this.listener = listener
    }
}