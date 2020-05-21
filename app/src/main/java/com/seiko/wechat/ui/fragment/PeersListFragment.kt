package com.seiko.wechat.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.seiko.wechat.R
import com.seiko.wechat.data.model.PeerBean
import com.seiko.wechat.databinding.WechatFragmentPeersListBinding
import com.seiko.wechat.service.P2pChatService
import com.seiko.wechat.ui.adapter.PeersAdapter
import com.seiko.wechat.util.bindService
import com.seiko.wechat.util.extension.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class PeersListFragment : Fragment()
    , View.OnClickListener {

    private val args by navArgs<PeersListFragmentArgs>()

    private var _binding: WechatFragmentPeersListBinding? = null
    private val binding get() = _binding!!

    private var _adapter: PeersAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var popWindow: PeersMenuPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window?.let {
            // 深色字体
            it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            // 透明状态栏
            it.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WechatFragmentPeersListBinding.inflate(inflater, container, false)
        _adapter = PeersAdapter(requireActivity())
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        binding.wechatTvTitle.text = "在线用户"
        binding.wechatBtnMore.setOnClickListener(this)
        binding.wechatBtnExit.setOnClickListener(this)

        popWindow = PeersMenuPopup(requireActivity())
        popWindow.setOnClickListener(this)

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
        binding.wechatList.addItemDecoration(dividerItemDecoration)

        binding.wechatList.setHasFixedSize(true)
        binding.wechatList.layoutManager = LinearLayoutManager(requireActivity())

        adapter.setOnItemClickListener(object : PeersAdapter.OnItemClickListener {
            override fun onClick(peer: PeerBean) {
                findNavController().navigate(PeersListFragmentDirections.wechatActionChat(peer))
            }
        })
        binding.wechatList.adapter = adapter
    }

    private fun bindViewModel() {
        bindService<P2pChatService, P2pChatService.P2pBinder>()
            .flatMapConcat { it.getState() }
            .collect(lifecycleScope) { state ->
                when(state) {
                    is P2pChatService.State.Started -> {
                        Timber.d("P2P聊天已开启")
                        P2pChatService.ready(requireActivity(), args.serviceName)
                    }
                    is P2pChatService.State.Stopped -> {
                        Timber.d("P2P聊天已关闭")
                    }
                    is P2pChatService.State.PeersChange -> {
                        adapter.submitList(state.peers)
                    }
                }
            }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_btn_exit -> {
                findNavController().navigate(R.id.wechat_action_login)
            }
            R.id.wechat_btn_more -> {
                popWindow.showAsDropDown(v)
            }
            R.id.wechat_btn_refresh -> {
                popWindow.dismiss()
                P2pChatService.ready(requireActivity(), args.serviceName)
            }
        }
    }
}