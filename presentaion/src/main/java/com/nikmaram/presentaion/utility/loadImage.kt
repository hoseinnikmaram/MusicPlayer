package com.nikmaram.presentaion.utility

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, data: Any?) {
    when (data) {
        is String -> {
            if (data.isNotEmpty()) {
                Glide.with(view.context)
                    .load(data)
                    .into(view)
            }
        }
        is Bitmap ->{
            Glide.with(view.context)
                .load(data as Bitmap)
                .into(view)
            }
        is Uri -> {
                Glide.with(view.context)
                    .load(data as Uri)
                    .into(view)
        }
    }
}

