package com.seiko.wechat.util.extension

import android.view.animation.Animation


inline fun Animation.setAnimationListener(
    crossinline onAnimationRepeat: (animation: Animation?) -> Unit = { _ -> },
    crossinline onAnimationStart: (animation: Animation?) -> Unit = { _ -> },
    crossinline onAnimationEnd: (animation: Animation?) -> Unit = { _ -> }
) {
    val listener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            onAnimationRepeat.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            onAnimationStart.invoke(animation)
        }
    }
    setAnimationListener(listener)
}