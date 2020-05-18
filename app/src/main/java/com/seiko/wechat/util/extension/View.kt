package com.seiko.wechat.util.extension

import android.view.View

fun View.setVisible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.setInVisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.setGone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}