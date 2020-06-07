package com.seiko.wechat.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(msg: String) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
}