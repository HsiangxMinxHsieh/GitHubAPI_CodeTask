package com.timmymike.githubapi_codetask.tools

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.timmymike.githubapi_codetask.BuildConfig
import com.timmymike.githubapi_codetask.R


/**Glide Circle Img setting*/
private val options by lazy {
    RequestOptions()
        .transform(MultiTransformation<Bitmap>(CenterCrop(), CircleCrop()))
        .priority(Priority.NORMAL)
}

@BindingAdapter("app:imageUrl")
fun bindImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context)
        .load(url)
        .apply(options)
        .placeholder(R.drawable.ic_person_outline_black_24dp)
        .into(imageView)
}

interface OnOkInSoftKeyboardListener {
    fun onOkInSoftKeyboard()
}

@BindingAdapter("app:onOkInSoftKeyboard") // I like it to match the listener method name
fun setOnOkInSoftKeyboardListener(
    view: TextView,
    listener: OnOkInSoftKeyboardListener?
) {
    if (listener == null) {
        view.setOnEditorActionListener(null)
    } else {
        view.setOnEditorActionListener { _, _, _ ->
            listener.onOkInSoftKeyboard()
            false
        }
    }
}

fun logi(tag: String, log: Any) {

    if (BuildConfig.DEBUG_MODE) Log.i(tag, log.toString())
}

