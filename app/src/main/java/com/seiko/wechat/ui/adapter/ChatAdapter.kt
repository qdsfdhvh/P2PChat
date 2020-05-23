package com.seiko.wechat.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.seiko.wechat.R
import com.seiko.wechat.data.db.model.ImageData
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.databinding.WechatItemChatReceiveImageBinding
import com.seiko.wechat.databinding.WechatItemChatReceiveTextBinding
import com.seiko.wechat.databinding.WechatItemChatSendImageBinding
import com.seiko.wechat.databinding.WechatItemChatSendTextBinding
import com.seiko.wechat.util.annotation.ItemType
import com.seiko.wechat.util.annotation.MessageState
import com.seiko.wechat.util.extension.setGone
import com.seiko.wechat.util.extension.setVisible
import com.seiko.wechat.util.load

private const val CHAT_ARGS_STATE = "CHAT_ARGS_STATE"

class ChatAdapter(context: Context) : ListAdapter<MessageBean, ChatViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageBean>() {
            override fun areItemsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
                return oldItem.state == newItem.state
            }

            override fun getChangePayload(oldItem: MessageBean, newItem: MessageBean): Any? {
                val bundle = Bundle()
                if (oldItem.state != newItem.state) {
                    bundle.putInt(CHAT_ARGS_STATE, newItem.state)
                }
                return if (bundle.isEmpty) null else bundle
            }
        }
    }

    private val inflater = LayoutInflater.from(context)
    private var listener: OnItemClickListener? = null

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val viewHolder = when(viewType) {
            ItemType.SEND_TEXT -> {
                val binding = WechatItemChatSendTextBinding.inflate(inflater, parent, false)
                SendTextViewHolder(binding)
            }
            ItemType.RECEIVE_TEXT -> {
                val binding = WechatItemChatReceiveTextBinding.inflate(inflater, parent, false)
                ReceiveTextViewHolder(binding)
            }
            ItemType.SEND_IMAGE -> {
                val binding = WechatItemChatSendImageBinding.inflate(inflater, parent, false)
                SendImageViewHolder(binding)
            }
            ItemType.RECEIVE_IMAGE -> {
                val binding = WechatItemChatReceiveImageBinding.inflate(inflater, parent, false)
                ReceiveImageViewHolder(binding)
            }
            else -> throw RuntimeException("Unknown viewType = $viewType")
        }
        viewHolder.itemView.setOnClickListener {
            listener?.let {
                val position = viewHolder.layoutPosition
                it.onItemClick(viewHolder, getItem(position))
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            holder.bind(getItem(position))
        } else {
            val bundle = payloads[0] as? Bundle ?: return
            holder.bind(bundle)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(holder: RecyclerView.ViewHolder, item: MessageBean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}

abstract class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(item: MessageBean) {}
    open fun bind(bundle: Bundle) {}
}

/**
 * 发送文本
 */
class SendTextViewHolder(
    private val binding: WechatItemChatSendTextBinding
) : ChatViewHolder(binding.root) {
    override fun bind(item: MessageBean) {
        val data = item.data as TextData
        binding.wechatChatText.text = data.text
        updateState(item.state)
    }

    override fun bind(bundle: Bundle) {
        if (bundle.containsKey(CHAT_ARGS_STATE)) {
            updateState(bundle.getInt(CHAT_ARGS_STATE))
        }
    }

    private fun updateState(@MessageState state: Int) {
        when(state) {
            MessageState.NORMAL -> {
                binding.wechatProgress.setVisible()
                binding.wechatImgError.setGone()
            }
            MessageState.POSTED -> {
                binding.wechatProgress.setGone()
                binding.wechatImgError.setGone()
            }
            MessageState.ERROR -> {
                binding.wechatImgError.setVisible()
                binding.wechatProgress.setGone()
            }
        }
    }
}

/**
 * 接收文本
 */
class ReceiveTextViewHolder(
    private val binding: WechatItemChatReceiveTextBinding
) : ChatViewHolder(binding.root) {
    override fun bind(item: MessageBean) {
        val data = item.data as TextData
        binding.wechatChatText.text = data.text
    }
}

/**
 * 发送图片
 */
class SendImageViewHolder(
    private val binding: WechatItemChatSendImageBinding
) : ChatViewHolder(binding.root) {
    override fun bind(item: MessageBean) {
        val data = item.data as ImageData
        binding.wechatChatImage.load(data.imagePath) {
            centerInside()
            override(400)
            apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
        }
        updateState(item.state)
    }

    override fun bind(bundle: Bundle) {
        if (bundle.containsKey(CHAT_ARGS_STATE)) {
            updateState(bundle.getInt(CHAT_ARGS_STATE))
        }
    }

    private fun updateState(@MessageState state: Int) {
        when(state) {
            MessageState.NORMAL -> {
                binding.wechatProgress.setVisible()
                binding.wechatImgError.setGone()
            }
            MessageState.POSTED -> {
                binding.wechatProgress.setGone()
                binding.wechatImgError.setGone()
            }
            MessageState.ERROR -> {
                binding.wechatImgError.setVisible()
                binding.wechatProgress.setGone()
            }
        }
    }
}

/**
 * 接收图片
 */
class ReceiveImageViewHolder(
    private val binding: WechatItemChatReceiveImageBinding
) : ChatViewHolder(binding.root) {
    override fun bind(item: MessageBean) {
        val data = item.data as ImageData
        binding.wechatChatImage.load(data.imagePath) {
            centerInside()
            override(400)
            apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
        }
    }
}