package com.seiko.wechat.compose.component

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextFieldValue
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.material.*
import androidx.ui.tooling.preview.Preview
import com.seiko.wechat.compose.ThemedRawPreview
import com.seiko.wechat.compose.theme.shapes
import com.seiko.wechat.compose.theme.textFieldHeight

/**
 * 目前FilledTextField强制background透明0.12f，所以在外面重绘一层Surface类设置背景
 */
@Composable
fun TextFieldComponent(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(textFieldHeight),
        shape = shapes.medium,
        color = MaterialTheme.colors.surface
    ) {
        FilledTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = hint) },
            backgroundColor = Color.Transparent
        )
    }
}

@Preview
@Composable
fun TextFieldComponentPreview() {
    ThemedRawPreview {
        var valueState by state { TextFieldValue(text = "罗宾") }
        TextFieldComponent(
            value = valueState,
            onValueChange = { valueState = it},
            hint = "请输入昵称"
        )
    }
}