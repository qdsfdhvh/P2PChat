package com.seiko.wechat.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.seiko.wechat.databinding.WechatPopupPeersMenuBinding

class PeersMenuPopup(context: Context) : PopupWindow(context) {

    private val binding = WechatPopupPeersMenuBinding.inflate(LayoutInflater.from(context))

    init {
        contentView = binding.root
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isTouchable = true
        isFocusable = true
    }

    fun setOnClickListener(listener: View.OnClickListener?) {
        binding.wechatBtnRefresh.setOnClickListener(listener)
    }


}