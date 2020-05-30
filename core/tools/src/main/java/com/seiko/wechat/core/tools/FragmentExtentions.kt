package com.seiko.wechat.core.tools

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

fun Fragment.navigateTo(actionId: Int, bundle: Bundle? = null, navOptions: NavOptions? = null) {
    findNavController().navigate(actionId, bundle, navOptions)
}

fun Fragment.navigateTo(navDirections: NavDirections) {
    findNavController().navigate(navDirections.actionId, navDirections.arguments)
}