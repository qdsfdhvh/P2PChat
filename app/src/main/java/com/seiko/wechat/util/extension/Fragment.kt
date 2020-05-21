package com.seiko.wechat.util.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * 尝试关闭虚拟键盘
 */
fun Fragment.hideSoftInput() {
    val view = requireActivity().currentFocus ?: requireActivity().window.decorView
    hideSoftInput(view)
}

fun Fragment.hideSoftInput(forceView: View) {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(forceView.windowToken, 0)
}

/**
 * 弹出软键盘
 */
fun Fragment.showSoftInput(view: View) {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    imm.showSoftInput(view, 0)
}

/**
 * Fragment调用Activity的返回
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.onBackPressed() {
    requireActivity().onBackPressed()
}