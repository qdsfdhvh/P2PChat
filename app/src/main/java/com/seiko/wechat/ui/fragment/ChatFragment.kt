package com.seiko.wechat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.wechat.R
import com.seiko.wechat.data.db.model.MessageData
import com.seiko.wechat.data.db.model.TextData
import com.seiko.wechat.databinding.WechatFragmentChat2Binding
import com.seiko.wechat.service.P2pChatService
import com.seiko.wechat.ui.adapter.ChatAdapter
import com.seiko.wechat.ui.widget.helper.HeightProvider
import com.seiko.wechat.util.bindService
import com.seiko.wechat.util.extension.*
import com.seiko.wechat.util.toast
import com.seiko.wechat.vm.ChatViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ChatFragment : Fragment()
    , View.OnClickListener {

    companion object {
        private const val ANIM_TIME = 300L
    }

    private val args by navArgs<ChatFragmentArgs>()
    private val peer get() = args.peer

    private var _binding: WechatFragmentChat2Binding? = null
    private val binding get() = _binding!!
    private val bindingInput get() = binding.wechatViewInput
    private val bindingMore get() = binding.wechatViewMore

    private val viewModel: ChatViewModel by viewModel()

    private lateinit var adapter: ChatAdapter

    private lateinit var heightProvider: HeightProvider

    private val rootHeight get() = binding.root.height
    private val toolbarHeight get() = binding.wechatBtnBack.height
    private val inputHeight get() = binding.wechatViewInput.root.height
    private val listHeight get() = binding.wechatLlyAll.height
    private val listY get() = binding.wechatLlyAll.y

    private var isPanelOpen = false
    private var isSoftOpen = true
    private var softHeight = 0

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
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
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
        _binding = WechatFragmentChat2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        heightProvider.dismiss()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        bindViewModel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.wechatBtnBack.setOnClickListener(this)
        bindingInput.wechatBtnSend.setOnClickListener(this)
        bindingInput.wechatBtnEmoji.setOnClickListener(this)
        bindingInput.wechatBtnMore.setOnClickListener(this)
        bindingInput.wechatEtText.setOnClickListener(this)

        binding.wechatTvTitle.text = peer.name
        // 监听输入框变化
        bindingInput.wechatEtText.addTextChangedListener(afterTextChanged = {
            actor.offer(it?.toString().isNullOrBlank().not())
        })
        // 界面变化是，滑动list
        binding.root.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val position = adapter.itemCount - 1
            if (position >= 0) {
                binding.wechatList.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }

        binding.wechatList.setHasFixedSize(true)
        binding.wechatList.layoutManager = LinearLayoutManager(requireActivity())

        adapter = ChatAdapter(requireActivity())
        binding.wechatList.adapter = adapter

        // 创建一个0宽度的PopWindow通过其高度的变化，确定软键盘的尺寸及其显示or隐藏
        heightProvider = HeightProvider(requireActivity())
        heightProvider.reset { keyboardHeight ->
            Timber.d("键盘高度=$keyboardHeight")

            val space = rootHeight - toolbarHeight - keyboardHeight - inputHeight
            Timber.d("rootHeight=$rootHeight, toolbarHeight=$toolbarHeight, inputHeight=$inputHeight， space=$space")

            var moveAll = 0f
            if (keyboardHeight > 0) {
                isSoftOpen = true
                softHeight = keyboardHeight
                // 判断list是否被遮挡
                Timber.d("listHeight=$listHeight")
                if (listHeight > space) {
                    moveAll = space - listHeight - listY
                }
            } else {
                isSoftOpen = false
                if (listY != 0f) {
                    moveAll = -listY
                }
            }
            Timber.d("移动距离=$moveAll")


            val animationAll = createAnimation(ANIM_TIME, moveAll)
            val move = binding.wechatContent.y + binding.wechatViewFill.height - (rootHeight - keyboardHeight - inputHeight)
            val animation = createAnimation(150, -move)

            Timber.d("onHeightChanged: move=$move, moveAll=$moveAll, space=$space")

            if (!isPanelOpen || keyboardHeight > 0) {
                binding.wechatContent.startAnimation(animation)
                binding.wechatLlyAll.startAnimation(animationAll)
            }

            animation.setAnimationListener {
                if (keyboardHeight > 0) {
                    binding.wechatContent.clearAnimation()
                    binding.wechatContent.y = (rootHeight - keyboardHeight - inputHeight - binding.wechatViewFill.height).toFloat()
                    isPanelOpen = false
                } else if (!isPanelOpen) {
                    binding.wechatContent.clearAnimation()
                    binding.wechatContent.y = bindingMore.root.height.toFloat()
                }
            }

            animationAll.setAnimationListener {
                if (keyboardHeight > 0) {
                    binding.wechatLlyAll.clearAnimation()
                    binding.wechatLlyAll.y += moveAll
                } else if (!isPanelOpen) {
                    binding.wechatLlyAll.clearAnimation()
                    binding.wechatLlyAll.y = 0f

                    val lp = binding.wechatFillAll.layoutParams as LinearLayout.LayoutParams
                    lp.bottomMargin = inputHeight
                    binding.wechatFillAll.layoutParams = lp
                    binding.wechatList.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        binding.wechatList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                closePanelSoft()
            }
            false
        }
        bindingInput.wechatEtText.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val space = rootHeight - toolbarHeight - softHeight - inputHeight
            var moveAll = 0
            if (oldBottom > 0 && listHeight > space) {
                moveAll = oldBottom - bottom
                binding.wechatLlyAll.y += moveAll
            }
            Timber.d("switchPall: moveAll=$moveAll")
        }

        binding.root.post {
            binding.wechatContent.y = bindingMore.root.height.toFloat()
        }
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
                    binding.wechatList.trySmoothScrollToPosition(list.size -1)
//                    binding.root.postDelayed({
//                        var space = 0
//                        val fillSpace = rootHeight - inputHeight - toolbarHeight
//                        if (isSoftOpen) {
//                            space = rootHeight - inputHeight - softHeight - toolbarHeight
//                        } else if (isPanelOpen) {
//                            space = binding.wechatViewFill.height - toolbarHeight
//                        }
//
//                        if (space in 1 until listHeight && listHeight < fillSpace) {
//                            val move = space - listHeight
//                            binding.wechatLlyAll.y = move.toFloat()
//                        }
//                    }, 100)
                }
            }
        }
        viewModel.setPeer(peer)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.wechat_btn_back -> onBackPressed()
            R.id.wechat_btn_more -> {
                isPanelOpen = true
                v.isSelected = !v.isSelected
                if (bindingInput.wechatBtnEmoji.isSelected) {
                    bindingInput.wechatBtnEmoji.isSelected = false
                }
                Timber.d("onClick emoji=${bindingInput.wechatBtnEmoji.isSelected}, more=${bindingInput.wechatBtnMore.isSelected}")
                switchPanel(v)
            }
            R.id.wechat_btn_emoji -> {
                isPanelOpen = true
                v.isSelected = !v.isSelected
                if (bindingInput.wechatBtnMore.isSelected) {
                    bindingInput.wechatBtnMore.isSelected = false
                }
                Timber.d("onClick emoji=${bindingInput.wechatBtnEmoji.isSelected}, more=${bindingInput.wechatBtnMore.isSelected}")
                switchPanel(v)
            }
            R.id.wechat_btn_send -> {
                sendText()
            }
            R.id.wechat_et_text -> {
                bindingInput.wechatBtnEmoji.isSelected = false
                bindingInput.wechatBtnMore.isSelected = false
            }
        }
    }

    /**
     * 发送文本
     */
    private fun sendText() {
        val text = bindingInput.wechatEtText.text.toString()
        if (text.isEmpty()) return
        sendMsg(TextData(text))
        bindingInput.wechatEtText.setText("")
    }

    /**
     * 发送消息
     */
    private fun sendMsg(data: MessageData) {
        P2pChatService.send(requireActivity(), peer, data)
    }

    private fun updateSendVisibility() {
        if (hasText) {
            bindingInput.wechatBtnSend.visibility = View.VISIBLE
        } else {
            bindingInput.wechatBtnSend.visibility = View.GONE
        }
    }

    private fun closePanelSoft() {
        bindingInput.wechatEtText.clearFocus()

        val animation = createAnimation(ANIM_TIME, binding.wechatViewMore.root.height)
        val animationAll = createAnimation(ANIM_TIME, -binding.wechatLlyAll.y)

        if (isPanelOpen) {
            if (isSoftOpen) {
                hideSoftInput(bindingInput.wechatEtText)
            }

            Timber.d("onTouch: 内容开始下降")

            animation.setAnimationListener {
                binding.wechatContent.clearAnimation()
                binding.wechatContent.y = (rootHeight - inputHeight - binding.wechatViewFill.height).toFloat()
                isPanelOpen = false
            }
            animationAll.setAnimationListener {
                binding.wechatLlyAll.clearAnimation()
                binding.wechatLlyAll.y = 0f
                Timber.d("onAnimationEnd: $inputHeight")
                val lp = binding.wechatFillAll.layoutParams as LinearLayout.LayoutParams
                lp.bottomMargin = inputHeight
                binding.wechatFillAll.layoutParams = lp
                binding.wechatList.smoothScrollToPosition(adapter.itemCount - 1)
            }
            binding.wechatContent.startAnimation(animation)
            binding.wechatLlyAll.startAnimation(animationAll)
        } else {
            hideSoftInput(bindingInput.wechatEtText)
        }

        isSoftOpen = false
        bindingInput.wechatBtnEmoji.isSelected = false
        bindingInput.wechatBtnMore.isSelected = false
    }

    private fun switchPanel(view: View) {
        if (view.isSelected) {
            Timber.d("显示面板")
            bindingInput.wechatEtText.clearFocus()

            val animation = createAnimation(ANIM_TIME, -binding.wechatContent.y)

            val space = binding.wechatViewFill.height - toolbarHeight
            var moveAll = 0f
            if (listHeight > space) {
                moveAll = space - listHeight - binding.wechatLlyAll.y
            }
            Timber.d("switchPanel moveAll=$moveAll")
            val animationAll = createAnimation(ANIM_TIME, moveAll)
            if (isSoftOpen) {
                hideSoftInput(bindingInput.wechatEtText)
            }

            animation.setAnimationListener {
                binding.wechatContent.clearAnimation()
                binding.wechatContent.y = 0f
            }
            animationAll.setAnimationListener {
                if (!isSoftOpen) {
                    binding.wechatLlyAll.clearAnimation()
                    binding.wechatLlyAll.y += moveAll
                }
            }
            binding.wechatContent.startAnimation(animation)
            binding.wechatLlyAll.startAnimation(animationAll)
        } else {
            Timber.d("显示键盘")
            showSoftInput(bindingInput.wechatEtText)
        }
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

/**
 * 创建一个上下移动的动画
 */
private fun createAnimation(duration: Long, height: Float): TranslateAnimation {
    return TranslateAnimation(
        TranslateAnimation.ABSOLUTE, 0f,
        TranslateAnimation.ABSOLUTE, 0f,
        TranslateAnimation.ABSOLUTE, 0f,
        TranslateAnimation.ABSOLUTE, height
    ).apply {
        this.duration = duration
        this.fillAfter = true
    }
}

private inline fun createAnimation(duration: Long, height: Int): TranslateAnimation {
    return createAnimation(duration, height.toFloat())
}