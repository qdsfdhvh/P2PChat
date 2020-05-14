package com.seiko.wechat.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.databinding.WechatItemPeerBinding
import com.seiko.wechat.util.loadImage

class PeersAdapter(context: Context) : ListAdapter<PeerBean, PeersAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    private val inflater = LayoutInflater.from(context)

    companion object {
        private const val ARGS_LOGO_RES = "ARGS_LOGO_RES"
        private const val ARGS_NAME = "ARGS_NAME"
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PeerBean>() {
            override fun areItemsTheSame(oldItem: PeerBean, newItem: PeerBean): Boolean {
                return oldItem.uuid == newItem.uuid
            }
            override fun areContentsTheSame(oldItem: PeerBean, newItem: PeerBean): Boolean {
                return oldItem.logoResId == newItem.logoResId
                        && oldItem.name == newItem.name
            }

            override fun getChangePayload(oldItem: PeerBean, newItem: PeerBean): Any? {
                val bundle = Bundle()
                if (oldItem.name != newItem.name) {
                    bundle.putString(ARGS_NAME, newItem.name)
                }
                if (oldItem.logoResId != newItem.logoResId) {
                    bundle.putInt(ARGS_LOGO_RES, newItem.logoResId)
                }
                return if (bundle.isEmpty) null else bundle
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = WechatItemPeerBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            holder.bind(position)
        } else {
            val bundle = payloads[0] as? Bundle ?: return
            holder.bind(bundle)
        }
    }

    inner class ItemViewHolder(
        private val binding: WechatItemPeerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)
            binding.wechatName.text = item.name
            binding.wechatLogo.loadImage(item.logoResId)
        }
        fun bind(bundle: Bundle) {
            if (bundle.containsKey(ARGS_NAME)) {
                binding.wechatName.text = bundle.getString(ARGS_NAME)
            }
            if (bundle.containsKey(ARGS_LOGO_RES)) {
                binding.wechatLogo.loadImage(bundle.getInt(ARGS_LOGO_RES))
            }
        }
    }
}