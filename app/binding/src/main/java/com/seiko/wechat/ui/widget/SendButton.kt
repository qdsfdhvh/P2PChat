package com.seiko.wechat.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatButton
import androidx.transition.ChangeBounds
import androidx.transition.ChangeTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet

/**
 * 发送消息按钮
 * Created by 陈健宇 at 2019/5/28
 */
class SendButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    companion object {
        private const val ANIM_TIME = 150L
    }

    private val transitionSet = TransitionSet()

    init {
        transitionSet.duration = ANIM_TIME
        transitionSet.addTransition(ChangeTransform())
            .addTransition(ChangeBounds())
    }

    override fun setVisibility(visibility: Int) {
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transitionSet)
        when(visibility) {
            getVisibility() -> return
            View.VISIBLE -> {
                super.setVisibility(View.VISIBLE)
                this.animate().alpha(1f).scaleX(1f)
                    .setDuration(ANIM_TIME)
                    .start()
            }
            View.GONE -> {
                this.animate().alpha(0f).scaleX(0f)
                    .setDuration(ANIM_TIME)
                    .start()
                postDelayed({
                    super.setVisibility(View.GONE)
                }, ANIM_TIME)
            }
            else -> super.setVisibility(visibility)
        }
    }

}