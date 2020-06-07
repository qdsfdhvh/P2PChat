package com.seiko.wechat.ui.widget.helper

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

private typealias HeightListener = (Int) -> Unit

class HeightProvider(
    fragment: Fragment
) : PopupWindow(fragment.requireActivity())
    , ViewTreeObserver.OnGlobalLayoutListener
    , LifecycleObserver {

    private var listener: HeightListener? = null
    private var maxHeight: Int = 0 // 记录Popup内容区的最大高度
    private var lastKeyboardHeight = -1
    private val rect = Rect()

    init {
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)

        contentView = View(fragment.requireActivity())

        // 监听全局Layout变化
        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)

        // 设置宽度为0, 高度为全屏
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        // 设置键盘弹出方式
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
    }

    fun show(view: View, listener: HeightListener?) {
        if (!isShowing) {
            view.post {
                showAtLocation(view, Gravity.NO_GRAVITY, 0, 0)
            }
        }
        this.listener = listener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun dismiss() {
        super.dismiss()
        Timber.d("注销dismiss")
        listener = null
        maxHeight = 0
        lastKeyboardHeight = -1
    }

    override fun onGlobalLayout() {
        contentView.getWindowVisibleDisplayFrame(rect)
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