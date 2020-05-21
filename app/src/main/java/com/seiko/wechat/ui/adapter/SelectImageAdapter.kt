package com.seiko.wechat.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.data.model.LogoBean
import com.seiko.wechat.databinding.WechatItemUserLogoBinding
import com.seiko.wechat.util.load

class SelectImageAdapter(
    context: Context
) : ListAdapter<LogoBean, SelectImageAdapter.ItemViewHolder>(
    DIFF_CALLBACK
) {

    private val inflater = LayoutInflater.from(context)
    private var listener: OnItemClickListener? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LogoBean>() {
            override fun areItemsTheSame(oldItem: LogoBean, newItem: LogoBean): Boolean {
                return oldItem.resId == newItem.resId
            }
            override fun areContentsTheSame(oldItem: LogoBean, newItem: LogoBean): Boolean {
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = WechatItemUserLogoBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemViewHolder(
        private val binding: WechatItemUserLogoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener?.onClick(layoutPosition)
            }
        }

        fun bind(position: Int) {
            val item = getItem(position)
            binding.wechatLogo.load(item.resId)
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

}