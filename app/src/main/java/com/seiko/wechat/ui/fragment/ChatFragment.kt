package com.seiko.wechat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.R
import com.seiko.wechat.data.db.model.MessageData
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.databinding.WechatFragmentChatBinding
import com.seiko.wechat.databinding.WechatViewMoreBinding
import com.seiko.wechat.service.P2pChatService
import com.seiko.wechat.ui.adapter.ChatAdapter
import com.seiko.wechat.ui.widget.helper.ChatTranslationHelper
import com.seiko.wechat.util.bindService
import com.seiko.wechat.util.extension.*
import com.seiko.wechat.util.toast
import com.seiko.wechat.vm.ChatViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment()
    , View.OnClickListener {

    private val args by navArgs<ChatFragmentArgs>()
    private val peer get() = args.peer

    private var _binding: WechatFragmentChatBinding? = null
    private val binding get() = _binding!!
    private val bindingChat get() = binding.wechatViewChat

//    private var _bindingMore: WechatViewMoreBinding? = null
//    private val bindingMore: WechatViewMoreBinding
//        get() {
//            if (_bindingMore == null) {
//                val view = binding.wechatViewMore.inflate()
//                _bindingMore = WechatViewMoreBinding.bind(view)
//            }
//            return _bindingMore!!
//        }
    private val bindingMore get() = binding.wechatViewMore

    private val viewModel: ChatViewModel by viewModel()

    private lateinit var adapter: ChatAdapter
    private lateinit var translationHelper: ChatTranslationHelper

    /**
     * 输入框是否有文本内容
     */
    private var hasText = false
        set(value) {
            if (field != value) {
                field = value
                updateSendVisibility()
            }
        }

    private val actor = lifecycleScope.actor<Boolean>(capacity = Channel.CONFLATED) {
        for (bool in channel) {
            hasText = bool
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window?.let {
            // 软键盘弹出时重绘界面
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WechatFragmentChatBinding.inflate(
            inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        binding.wechatBtnBack.setOnClickListener(this)
        bindingChat.wechatBtnSend.setOnClickListener(this)
        bindingChat.wechatBtnMore.setOnClickListener(this)

        binding.wechatTvTitle.text = peer.name
        // 监听输入框变化
        bindingChat.wechatEtText.addTextChangedListener(afterTextChanged = {
            actor.offer(it?.toString().isNullOrBlank().not())
        })
        // 软键盘弹出，界面重绘时，聊天列表上滑
        binding.wechatList.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (adapter.currentList.isNotEmpty() && bottom < oldBottom) {
                view?.post {
                    binding.wechatList.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        binding.wechatList.setHasFixedSize(true)
        binding.wechatList.layoutManager = LinearLayoutManager(requireActivity())

        adapter = ChatAdapter(requireActivity())
        binding.wechatList.adapter = adapter

        translationHelper = ChatTranslationHelper(binding.container)
    }

    private fun bindViewModel() {
        bindService<P2pChatService, P2pChatService.P2pBinder>()
            .collect(lifecycleScope) { binder ->
                binder.getState()
                    .mapNotNull { it as? P2pChatService.State.PeersChange }
                    .filter { !it.peers.contains(peer) }
                    .collect(lifecycleScope) {
                        requireActivity().onBackPressed()
                        toast("对方关闭了聊天。")
                    }
                binder.connect(peer)
                    .collect(lifecycleScope) { success ->
                        if (success) {
                            toast("${peer.name} 连接成功。")
                        } else {
                            toast("${peer.name} 连接失败。")
                        }
                    }
            }
        viewModel.messageList.observe(viewLifecycleOwner) { list ->
            lifecycleScope.launchWhenResumed {
                delay(200)
                adapter.submitList(list) {
                    binding.wechatList.trySmoothScrollToPosition(list.size - 1)
                }
            }
        }
        viewModel.setPeer(peer)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_btn_back -> onBackPressed()
            R.id.wechat_btn_more -> updateMoreLayoutVisibility()
            R.id.wechat_btn_send -> sendText()
        }
    }

    private fun updateSendVisibility() {
        if (hasText) {
            bindingChat.wechatBtnSend.visibility = View.VISIBLE
        } else {
            bindingChat.wechatBtnSend.visibility = View.GONE
        }
    }

    /**
     * 显示or隐藏 MoreLayout
     */
    private fun updateMoreLayoutVisibility() {
        if (translationHelper.isMoreLayoutShown) {
            translationHelper.hideBottomLayout()
//            bindingMore.root.setGone()
        } else {
            translationHelper.showMoreLayout()
//            bindingMore.root.setVisible()
        }
    }

    /**
     * 发送文本
     */
    private fun sendText() {
        val text = bindingChat.wechatEtText.text.toString()
        if (text.isEmpty()) return
        sendMsg(TextData(text))
        bindingChat.wechatEtText.setText("")
    }

    /**
     * 发送消息
     */
    private fun sendMsg(data: MessageData) {
        P2pChatService.send(requireActivity(), peer, data)
    }
}

/**
 * 如果list视图为空，直接跳转否则使用动画跳转
 */
private fun RecyclerView.trySmoothScrollToPosition(position: Int) {
    if (position < 0) return
    if (childCount == 0) {
        scrollToPosition(position)
    } else {
        smoothScrollToPosition(position)
    }
}