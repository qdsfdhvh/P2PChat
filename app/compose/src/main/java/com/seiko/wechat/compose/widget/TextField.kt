package com.seiko.wechat.compose.widget

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.SoftwareKeyboardController
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.seiko.wechat.R

@Composable
fun HintTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String,
    hintTextColor: Color = Color.LightGray,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unset,
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
    Stack {
        TextField(
            value,
            onValueChange,
            modifier,
            textColor,
            textStyle,
            keyboardType,
            imeAction,
            onFocusChange,
            onImeActionPerformed,
            visualTransformation,
            onTextLayout,
            onTextInputStarted,
            cursorColor
        )
        if (value.text.isEmpty()) {
            Text(
                modifier = Modifier
                    .gravity(Alignment.CenterStart),
                text = hint,
                color = hintTextColor
            )
        }
    }
}

@Composable
fun ImageRoundBoxTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    icon: VectorAsset,
    hint: String = "Enter the field value",
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    iconColor: Color = MaterialTheme.colors.onSurface,
    textColor: Color = MaterialTheme.colors.onSurface,
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
    Box(
        modifier = modifier,
        shape = RoundedCornerShape(roundAngle),
        backgroundColor = backgroundColor,
        border = Border(1.dp, borderColor),
        gravity = ContentGravity.CenterStart
    ) {
        Row(
            verticalGravity = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .preferredWidth(10.dp)
            )
            Icon(
                modifier = Modifier
                    .preferredSize(20.dp),
                asset = icon,
                tint = iconColor
            )
            Spacer(
                modifier = Modifier
                    .preferredWidth(5.dp)
            )
            HintTextField(
                value,
                onValueChange,
                hint,
                hintTextColor,
                Modifier.fillMaxWidth(),
                textColor,
                textStyle,
                keyboardType,
                imeAction,
                onFocusChange,
                onImeActionPerformed,
                visualTransformation,
                onTextLayout,
                onTextInputStarted,
                cursorColor
            )
            Spacer(
                modifier = Modifier
                    .preferredWidth(10.dp)
            )
        }
    }
}

@Preview("TextField")
@Composable
fun HintTextFieldPreview() {
    val valueState = state { TextFieldValue(text = "AAA") }
    HintTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        hint = "this is hint"
    )
}

@Preview("TextFieldHint")
@Composable
fun HintTextFieldHintPreview() {
    val valueState = state { TextFieldValue(text = "") }
    HintTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        hint = "this is hint"
    )
}

@Preview("ImageTextField")
@Composable
fun ImageRoundBoxTextFieldPreview() {
    val valueState = state { TextFieldValue(text = "AAA") }
    ImageRoundBoxTextField(
        modifier = Modifier,
        value = valueState.value,
        onValueChange = { valueState.value = it },
        icon = vectorResource(R.drawable.ic_keyboard_black_24dp),
        hint = "this is hint",
        backgroundColor = Color.White
    )
}

@Preview("ImageTextFieldHint")
@Composable
fun ImageRoundBoxTextFieldHintPreview() {
    val valueState = state { TextFieldValue(text = "") }
    ImageRoundBoxTextField(
        modifier = Modifier,
        value = valueState.value,
        onValueChange = { valueState.value = it },
        icon = vectorResource(R.drawable.ic_keyboard_black_24dp),
        hint = "this is hint",
        backgroundColor = Color.White
    )
}