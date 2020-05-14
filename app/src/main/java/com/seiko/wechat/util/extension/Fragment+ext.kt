package com.seiko.wechat.util.extension

import android.content.Context
import android.graphics.Rect
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment


/**
 * 尝试关闭虚拟键盘
 */
fun Fragment.hideSoftInput() {
    if (isSoftInputMethodShowing()) {
        val view = requireActivity().window.peekDecorView()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

/**
 * 判断虚拟键盘是否已经弹出
 */
fun Fragment.isSoftInputMethodShowing(): Boolean {
    val act = activity ?: return false

    //获取当前屏幕内容的高度
    val screenHeight = act.window.decorView.height
    //获取View可见区域的bottom
    val rect = Rect()
    act.window.decorView.getWindowVisibleDisplayFrame(rect)

    val value = screenHeight - rect.bottom
    return value > 48
}