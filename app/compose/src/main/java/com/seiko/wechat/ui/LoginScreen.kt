package com.seiko.wechat.ui

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.tag
import androidx.ui.foundation.*
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.MaterialTheme
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import com.seiko.wechat.R
import com.seiko.wechat.compose.ThemedPreview
import com.seiko.wechat.compose.component.ButtonLightComponent
import com.seiko.wechat.compose.component.DialogListComponent
import com.seiko.wechat.compose.component.TextFieldComponent
import com.seiko.wechat.compose.theme.colorPrimary
import com.seiko.wechat.compose.theme.spaceSize
import com.seiko.wechat.compose.widget.CircleImage
import com.seiko.wechat.vm.LoginViewModel

private object LogoTag
private object LogoTipTag
private object LoginInputTag
private object BtnCenterTag
private object DescriptionTag

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val logoList by viewModel.logoList.observeAsState(emptyList())

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
            .drawBackground(colorPrimary)
            .padding(spaceSize),
        constraintSet = ConstraintSet {
            val inputConstraint = tag(LoginInputTag)
            val btnCenterConstraint = tag(BtnCenterTag)
            inputConstraint.apply {
                centerHorizontally()
                top constrainTo parent.top
                bottom constrainTo btnCenterConstraint.top
            }
            btnCenterConstraint.apply {
                centerHorizontally()
                top constrainTo inputConstraint.bottom
                bottom constrainTo parent.bottom
            }
            val logoConstraint = tag(LogoTag).apply {
                centerHorizontally()
                top constrainTo parent.top
                bottom constrainTo inputConstraint.top
            }
            tag(LogoTipTag).apply {
                constrainVerticallyTo(logoConstraint)
                left constrainTo logoConstraint.right
            }
            tag(DescriptionTag).apply {
                centerHorizontally()
                top constrainTo btnCenterConstraint.bottom
                bottom constrainTo parent.bottom
            }
            createVerticalChain(
                inputConstraint,
                btnCenterConstraint,
                chainStyle = ConstraintSetBuilderScope.ChainStyle.Packed
            )
        }
    ) {

        Text(
            modifier = Modifier.tag(LogoTipTag),
            text = " ← 选择头像",
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.surface)
        )

        var nameState by state { TextFieldValue(text = "") }
        TextFieldComponent(
            modifier = Modifier.tag(LoginInputTag).padding(spaceSize),
            value = nameState,
            onValueChange = { nameState = it },
            hint = "请输入昵称"
        )


        ButtonLightComponent(
            modifier = Modifier.tag(BtnCenterTag).padding(spaceSize),
            onClick = { /* navigator.push(Screen.Home) */ },
            text = "登录"
        )

        Text(
            modifier = Modifier.tag(DescriptionTag),
            text = "P2P聊天",
            style = MaterialTheme.typography.h4.copy(color = MaterialTheme.colors.surface)
        )

        var showPopup by state { false }
        CircleImage(
            modifier = Modifier.tag(LogoTag)
                .clickable(onClick = { showPopup = true }),
            asset = imageResource(R.drawable.wechat_iv_0)
        )

        val onPopupDismissed = { showPopup = false }

        if (showPopup) {
            DialogListComponent(
                title = "选择头像",
                onPopupDismissed = onPopupDismissed,
                data = logoList
            ) { resId ->
                CircleImage(asset = imageResource(id = resId))
            }
        }
    }
}

//@Preview("Light")
//@Composable
//fun LoginScreenPreview() {
//    ThemedPreview {
//        LoginScreen(LoginViewModel())
//    }
//}