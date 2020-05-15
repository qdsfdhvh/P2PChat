package com.seiko.wechat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.seiko.wechat.R
import com.seiko.wechat.databinding.WechatFragmentChatBinding

class ChatFragment : Fragment()
    , View.OnClickListener {

    private val args by navArgs<ChatFragmentArgs>()

    private var _binding: WechatFragmentChatBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupUI() {
        binding.wechatBtnBack.setOnClickListener(this)
        binding.wechatTvTitle.text = args.peer.name
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_btn_back -> findNavController().popBackStack()

        }
    }
}