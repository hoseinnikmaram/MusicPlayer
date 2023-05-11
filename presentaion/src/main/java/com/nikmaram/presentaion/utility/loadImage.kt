package com.nikmaram.presentaion.utility

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nikmaram.presentaion.R

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

@BindingAdapter("loadImageByPathFile")
fun loadImageByPathFile(view: ImageView, path: String) {
    Glide.with(view.context)
        .load(getImageOfTrackByPath(path))
        .placeholder(R.drawable.ic_round_audiotrack_24)
        .error(R.drawable.ic_round_audiotrack_24)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
        .into(view)
}


