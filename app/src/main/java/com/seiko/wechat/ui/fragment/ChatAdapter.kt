package com.seiko.wechat.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.data.db.model.MessageBean
import com.seiko.wechat.databinding.WechatItemChatTextBinding

class ChatAdapter(context: Context) : ListAdapter<MessageBean, ChatAdapter.ItemViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageBean>() {
            override fun areItemsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageBean, newItem: MessageBean): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = WechatItemChatTextBinding.inflate(inflater, parent, false)
        return SendTextViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    abstract class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(position: Int) {}
    }

    inner class SendTextViewHolder(
        private val binding: WechatItemChatTextBinding
    ) : ItemViewHolder(binding.root) {
        override fun bind(position: Int) {
            val bean = getItem(position)
            binding.wechatChatText.text = bean.data.toString()
//            val item = getItem(position) as SendTextBean
//            binding.wechatChatText.text = item.text
        }
    }
}