package com.seiko.wechat.ui.widget.helper

import android.app.Activity
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow

private typealias HeightListener = (Int) -> Unit

class HeightProvider(private val activity: Activity): PopupWindow(activity)
    , ViewTreeObserver.OnGlobalLayoutListener {

    private val rootView = View(activity)
    private var listener: HeightListener? = null
    private var maxHeight: Int = 0 // 记录Popup内容区的最大高度
    private var lastKeyboardHeight = -1

    init {
        contentView = rootView

        // 监听全局Layout变化
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)

        // 设置宽度为0, 高度为全屏
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        // 设置键盘弹出方式
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
    }

    fun reset(listener: HeightListener?) {
        if (!isShowing) {
            val view = activity.window.decorView
            view.post {
                showAtLocation(view, Gravity.NO_GRAVITY, 0, 0)
            }
        }
        this.listener = listener
    }

    override fun dismiss() {
        super.dismiss()
        listener = null
        maxHeight = 0
        lastKeyboardHeight = -1
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > maxHeight) {
            maxHeight = rect.bottom
        }

        // 两者的差值就是键盘的高度
        val keyboardHeight = maxHeight - rect.bottom
        if (keyboardHeight != lastKeyboardHeight) {
            listener?.invoke(keyboardHeight)
            lastKeyboardHeight = keyboardHeight
        }
    }

}