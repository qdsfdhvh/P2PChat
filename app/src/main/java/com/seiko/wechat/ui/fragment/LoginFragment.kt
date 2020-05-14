package com.seiko.wechat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.seiko.wechat.R
import com.seiko.wechat.databinding.WechatFragmentLoginBinding
import com.seiko.wechat.util.extension.hideSoftInput
import com.seiko.wechat.util.loadImage
import com.seiko.wechat.util.toast
import com.seiko.wechat.vm.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment()
    , View.OnClickListener {

    private var _binding: WechatFragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    private val viewModel: LoginViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WechatFragmentLoginBinding.inflate(inflater, container, false)
        _navController = findNavController()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _navController = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        binding.wechatLogo.setOnClickListener(this)
        binding.wechatBtnLogin.setOnClickListener(this)
        binding.wechatEtAccount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftInput()
                navToPeers()
            }
            true
        }
    }

    private fun bindViewModel() {
        viewModel.userLogo.observe(viewLifecycleOwner) { resId ->
            binding.wechatLogo.loadImage(resId)
        }
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.wechatEtAccount.takeIf { it.text.isNullOrEmpty()}
                ?.setText(name)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_logo -> showSelectImageDialog()
            R.id.wechat_btn_login -> navToPeers()
        }
    }

    private fun showSelectImageDialog() {
        navController.navigate(R.id.wechat_action_select_image)
    }

    private fun navToPeers() {
        val name = binding.wechatEtAccount.text.toString()
        if (name.isBlank()) {
            toast("昵称无效。")
            return
        }
        viewModel.saveUserName(name)
        navController.navigate(LoginFragmentDirections.wechatActionPeersList(name))
    }

}