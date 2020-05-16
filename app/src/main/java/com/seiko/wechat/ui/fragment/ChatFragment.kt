package com.seiko.wechat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.R
import com.seiko.wechat.databinding.WechatFragmentChatBinding
import com.seiko.wechat.service.P2pChatService
import com.seiko.wechat.util.bindService
import com.seiko.wechat.util.extension.hideSoftInput
import com.seiko.wechat.util.toast
import com.seiko.wechat.vm.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.yield
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment()
    , View.OnClickListener {

    private val args by navArgs<ChatFragmentArgs>()

    private var _binding: WechatFragmentChatBinding? = null
    private val binding get() = _binding!!
    private val bindingChat get() = binding.wechatViewChat

    private val viewModel: ChatViewModel by viewModel()

    private lateinit var adapter: ChatAdapter

    private var hasText = false
        set(value) {
            if (field != value) {
                field = value
                updateSendVisibility()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

        binding.wechatTvTitle.text = args.peer.name
        // 监听输入框变化
        bindingChat.wechatEtText.addTextChangedListener(afterTextChanged = {
            hasText = it?.toString().isNullOrBlank().not()
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

    }

    private fun bindViewModel() {
        bindService<P2pChatService, P2pChatService.P2pBinder>()
            .flatMapConcat { it.connect(args.peer) }
            .asLiveData(lifecycleScope.coroutineContext)
            .observe(viewLifecycleOwner) { success ->
                if (success) {
                    toast("${args.peer.name} 连接成功。")
                } else {
                    toast("${args.peer.name} 连接失败。")
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
        viewModel.setPeer(args.peer)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_btn_back -> {
                hideSoftInput()
                requireActivity().onBackPressed()
            }
            R.id.wechat_btn_send -> sendText()
        }
    }

    private fun updateSendVisibility() {
        view?.post {
            if (hasText) {
                bindingChat.wechatBtnSend.visibility = View.VISIBLE
                bindingChat.wechatBtnMore.visibility = View.GONE
            } else {
                bindingChat.wechatBtnMore.visibility = View.VISIBLE
                bindingChat.wechatBtnSend.visibility = View.GONE
            }
        }
    }

    /**
     * 发送文本
     */
    private fun sendText() {
        val text = bindingChat.wechatEtText.text.toString()
        if (text.isEmpty()) return
        viewModel.sendText(args.peer, text)
        bindingChat.wechatEtText.setText("")
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
}