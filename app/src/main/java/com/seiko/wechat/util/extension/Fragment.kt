package com.seiko.wechat.util.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * 尝试关闭虚拟键盘
 */
fun Fragment.hideSoftInput() {
    val view = requireActivity().currentFocus ?: requireActivity().window.decorView
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Fragment调用Activity的返回
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.onBackPressed() {
    requireActivity().onBackPressed()
}