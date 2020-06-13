package com.seiko.wechat.compose.component

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.SoftwareKeyboardController
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.seiko.wechat.R
import com.seiko.wechat.compose.ThemedPreview
import com.seiko.wechat.compose.ThemedRawPreview
import com.seiko.wechat.compose.theme.textFieldHeight
import com.seiko.wechat.compose.widget.ImageRoundBoxTextField

@Composable
fun IconHintTextFieldComponent(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    icon: VectorAsset,
    hint: String = "Enter the field value",
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    textColor: Color = MaterialTheme.colors.onSurface,
    iconColor: Color = Color.LightGray,
    hintTextColor: Color = Color.LightGray,
    borderColor: Color = Color.Transparent,
    roundAngle: Dp = 10.dp,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocusChange: (Boolean) -> Unit = {},
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    cursorColor: Color = contentColor()
) {
    ImageRoundBoxTextField(
        modifier = modifier.preferredHeight(textFieldHeight),
        value = value,
        onValueChange = onValueChange,
        icon = icon,
        hint = hint,
        backgroundColor = backgroundColor,
        textColor = textColor,
        hintTextColor = hintTextColor,
        iconColor = iconColor,
        borderColor = borderColor,
        roundAngle = roundAngle,
        textStyle = textStyle,
        keyboardType = keyboardType,
        imeAction = imeAction,
        onFocusChange = onFocusChange,
        onImeActionPerformed = onImeActionPerformed,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        onTextInputStarted = onTextInputStarted,
        cursorColor = cursorColor
    )
}

@Preview("Light")
@Composable
fun AppInputFieldPreview() {
    ThemedRawPreview {
        val valueState = state { TextFieldValue(text = "") }
        IconHintTextFieldComponent(
            modifier = Modifier.fillMaxWidth(),
            value = valueState.value,
            onValueChange = { valueState.value = it },
            icon = vectorResource(R.drawable.ic_keyboard_black_24dp),
            hint = "请输入内容"
        )
    }
}

@Preview("Dark")
@Composable
fun AppInputFieldDarkPreview() {
    ThemedRawPreview(darkTheme = true) {
        val valueState = state { TextFieldValue(text = "") }
        IconHintTextFieldComponent(
            modifier = Modifier.fillMaxWidth(),
            value = valueState.value,
            onValueChange = { valueState.value = it },
            icon = vectorResource(R.drawable.ic_keyboard_black_24dp),
            hint = "请输入内容"
        )
    }
}