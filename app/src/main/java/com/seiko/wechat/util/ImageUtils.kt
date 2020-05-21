package com.seiko.wechat.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.load(@DrawableRes resId: Int) {
    Glide.with(context)
        .load(resId)
        .into(this)
}