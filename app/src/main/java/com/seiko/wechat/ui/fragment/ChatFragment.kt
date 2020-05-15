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
import com.seiko.wechat.R
import com.seiko.wechat.databinding.WechatFragmentChatBinding
import com.seiko.wechat.service.P2pChatService
import com.seiko.wechat.util.bindService
import com.seiko.wechat.util.extension.hideSoftInput
import com.seiko.wechat.vm.ChatViewModel
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
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WechatFragmentChatBinding.inflate(inflater, container, false)
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
        bindingChat.wechatEtText.addTextChangedListener(afterTextChanged = {
            hasText = it?.toString().isNullOrBlank().not()
        })
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
            .flatMapConcat { it.getState() }
            .asLiveData(lifecycleScope.coroutineContext)
            .observe(viewLifecycleOwner) { state ->

            }
        viewModel.messageList.observe(viewLifecycleOwner) { list ->
            lifecycleScope.launchWhenResumed {
                yield()
                adapter.submitList(list) {
                    if (list.isNotEmpty()) {
                        binding.wechatList.smoothScrollToPosition(list.size - 1)
                    }
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

    private fun sendText() {
        val text = bindingChat.wechatEtText.text.toString()
        if (text.isEmpty()) return
        viewModel.sendText(args.peer, text)
        bindingChat.wechatEtText.setText("")
    }
}