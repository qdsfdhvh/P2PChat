package com.seiko.wechat.util.extension

import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import timber.log.Timber

/**
 * 尝试关闭虚拟键盘
 */
fun Fragment.hideSoftInput() {
    val view = requireActivity().currentFocus ?: requireActivity().window.decorView
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}