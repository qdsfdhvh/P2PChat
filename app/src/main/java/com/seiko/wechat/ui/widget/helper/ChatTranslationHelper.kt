package com.seiko.wechat.ui.widget.helper

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.seiko.wechat.R

/**
 * ChatActivity中用于帮助表情布局、更多布局和编辑布局进行平移动画
 * Created by 陈健宇 at 2019/11/14
 */
class ChatTranslationHelper(private val parentLayout: ConstraintLayout) {

    private val moreConstraintSet = ConstraintSet()
    private val resetConstraintSet = ConstraintSet()

    var isMoreLayoutShown: Boolean = false
        private set

    var isEmojiLayoutShown: Boolean = false
        private set

    val isBottomLayoutShow: Boolean
        get() = isMoreLayoutShown || isEmojiLayoutShown

    init {
        createMoreLayoutConstraint()
        createResetConstraint()
    }

    private fun createMoreLayoutConstraint() {
        moreConstraintSet.clear(R.id.wechat_view_chat)
        moreConstraintSet.clear(R.id.wechat_view_more)

        moreConstraintSet.connect(R.id.wechat_view_chat, ConstraintSet.TOP, R.id.wechat_list, ConstraintSet.BOTTOM)
        moreConstraintSet.connect(R.id.wechat_view_chat, ConstraintSet.BOTTOM, R.id.wechat_view_more, ConstraintSet.TOP)
        moreConstraintSet.centerHorizontally(R.id.wechat_view_chat, ConstraintSet.PARENT_ID)
        moreConstraintSet.constrainHeight(R.id.wechat_view_chat, ConstraintSet.WRAP_CONTENT)
        moreConstraintSet.constrainWidth(R.id.wechat_view_chat, ConstraintSet.MATCH_CONSTRAINT)

        moreConstraintSet.connect(R.id.wechat_view_more, ConstraintSet.TOP, R.id.wechat_view_chat, ConstraintSet.BOTTOM)
        moreConstraintSet.connect(R.id.wechat_view_more, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        moreConstraintSet.centerHorizontally(R.id.wechat_view_more, ConstraintSet.PARENT_ID)
        moreConstraintSet.constrainHeight(R.id.wechat_view_more, ConstraintSet.WRAP_CONTENT)
        moreConstraintSet.constrainWidth(R.id.wechat_view_more, ConstraintSet.MATCH_CONSTRAINT)
    }

    private fun createResetConstraint() {
        resetConstraintSet.clear(R.id.wechat_view_chat)
        resetConstraintSet.clear(R.id.wechat_view_more)

        resetConstraintSet.connect(R.id.wechat_view_chat, ConstraintSet.TOP, R.id.wechat_list, ConstraintSet.BOTTOM)
        resetConstraintSet.connect(R.id.wechat_view_chat, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        resetConstraintSet.centerHorizontally(R.id.wechat_view_chat, ConstraintSet.PARENT_ID)
        resetConstraintSet.constrainHeight(R.id.wechat_view_chat, ConstraintSet.WRAP_CONTENT)
        resetConstraintSet.constrainWidth(R.id.wechat_view_chat, ConstraintSet.MATCH_CONSTRAINT)

        resetConstraintSet.connect(R.id.wechat_view_more, ConstraintSet.TOP, R.id.wechat_view_chat, ConstraintSet.BOTTOM)
        resetConstraintSet.centerHorizontally(R.id.wechat_view_more, ConstraintSet.PARENT_ID)
        resetConstraintSet.constrainHeight(R.id.wechat_view_more, ConstraintSet.WRAP_CONTENT)
        resetConstraintSet.constrainWidth(R.id.wechat_view_more, ConstraintSet.MATCH_CONSTRAINT)
    }

    fun showMoreLayout() {
        TransitionManager.beginDelayedTransition(parentLayout)
        moreConstraintSet.applyTo(parentLayout)
        isMoreLayoutShown = true
        isEmojiLayoutShown = false
    }

    fun hideBottomLayout() {
        TransitionManager.beginDelayedTransition(parentLayout)
        resetConstraintSet.applyTo(parentLayout)
        isMoreLayoutShown = false
        isEmojiLayoutShown = false
    }

}