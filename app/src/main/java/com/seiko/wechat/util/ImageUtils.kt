package com.seiko.wechat.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target

fun ImageView.loadImage(resId: Int) {
    Glide.with(context)
        .load(resId)
        .override(Target.SIZE_ORIGINAL)
        .into(this)
}