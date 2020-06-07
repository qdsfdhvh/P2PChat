package com.seiko.wechat.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

fun ImageView.load(@DrawableRes resId: Int, block: RequestBuilder<Drawable>.() -> Unit = {}) {
    Glide.with(context)
        .load(resId)
        .apply(block)
        .into(this)
}

fun ImageView.load(imageUrl: String, block: RequestBuilder<Drawable>.() -> Unit = {}) {
    Glide.with(context)
        .load(imageUrl)
        .apply(block)
        .into(this)
}