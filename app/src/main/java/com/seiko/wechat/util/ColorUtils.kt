package com.seiko.wechat.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.AttrRes
import androidx.core.content.res.use

@SuppressLint("Recycle")
fun Context.getAttrColor(@AttrRes attrResId: Int): Int {
    val attrsArray = intArrayOf(attrResId)
    return obtainStyledAttributes(attrsArray).use {
        it.getColor(0, 0xFF0000)
    }
}