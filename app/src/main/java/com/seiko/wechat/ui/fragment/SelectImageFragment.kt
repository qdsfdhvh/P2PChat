package com.seiko.wechat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.seiko.wechat.R
import com.seiko.wechat.databinding.WechatDialogSelectImageBinding
import com.seiko.wechat.ui.adapter.SelectImageAdapter
import com.seiko.wechat.ui.widget.SpaceItemDecoration
import com.seiko.wechat.vm.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectImageFragment : DialogFragment() {

    private var _binding: WechatDialogSelectImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SelectImageAdapter

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WechatDialogSelectImageBinding.inflate(inflater, container, false)
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
        val spacing = resources.getDimensionPixelSize(R.dimen.wechat_log_spacing)
        binding.wechatList.addItemDecoration(SpaceItemDecoration(spacing))

        binding.wechatList.setHasFixedSize(true)
        binding.wechatList.layoutManager = GridLayoutManager(requireActivity(), 3)

        adapter =
            SelectImageAdapter(requireActivity())
        adapter.setOnItemClickListener(object : SelectImageAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                viewModel.selectUserLogo(position)
                dismiss()
            }
        })
        binding.wechatList.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.logoList.observe(viewLifecycleOwner, adapter::submitList)
    }

}